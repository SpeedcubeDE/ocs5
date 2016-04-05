<?php
require_once 'includes/header.php';
?>

<h2>Über das OCS</h2>
<p>Diese Anwendung, welche sie im Moment vor sich haben, ist eine Grunderneuerung des ursprünglichen 1-Mann-Projektes "Online-Cubing-System" (<a href="http://speedcube.de/forum/showthread.php?tid=169" target="_blank">Thread</a>), welches von September 2009 bis Juni 2014 gute Dienste geleistet hatte.<br />
Aufgabe jenes war es, über das Internet das gemeinsame Speedcuben und Chatten von Mitgliedern der Speedcubing (insbesondere Speedcube.de) Community zu ermöglichen. Dieses war serverseitig in PHP realisiert, weshalb es hohe Serverlasten und große Latenzzeiten aufwieß. Mehrere Erneuerungsversuche waren aus technischen Gründen gescheitert, bis schließlich dieses OCS entwickelt wurde.</p>

<p>Dieses OCS basiert auf HTML5-Websockets, weshalb es neben nahezu gar keinen Latenzzeiten eine sehr hohe Performanz und Flexibilität aufweist. Durch die modernen HTML5-Techniken ist es auch ein gutes Stück dynamischer als sein Vorgänger.<br />
Hier nun technische Details für die Nerds unter uns...</p>

<h3>Die Serversoftware</h3>
<p>Der OCS-Server ist ein <a href="http://netty.io/" target="_blank">Netty</a> basierter Java-Webserver mit Anbindung an eine MySQL-Datenbank. Als Scramblegenerator wird <a href="http://www.speedsolving.com/forum/showthread.php?25790-Prisma-Puzzle-Timer" target="_blank">Walter Souzas Prisma Puzzle Timer</a> verwendet.<br />
Der Server wird entwickelt von Justin Tappeser (alias Nerogar)</p>

<h3>Die Clientsoftware</h3>
<p>Der OCS-Client ist im Prinzip nur eine HTML5-Webseite. Verwendung findet vor allem die JavaScript-Bibliothek <a href="http://jquery.com/" target="_blank">jQuery</a> und 2 jQuery Plugins (<a href="http://soapbox.github.io/jQuery-linkify/" target="_blank">linkify</a> und <a href="http://ned.im/noty/" target="_blank">noty</a>).<br />
Etwaige Zusatzseiten oder Skripte (z.B. Login, Registrierung) sind mit PHP realisiert. Der Client wird (wie das alte OCS) entwickelt von Felix König (alias Felk)</p>

<h3>Das Protokoll</h3>
<p>Die Kommunikation zwischen dem Server und dem Client findet über <a href="http://de.wikipedia.org/wiki/JavaScript_Object_Notation" target="_blank">JSON</a> statt. Das war's.<br />
Falls du einen Bot schreiben möchtest, oder es dich einfach nur interessiert, findest du <a href="json.php">hier</a> Beispiele für alle JSON-Befehle, die der Server sendet oder versteht.</p>

<?php
require_once 'includes/footer.php';
?>