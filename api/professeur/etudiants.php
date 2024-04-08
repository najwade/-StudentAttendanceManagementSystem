<?php 
	declare(strict_types=1);
	namespace Professeur;
	use \Tools\DB;
	use \Tools\U;
	class Etudiants{

		private static function get($groupe_id){
			return Session::do_try(function() use(&$groupe_id){
				return DB::no_trans(function(DB $db) use(&$groupe_id){
					$q="
						select 
							*
						from 
							etudiant 
						where 
							groupe_id=?
					";
					$st=$db->stmt_exec_select($q,[$groupe_id]);
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
				$id=U::assert_g_id();
				return self::get($id);
			});
		}

	}
?>