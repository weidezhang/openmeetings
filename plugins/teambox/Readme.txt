If you want to set up the TeamBox plugin then you need:
1) Register you application at TeamBox site here:
    https://teambox.com/oauth_clients/
2) Edit the file ./config/settings.ini. Here you need to:
    _ choose a protocol (https or http);
    _ set up TeamBox acceess (app key and secret from the first step);
    _ set up OpenMeetings reas api acceess;
    _ set up OpenMeetings mysql access.
3) Edit the file openmeetings.js:
    replace the string "protocol://host/teambox/index.php?action=authorize" with you protocol and host
4) Visit the TeamBox site enter javascript console if you browser there and enter the string:
    Teambox.models.user.saveSetting('custom_apps', ["protocol://host/teambox_js/openmeetings.js"]);
