<?php
/********************************************

EDIT THE THE LINE BELOW WITH THE PROFILE 
FIELD vBULLETIN USES FOR MINECRAFT USERNAMES

To add a custom profile field:
Go to Add New User Profile Field under
User Profile Fields in vBulletin AdminCP

********************************************/

$profileField = "field62";

/********************************************

DO NOT EDIT BELOW THIS LINE!

********************************************/

global $vbulletin;
require_once('./global.php'); //require forum backend


$mcname = mysql_real_escape_string($_GET['user']); //get username trying to join

$sql = "SELECT " . $profileField . " FROM userfield WHERE " . $profileField . " = '" . $mcname . "'";

$result = $db->query_read($sql); //query forums database

if((mysql_num_rows($result) > 0) && ($mcname != "")) //query returns 1 or more results
{
	echo "true"; //minecraft plugin reads this and allows player to join
}
else
{
	echo "false"; //minecraft plugin reads this and stops player from joining
}

?>