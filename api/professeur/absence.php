<?php 
	declare(strict_types=1);
	namespace Professeur;

use DateTime;
use Tools\DB;
	use Tools\U;
	class Absence{
/*

		{
			"jour": "jeudi",
			"module": "Bases de données",
			"module_id": 4,
			"groupe": "D",
			"groupe_id": 4,
			"debut": "10:00:00",
			"fin": "12:00:00"
		}
*/
		private  static $days_fr_en=[
			'lundi'=>'mon',
			'mardi'=>'tue',
			'mercredi'=>'wed',
			'jeudi'=>'thu',
			'vendredi'=>'fri',
			'samedi'=>'sat',
			'dimanche'=>'sun'
		];
		private static $days_en_fr=[
			'mon'=>'lundi',
			'tue'=>'mardi',
			'wed'=>'mercredi',
			'thu'=>'jeudi',
			'fri'=>'vendredi',
			'sat'=>'samedi',
			'sun'=>'dimanche'
		];
		private static $times=[
			'08:00:00',
			'09:00:00',
			'10:00:00',
			'11:00:00',
			'12:00:00',

			'14:00:00',
			'15:00:00',
			'16:00:00',
			'17:00:00',
			'18:00:00',
		];


		private static function cocher_statut($absence_id,$statut){
			return Session::do_try(function($user)use(&$absence_id,&$statut){
				$professeur_id=$user['id'];
				return DB::trans(function(DB $db) use(&$professeur_id,&$absence_id,&$statut){
					$q="update absence set statut=? where id=?";
					$db->stmt_exec_affect($q,[$statut,$absence_id],1);
					return [true];
				});
			});
		}

		private static function _inserer_absence_element(DB &$db,$date,$emploie_id){
			$q="insert into liste_absence (date,emploie_id) values(?,?)";
			$db->stmt_exec_affect($q,[$date,$emploie_id],1);
			$id=$db->lastInsertId();
			$q1="insert into absence (liste_absence_id,etudiant_id) values (?,?)";
			$st_i=$db->prepare($q1);
			$q2="
				select 
					etudiant.id as id
				from 
					emploie 
                    inner join groupe on groupe.id = emploie.groupe_id
                    inner join etudiant on etudiant.groupe_id=emploie.groupe_id
				where 
					emploie.id=?
			";
			$st_s=$db->stmt_exec_select($q2,[$emploie_id]);
			while ( ($r=$st_s->fetch(\PDO::FETCH_OBJ))){
				$st_i->execute([$id,$r->id]);
			}
			return $id;
		}

		private static function liste_absence($date,$emploie_id){
			return Session::do_try(function($user)use(&$date,&$emploie_id){
				$professeur_id=$user['id'];
				return DB::trans(function(DB $db) use(&$professeur_id,&$date,&$emploie_id){
					$q="
						select 
								*
						from 
							liste_absence
						where
							emploie_id=? and 
							date_format(date, '%Y-%m-%d')=?
					";
					$st=$db->stmt_exec_select($q,[$emploie_id,$date]);
					$r=$st->fetch(\PDO::FETCH_OBJ);
					$id=null;
					if (!$r)
						$id=self::_inserer_absence_element($db,$date,$emploie_id);
					else
						$id=$r->id;
					$q1="
						select 
								absence.id as id ,
								etudiant.id as etudiant_id,
								etudiant.nom_complet as nom_complet,
								etudiant.code_apogee as code_apogee,
								absence.statut as statut
						from
							absence
							inner join etudiant on etudiant.id=absence.etudiant_id
						where 
							liste_absence_id=?
					";
					$st2=$db->stmt_exec_select($q1,[$id]);
					$rows=[];
					while ( ($r=$st2->fetch(\PDO::FETCH_ASSOC))){
						$rows[]=$r;
					}
					return [true,$rows];
				});
			});
		}

		private static function liste_seances($date){
			return Session::do_try(function($user)use(&$date){
				$professeur_id=$user['id'];
				return DB::no_trans(function(DB $db) use(&$professeur_id,&$date){
					include_once'./professeur/emploie.php';
					list($ok,$r)=Emploie::get();
					if (!$ok)
						return [$ok,$r];
					$d=\DateTime::createFromFormat('Y-m-d',$date);
					$d->setTimezone(new \DateTimeZone('Africa/Casablanca'));
					$d_en=strtolower($d->format('D'));
					$d_fr=self::$days_en_fr[$d_en];
					$seances=[];
					foreach(self::$times as $tm){
						//$found=false;
						foreach ($r as $seance) {
							if ($seance['jour']==$d_fr && $seance['debut']==$tm){
								$seances[]=[
									'heure'=>$tm,
									'statut'=>true,
									'seance'=>$seance,
									'date'=>$date
								];
								//$found=true;
								break;
							}
						}
						/*if (!$found){
							$seances[]=[
								'heure'=>$tm,
								'statut'=>false,
								'seance'=>null,
								'date'=>$date
							];
						}*/
					}
					return [true,$seances];
				});
			});
		}

		private static function liste_dates(){
			return Session::do_try(function($user){
				$professeur_id=$user['id'];
				return DB::no_trans(function(DB $db) use(&$professeur_id){
					include_once'./professeur/emploie.php';
					list($ok,$r)=Emploie::get();
					if (!$ok)
						return [$ok,$r];

					$dates=[];
					$max_dates=30;
					for ($i=0;$i<$max_dates;$i++){
						$d=(new \DateTime())->setTimestamp(time()-(24*3600*$i));
						$d->setTimezone(new \DateTimeZone('Africa/Casablanca'));
						$d_en=strtolower($d->format('D'));
						$d_fr=self::$days_en_fr[$d_en];
						$found=false;
						foreach ($r as $seance) {
							if ($seance['jour']==$d_fr){
								$dates[]=[
									'statut'=>true,
									'date'=>$d->format('Y-m-d'),
									'jour'=>$d_fr,
									'jour_en'=>$d_en
								];
								$found=true;
								break;
							}
						}
						if (!$found){
							$dates[]=[
								'statut'=>false,
								'date'=>$d->format('Y-m-d'),
								'jour'=>$d_fr,
								'jour_en'=>$d_en
							];
						}
					}
					return [true,$dates];
				});
			});
		}
		public static function api(){
			return Session::do_try(function(){	
				$a=U::assert_g_in('action','liste_dates','liste_seances','liste_absence','cocher_statut');
				if ($a=='liste_dates')
					return self::liste_dates();
				elseif ($a=='liste_seances'){
					U::assert(!empty($_GET['date']));
					$date=$_GET['date'];
					return self::liste_seances($date);
				}elseif ($a=='liste_absence'){
					$emploie_id=U::assert_g_id();
					U::assert(!empty($_GET['date']));
					$date=$_GET['date'];
					return self::liste_absence($date,$emploie_id);
				}else {
					U::assert(!empty($_GET['statut']));
					$statut=$_GET['statut'];
					$absence_id=U::assert_g_id();
					return self::cocher_statut($absence_id,$statut);
				}
			});
		}

	}
?>