<?php 
	namespace Shared;
	use \Tools\U;
	use \Exception;
	class Session{
		public static function do_try($fn){
			return U::do_try(function() use (&$fn){
				U::start_session();
				U::assert(!empty($_SESSION['user']) && is_array($_SESSION['user']),'session not found');
				$user=$_SESSION['user'];
				U::assert(!empty($user['id'] && !empty($user['email'])),'invalid session');
				U::assert($user['type']=='professeur' || $user['type']=='etudiant','invalid session');
				$time=$user['login_time'];
				if (time()-$time>(3600)){
					if ($user['type']=='professeur'){
						include_once '../professeur/compte.php';
						list($ok,$r)=\Professeur\Compte::connect($user['id'],$user['email']);
						if (!$ok) {
							self::unsave();
							throw new Exception($r);
						}
					}else{
						include_once '../etudiant/compte.php';
						list($ok,$r)=\Etudiant\Compte::connect($user['id'],$user['email']);
						if (!$ok) {
							self::unsave();
							throw new Exception($r);
						}
					}
				}
				return $fn($user);
			});
		}

		public static function unsave(){
			U::start_session();
			unset($_SESSION['user']);
		}
	}

?>
