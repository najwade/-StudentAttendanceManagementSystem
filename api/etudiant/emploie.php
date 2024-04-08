<?php 
	declare(strict_types=1);
	namespace Etudiant;
	use \Tools\DB;
	use \Tools\U;
	class Emploie{
		private static function get(){
			return Session::do_try(function($user){
				$etudiant_id=$user['id'];
				return DB::no_trans(function(DB $db) use(&$etudiant_id){
					$q="
						select 
							emploie.id as id,
							emploie.jour as jour,
						    module.module as module,
						    module.id as module_id,
						    professeur.nom_complet as professeur,
						    emploie.debut as debut,
						    emploie.fin as fin
						from 
							emploie
						    inner join professeur on professeur.id=emploie.professeur_id
						    inner join groupe on groupe.id=emploie.groupe_id
						    inner join module on module.id=emploie.module_id
						    inner join etudiant on etudiant.groupe_id=groupe.id
						where 
							etudiant.id=?
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
				return self::get();
			});
		}

	}
?>