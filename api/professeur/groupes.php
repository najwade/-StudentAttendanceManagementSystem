<?php 
	declare(strict_types=1);
	namespace Professeur;
	use Tools\DB;
	use Tools\U;
	class Groupes{

		private static function get(){
			return Session::do_try(function($user){
				$professeur_id=$user['id'];
				return DB::no_trans(function(DB $db) use(&$professeur_id){
					$q="
						select 
							groupe.code as groupe,
							groupe.id as id
						from 
							groupe
							inner join emploie on emploie.groupe_id=groupe.id 
						where 
							emploie.professeur_id=?
						group by
							groupe.code,
							groupe.id
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