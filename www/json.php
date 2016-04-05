<?php
require_once 'includes/header.php';
?>

<h2>Das JSON-Protokoll des OCS</h2>
<p>Hat sich ein Benutzer erfolgreich eingeloggt, erhält er einen temporären Token. Mit diesem Token kann er sich beim OCS-Server authentifizieren. Dazu muss er eine Verbindung zu <code>ws://picocom.net:34543/websocket</code> aufbauen.<br />
Das Senden des Tokens und Warten auf eine positive Rückmeldung sollte als aller erstes geschehen. Hier nun anhand von Beispielen, wie die JSON-Daten aussehen, die das OCS versteht und verschickt:</p>

<h3>Client zu Server</h3>
<pre>login:
{"key":"f8324/&§fsdbla_irgendwas","type":"login"}

chat:
{"type":"chat", "msg":"asdfsdg", "roomID":1, "me":false}

chatroom
{"type":"chatRoom", "action":"create", "roomName":"testraum", "minPower":0, "pw":""}
(minPower ist der rang der gebraucht wird um den raum betreten zu dürfen)
{"type":"chatRoom", "action":"enter", "roomID":1, "password":"pw"}
{"type":"chatRoom", "action":"leave", "roomID":1}
{"type":"chatRoom", "action":"remove", "roomID":1}

party:
{"type":"party", "action":"create", "rounds":5, "cubeType":"2x2", "name":"asd",
	"ranking":"avg", "mode":"normal"}
{"type":"party", "action":"enter", "partyID":1}
{"type":"party", "action":"leave", "partyID":1}
{"type":"party", "action":"start", "partyID":1}
{"type":"party", "action":"time", "partyID":1, "round":0, "time":1}
{"type":"party", "action":"remove", "partyID":1}

User data:
{"type":"user", "action":"fetch", "userID":1}

whisper:
{"type":"whisper", "action":"start", "userID":1}

sound:
{"type":"sound", "filename":"sound.ogg", "volume":0.5}

heartbeat:
{"type":"heartbeat"}</pre>

<h3>Server zu Client</h3>
<pre>login:
{"name":"Nerogar","login":true,"type":"login"}

userliste:
{"type":"userlist","users":[{"id":1,"rank":"Dev","username":"Nerogar",
	"status":"test status","nameColor":"000000"}]}

User data:
{"type":"user", "action":"data", "userID":1, "user":{"id":1,"rank":"Dev",
	"username":"Nerogar","status":"test status","nameColor":"000000"}}

raumliste:
{"type":"roomlist","rooms":[{"name":"staff","hasPW":false,"id":1,"inRoom":true,
	"userNum":15,"canClose":true}]}

partyliste:
{"parties":[{"id":1,"name":"tesasdasd","cubeType":"3","rounds":5,"inParty":true,
	"currentRound":0,"closed":false,"started":true,"canEdit":true}],"type":"partylist"}

einzelne party daten:
{"id":1,"result":[{"userID":1,"times":[0,0,0,0,0],"avg":65464,"mean":654,"place":2}],
	"ranking":"avg","scramble":"R2 D1 U42","type":"party"}

userliste in einem raum:
{"roomID":1,"type":"roomUserlist","users":[{"id":1}]}

chat:
{"type":"chat", "msg":["asdfsdg","sdg"], "roomID":1, "time":123, "userID":5}
(userID:-1 -> system)

alert:
{"type":"alert", "msg":"asdfsdg", "action":"", "sticky":true}
action types - Alert, Success, Error, Warning, Information



Profil:
{"type":"profile", "action":"get", "username":"nerogar"}

daten:
{"type":"profile", "action":"data", "userData":{userData*} ,"edit":true, "editRank":false}

userData*:    {"times":[{time*},{time*}],"wca":"dfasfasf","rank":"M",
	"status":"ich bin ein status","registerDate":544,"onlineTime":35,
	"loginCount":5,"chatMsgCount":837}
time*:        {"type":"3x3", "timeN":42, "countN":2, "bestN":14, "timeM":42,
	"countM":2, "bestM":648}

ändern:
{"type":"profile", "action":"edit" userData} bei editRank:false -> rank wird ignoriert</pre>

<?php
require_once 'includes/footer.php';
?>