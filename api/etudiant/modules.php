<?php 
	declare(strict_types=1);
	namespace Etudiant;
	use Tools\DB;
	use Tools\U;
	class Modules{
		private static function get(){
			return Session::do_try(function($user){
				$professeur_id=$user['id'];
				return DB::no_trans(function(DB $db) use(&$professeur_id){
					$q="
						select 
							module.module as module,
							module.id as id,
							professeur.nom_complet as professeur,
							count(module.id) as seances
						from 
							emploie
							inner join module on module.id=emploie.module_id
						    inner join groupe on groupe.id=emploie.groupe_id
						    inner join professeur on professeur.id=emploie.professeur_id
						    inner join etudiant on etudiant.groupe_id=groupe.id
						where 
							etudiant.id=?
						group by
							module.module,
							module.id,
							professeur.nom_complet
					";
					$st=$db->stmt_exec_select($q,[$professeur_id]);
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