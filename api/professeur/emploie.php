<?php 
	declare(strict_types=1);
	namespace Professeur;
	use \Tools\DB;
	use \Tools\U;
	class Emploie{

		public static function get(){
			return Session::do_try(function($user){
				$professeur_id=$user['id'];
				return DB::no_trans(function(DB $db) use(&$professeur_id){
					$q="
						select 
							emploie.id as id,
							emploie.jour as jour,
						    module.module as module,
						    module.id as module_id,
						    groupe.code as groupe,
						    groupe.id as groupe_id,
						    emploie.debut as debut,
						    emploie.fin as fin
						from 
							emploie
						    inner join professeur on professeur.id=emploie.professeur_id
						    inner join groupe on groupe.id=emploie.groupe_id
						    inner join module on module.id=emploie.module_id
						where 
							professeur.id=?
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