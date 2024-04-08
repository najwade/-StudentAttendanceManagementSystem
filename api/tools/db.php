<?php
	declare(strict_types=1);
	namespace Tools;
	use \Throwable;
	use \PDO;
	class DB extends PDO {
		function __construct()
		{
			parent::__construct("mysql:host=localhost;dbname=gestion_absence","root","",[
				PDO::ERRMODE_EXCEPTION,
				PDO::MYSQL_ATTR_INIT_COMMAND =>"SET NAMES utf8;SET time_zone = '+00:00'"
			]);
		}

		public static function trans($fn){
			$db=null;
			try{
				$db=new DB();
			}catch(Throwable $e){
				return [false,$e->getMessage()];
			}
			try {
				$db->beginTransaction();
				$ret=$fn($db);
				$db->commit();
				return $ret;
			}catch(Throwable $e){
				$db->rollBack();
				return [false,$e->getMessage()];
			}	
		}
		public static function no_trans($fn){

			try {
				$db=new DB();
				$ret=$fn($db);
				return $ret;
			}catch(Throwable $e){
				return [false,$e->getMessage()];
			}	
		}

		function stmt($q,$arr=null){
			$st=$this->prepare($q);
			return $st;
		}
		function stmt_exec_affect($q,$arr=null,$rowCount=1){ // insert update delete
			$st=self::stmt($q,$arr);
			$st->execute($arr);
			if ($rowCount>0)
				U::assert($st->rowCount()==$rowCount,'insert/update/delete failed');
			return $st;
		}
		function stmt_exec_select($q,$arr=null){ 
			$st=$this->prepare($q);
			$st->execute($arr);
			return $st;		
		}
	}

?>