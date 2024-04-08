<?php 
	namespace Professeur;
	use \Tools\U;
	use \Exception;
	class Session{
		public static function do_try($fn){
			return U::do_try(function() use (&$fn){
				U::start_session();
				U::assert(!empty($_SESSION['user']) && is_array($_SESSION['user']),'session not found');
				$user=$_SESSION['user'];
				U::assert(!empty($user['id'] && !empty($user['compte_id']) && !empty($user['email']) ),'invalid session');
				U::assert($user['type']=='professeur','invalid session');
				$time=$user['login_time'];
				if (time()-$time>(3600)){
					include_once './professeur/compte.php';
					list($ok,$r)=Compte::connect($user['id'],$user['compte_id'],$user['email']);
					if (!$ok) {
						\Shared\Session::unsave();
						throw new Exception($r);
					}
				}
				return $fn($user);
			});
		}
		public static function save($id,$compte_id,$email){ // be carful id is blong to user tb and compte id is belong to account
			if (session_status() === PHP_SESSION_NONE)
				session_start();
			$_SESSION['user']=[
				'type'=>'professeur',
				'compte_id'=>$compte_id,
				'id'=>$id,
				'email'=>$email,
				'login_time'=>time()
			];
			return session_id();
		}
	}

?>
