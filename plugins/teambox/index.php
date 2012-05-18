<?php

global $CFG;
$CFG = parse_ini_file('config/settings.ini');

require_once('rest_lib/TeamBoxRestService.php');
require_once('rest_lib/OpenMeetingsRestService.php');
require_once('oauthLogin.php');

$token = getTeamBoxAccessToken();

$tbService = new TeamBoxRestService($token);
$account = $tbService->getAccountInfo();
$projects = $tbService->getUserProjects();

$omService = new OpenMeetingsRestService();
$logged = $omService->loginAdmin();
if (!$logged) {
    print 'OpenMeetings internal error. Ask your system administrator.';
    exit(0);
}

$invitationsMap = $omService->getInvitationsMap($projects, $account); 

?>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
</head>
<body>
<link rel="stylesheet" type="text/css" href="https://d238xsvyykqg39.cloudfront.net/assets/public-9083c95676119d2654a02c3057084689.css" media="screen" />
<link rel="stylesheet" type="text/css" href="css/index.css" media="screen" />

<div style="text-align: center;">
<?php

if (0 == count($invitationsMap)) {
    echo '<p>You do not participate in any project, so you can not enter any project conference room</p>';
} else {
    echo '<p>Please, choose the project which conference room you want to enter:</p>';
    foreach ($invitationsMap as $project => $url) {
        echo '<p><a class="button button-primary" href="'.$url.'"><span>'.$project.'</span></a></p>';
    }
}

?>
</div>

</body>
</html>
