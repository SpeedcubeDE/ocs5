<?php
$ip = @$_REQUEST ['ip'];
$ip = htmlentities($ip);
?>
<!DOCTYPE html>
<html lang="de">
<head>
<title>OCS</title>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=0.7, maximum-scale=0.7, user-scalable=0" />
<!--320-->
<script type="text/javascript">
	var ip = "<?php echo $ip; ?>";
	var token = getCookie("token");
	String.prototype.escapeHTML = function() {
		return this.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
	}
	function getCookie(cname) {
	    var name = cname + "=";
	    var ca = document.cookie.split(';');
	    for(var i=0; i<ca.length; i++) {
	        var c = ca[i];
	        while (c.charAt(0)==' ') c = c.substring(1);
	        if (c.indexOf(name) != -1) return c.substring(name.length,c.length);
	    }
	    return "";
	} 
</script>
<script type="text/javascript" src="scripts/jQuery2.1.0.js"></script>
<script type="text/javascript" src="scripts/jquery.noty.packaged.min.js"></script>
<script type="text/javascript" src="scripts/jquery.linkify.min.js"></script>
<script type="text/javascript" src="scripts/konami.js"></script>
<script type="text/javascript" src="scripts/Tabber.js"></script>
<script type="text/javascript" src="scripts/sParty.js"></script>
<script type="text/javascript" src="scripts/sProfile.js"></script>
<script type="text/javascript" src="scripts/sChatroom.js"></script>
<script type="text/javascript" src="scripts/sChat.js"></script>
<script type="text/javascript" src="scripts/sUserlist.js"></script>
<script type="text/javascript" src="scripts/Timer.js"></script>
<script type="text/javascript" src="scripts/StackmatReader.js"></script>
<script type="text/javascript" src="scripts/main.js"></script>
<link rel="stylesheet" href="style.css">
<link rel="shortcut icon" type="image/ico" href="favicon.ico" />
</head>

<body>
	<div style="display: none;" id="additionalCSS"></div>
	<div id="container">
		<ul id="tabs"></ul>
		<div id="partyoverlay">
			<div style="float: right;">
				<button class="big" onclick="handler.party.selectParty(-1);">Schließen</button>
			</div>
			<div id="partydata">
				<h1>THIS IS AN ERROR! YOU ARE NOT SUPPOSED TO SEE THIS!<br />
				PLEASE SEND AN ADMIN THE CONTENT OF THE JAVASCRIPT CONSOLE<br />
				FIREFOX: Ctrl+Shift+K, CHROME: Ctrl+Shift+J</h1>
			</div>
		</div>
		<div id="profileoverlay">
			<div style="float: right;">
				<button class="big" onclick="handler.userlist.whisper(handler.profile.getSelectedProfile().userData.userID);">Privatchat</button>
				<button class="big" onclick="handler.profile.selectProfile(-1);">Schließen</button>
			</div>
			<div id="profile">
				<!-- context handled by javascript -->
			</div>
		</div>
		<div id="timer">
			<a href="javascript:timer.reset()" style="margin-right: 70px;">Zurücksetzen</a> <a href="javascript:void(0);" id="stackmatToggle">Stackmat verwenden</a> <a href="javascript:void(0);" id="stackmatGenToggle" style="margin-right: 70px;">(gen2)</a> <a href="javascript:timer.close()">Schließen</a>
			<div class="ttime">00.00</div>
			<div class="scramble"></div>
			<div class="controls">
				<button class="big" onclick="return timer.buttonOK(event);">OK</button>
				<button class="big" onclick="return timer.buttonPlus2(event);">+2</button>
				<button class="big" onclick="return timer.buttonDNF(event);">DNF</button>
			</div>
		</div>
		<div id="main">
			<div id="floatright">
				<div id="users"></div>
				<div id="parties">

					<div class="partytab" id="partyList">
						<!--<a href="javascript:handler.party.tabTo($('#partyData'))">Data</a>-->
						<a href="javascript:handler.party.tabTo($('#partyNew'))">&raquo;Neue Party erstellen</a><br />
						<div id="partylist"></div>
					</div>
					<div class="partytab" id="partyNew">
						<a href="javascript:handler.party.tabTo($('#partyList'))">&laquo; zurück</a><br /> <br />
						<form class="nosubmit" onsubmit="handler.party.create(this.name.value, this.rounds.value, this.cubeType[this.cubeType.selectedIndex].value, this.ranking[this.ranking.selectedIndex].value); this.rounds.value=''; handler.party.tabTo($('#partyList'))">
							<input type="text" name="name" placeholder="Name" size="8" maxLength="20"> <input type="number" name="rounds" placeholder="Runden" autocomplete="off" size="8"><br /> <select name="cubeType">
								<option value="2x2 URF">2x2</option>
								<option value="3x3" selected>3x3</option>
								<option value="4x4">4x4</option>
								<option value="5x5">5x5</option>
								<option value="6x6">6x6</option>
								<option value="7x7">7x7</option>
								<option disabled>-----------------</option>
								<option value="Clock">Clock</option>
								<option value="Megaminx">Megaminx</option>
								<option value="Pyraminx">Pyraminx</option>
								<option value="Square-1">Square-1</option>
								<option value="Floppy">Floppy</option>
								<option value="Skewb">Skewb</option>
								<option disabled>-----------------</option>
								<option value="3x3 easy cross">3x3 easy cross</option>
								<option value="3x3 CLL">3x3 CLL</option>
								<option value="3x3 ELL">3x3 ELL</option>
								<option value="3x3 F2L">3x3 F2L</option>
								<option value="3x3 OLL">3x3 OLL</option>
								<option value="3x3 PLL">3x3 PLL</option>
								<option value="Tower">Tower Cube</option>
								<option value="Rubik's Tower">Rubik's Tower</option>
								<option value="Rubik's Domino">Rubik's Domino</option>
								<option disabled></option>
								<option disabled>by Prisma Timer</option>
								<option disabled></option>
							</select> <select name="ranking">
								<option value="avg">Average</option>
								<option value="best">Best</option>
								<option value="mean">Mean</option>
							</select> <input type="submit" value="OK!">
						</form>
					</div>
					<br />
				</div>
				<div id="rooms"></div>
			</div>
			<div id="chat">
				<div id="tabcontents"></div>
			</div>
			<div id="input">

				<form class="nosubmit" onsubmit="handler.chat.chat(this.input_field.value), this.input_field.value='';" action="#">
					<input style="width: 24%;" name="input_field" maxlength="" autocomplete="off" type="text">
					<input value="Senden!" type="submit">
				</form>
				<button onclick="timer.open();">Timer einblenden</button> Volume: <input type="range" value="0.5" min="0" max="1" step="0.0001" onchange="setGlobalVolume(this.value)" oninput="setGlobalVolume(this.value)" />

			</div>
		</div>
	</div>

</body>

</html>