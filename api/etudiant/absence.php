<?php 
	declare(strict_types=1);
	namespace Etudiant;
	use Tools\DB;
	use Tools\U;
	class Absence{

		private static function get(){
			return Session::do_try(function($user){
				$etudiant_id=$user['id'];
				return DB::no_trans(function(DB $db) use(&$etudiant_id){
					$q="
						select 
							liste_absence.date as date ,
							emploie.jour as jour,
							professeur.nom_complet as professeur,
							module.module as module,
							emploie.debut as debut,
							emploie.fin as fin 

						from 
							absence
							inner join etudiant on etudiant.id=absence.etudiant_id
							inner join liste_absence on liste_absence.id=absence.liste_absence_id
							inner join emploie on emploie.id=liste_absence.emploie_id
							inner join module on module.id=emploie.module_id
							inner join professeur on professeur.id=emploie.professeur_id
						where 
							etudiant.id=? and 
							absence.statut='absent'
						
					";
					$st=$db->stmt_exec_select($q,[$etudiant_id]);
					$rows=[];
					while ( ($r=$st->fetch(\PDO::FETCH_ASSOC))){
						$rows[]=$r;
					}
					return [true,$rows];
				});
			});
		}
		public static function api(){
			return Session::do_try(function(){	
				$a=U::assert_g_in('action','get');
				return self::get();
			});
		}

	}
?>