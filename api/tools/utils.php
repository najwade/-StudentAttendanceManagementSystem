<?php 
	declare(strict_types=1);
	namespace Tools;
	use \Exception;
	class U{
		public static function message($status,$data=null){
			$m=[
				'status'=>$status,
				'data'=>$data
			];
			header('Content-Type: application/json');
			print(json_encode($m));
		}

		public static function assert($val,$message=null){
			if (!$val)
				throw new Exception( empty($message)?'assertion failed':$message);
			return $val;
		}

		public static function assert_g($a){
			self::assert(!empty($_GET[$a]),"bad request ($a is required)");
			return $_GET[$a];
		}
		public static function assert_p($k){
			self::assert(!empty($_POST[$k]),"$k is required");
			return $_POST[$k];
		}
		private static function _filter_g($k,$f,$options=FILTER_NULL_ON_FAILURE){
			$v=self::assert_g($k);
			$nv=filter_var($v,$f,$options);
			self::assert(!is_null($nv),"$k : invalid");
			return $nv;
		}
		private static function _filter_p($k,$f,$options=FILTER_NULL_ON_FAILURE){
			$v=self::assert_p($k);
			$nv=filter_var($v,$f,$options);
			if ($f==FILTER_VALIDATE_REGEXP)
				$nv=($nv?$v:null);
			self::assert(!is_null($nv),"$k : invalid");
			return $nv;
		}
		private static function _assert_gp_in($arr,$k,...$a){
			self::assert(is_array($arr) && !empty($arr[$k]),"$k is required");
			$v=$arr[$k];
			foreach ($a as $value) {
				if ($value==$v)
					return $v;
			}
			return null;
		}
		public static function assert_g_in($k,...$a){
			$v=self::_assert_gp_in($_GET,$k,...$a);
			U::assert(!is_null($v),'bad request');
			return $v;
		}
		public static function assert_p_in($k,...$a){
			$v=self::_assert_gp_in($_POST,$k,...$a);
			U::assert(!is_null($v),'bad request');
			return $v;
		}


		public static function assert_g_int($k){
			return self::_filter_g($k,FILTER_VALIDATE_INT);
		}
		public static function assert_p_int($k){
			return self::_filter_p($k,FILTER_VALIDATE_INT);
		}
		public static function assert_g_id(){
			return self::_filter_g('id',FILTER_VALIDATE_INT,["options" => ["min_range" => 1]]);
		}
		public static function assert_p_id(){
			return self::_filter_p('id',FILTER_VALIDATE_INT,["options" => ["min_range" => 1]]);
		}
		public static function assert_g_float($k){
			return self::_filter_g($k,FILTER_VALIDATE_FLOAT);
		}
		public static function assert_p_float($k){
			return self::_filter_p($k,FILTER_VALIDATE_FLOAT);
		}
		public static function assert_g_bool($k){
			return (self::_filter_g($k,FILTER_VALIDATE_BOOL)?1:0);
		}
		public static function assert_p_bool($k){
			return (self::_filter_p($k,FILTER_VALIDATE_BOOL)?1:0);
		}

		public static function assert_p_email($k){
			return self::_filter_p($k,FILTER_VALIDATE_EMAIL);
		}
		public static function assert_p_person_name($k){
			return self::_filter_p($k,FILTER_VALIDATE_REGEXP,['options'=>['regexp'=>"/^[A-Za-z ']{1,2}$/"]]);
		}
		public static function assert_p_pass($k){
			return self::_filter_p($k,FILTER_VALIDATE_REGEXP,['options'=>['regexp'=>"/^[A-Za-z0-9_\-']{6,16}$/"]]);
		}


		public static function do_try($fn){
			try{
				return $fn();
			}catch(Exception $e){
				return [false,$e->getMessage()];
			}
		}

		public static function start_session(){
			if (session_status() === PHP_SESSION_NONE)
    			session_start();
		}
	}


	set_error_handler(function($severity, $message, $file, $line){
	    if (!(error_reporting() & $severity)) {
	        // This error code is not included in error_reporting
	        return;
	    }
	    throw new \ErrorException($message, 0, $severity, $file, $line);
	});
	

?>