<?php
	declare(strict_types=1);
	include_once'./tools\utils.php';
	include_once'./tools\db.php';
	include_once'./shared\session.php';
	use \Tools\DB;
	use \Tools\U;

	U::message(...U::do_try(function(){
		$g=&$_GET;
		U::assert(!empty($g['who']) && !empty($g['entry']) && !empty($g['action']),'bad request');
		$who =U::assert_g_in('who','professeur','etudiant');
		$entry=$g['entry'];
		if ($who=='professeur'){
			include './professeur/session.php';
			if ($entry=='compte'){
				include_once('./professeur/compte.php');
				return \Professeur\Compte::api();
			}
			if ($entry=='emploie'){
				include_once('./professeur/emploie.php');
				return \Professeur\Emploie::api();
			}
			if ($entry=='groupes'){
				include_once('./professeur/groupes.php');
				return \Professeur\Groupes::api();
			}
			if ($entry=='modules'){
				include_once('./professeur/modules.php');
				return \Professeur\Modules::api();
			}
			if ($entry=='etudiants'){
				include_once('./professeur/etudiants.php');
				return \Professeur\Etudiants::api();
			}
			if ($entry=='absence'){
				include_once('./professeur/absence.php');
				return \Professeur\Absence::api();
			}
		}else{
			include './etudiant/session.php';
			if ($entry=='compte'){
				include_once('./etudiant/compte.php');
				return \Etudiant\Compte::api();
			}
			if ($entry=='emploie'){
				include_once('./etudiant/emploie.php');
				return \Etudiant\Emploie::api();
			}
			if ($entry=='modules'){
				include_once('./etudiant/modules.php');
				return \Etudiant\Modules::api();
			}
			if ($entry=='absence'){
				include_once('./etudiant/absence.php');
				return \Etudiant\Absence::api();
			}
		}
		return [false,'bad request'];
	}));





?>
