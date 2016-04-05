function Timer() {

	var This = this;

	This.running = false;
	This.opened = false;
	This.startTime = new Date();
	This.time = 0;
	This.timerObj = $("#timer");
	This.timeObj = $("#timer .ttime");
	This.scrambleObj = $("#timer .scramble");
	This.controlsObj = $("#timer .controls");
	This.updateInterval;
	This.blockNext = false;
	This.stackmatActivated = false;

	This.scrambleToControls = function() {
		if (!handler.party.validSelection() || !handler.party.hasRunningParty())
			return;
		This.scrambleObj.hide();
		This.controlsObj.show();
		This.controlsObj.children().first().focus();
	};

	This.controlsToScramble = function() {
		This.scrambleObj.show();
		This.controlsObj.hide();
	};

	This.reset = function() {
		This.time = 0;
		This.startTime = new Date();
		This.stop();
		This.controlsToScramble();
	};

	This.close = function() {
		if (!This.opened)
			return;
		// This.reset();
		This.controlsObj.find("button").blur();
		This.timerObj.fadeOut(100);
		This.opened = false;
	};

	This.start = function() {
		This.reset();
		This.running = true;
		This.startTime = new Date();
		This.updateInterval = setInterval(This.update, 76);
	};

	This.getTimeString = function(ms) {
		if (ms == -1)
			return "DNF";
		if (ms == -2)
			return "DNS";
		if (ms < -2)
			return "?";
		var milliseconds = parseInt((ms % 1000) / 10);
		var seconds = parseInt((ms / 1000) % 60);
		var minutes = parseInt((ms / (1000 * 60)));
		// var minutes = parseInt((ms / (1000 * 60)) % 60);
		// var hours = parseInt((ms / (1000 * 60 * 60)) % 24);

		// hours = (hours < 10) ? "0" + hours : hours;
		// minutes = (minutes < 10) ? "0" + minutes : minutes;
		seconds = (seconds < 10) ? "0" + seconds : seconds;
		milliseconds = (milliseconds < 10) ? "0" + milliseconds : milliseconds;

		// return hours + ":" + minutes + ":" + seconds + "." + milliseconds;
		if (minutes > 0)
			return minutes + ":" + seconds + "." + milliseconds;
		else
			return seconds + "." + milliseconds;
	};

	This.updateDisplay = function() {
		var str = This.getTimeString(This.time);
		This.timeObj.html(str);
	};

	This.updateScramble = function() {
		// TODO temporary displaying of selected party.
		var party = handler.party.getParty();
		var html = "";
		if (party == null) {
			html = "Keine Party ausgewählt";
		} else {
			html = "Ausgewählte Party: " + party.name.escapeHTML();
			if (!party.inParty) {
				html += " (nicht in Party)";
			}else if (party.closed)
				html += " (beendet)";
			else if (!party.started)
				html += " (noch nicht gestartet)";
			else
				html += "<br />" + party.data.scrambles[party.currentRound];
		}
		This.scrambleObj.html(html);
	};

	This.update = function() {
		var stop = new Date();
		if (This.running)
			This.time = stop.getTime() - This.startTime.getTime();
		This.updateDisplay();
		This.updateScramble();
	};

	This.stop = function() {
		clearInterval(This.updateInterval);
		This.update();
		This.running = false;
		This.scrambleToControls();
	};

	This.open = function() {
		if (This.opened)
			return;
		This.opened = true;
		This.reset();
		This.timerObj.fadeIn(100);
		This.update();
	};

	This.inputUp = function(e) {
		if (This.stackmatActivated)
			return;
		if (This.blockNext) {
			This.blockNext = false;
			e.preventDefault();
			return false;
		}
		if (This.time != 0)
			return;
		if (!This.running)
			This.start();
	};

	This.inputDown = function(e) {
		if (This.stackmatActivated)
			return;
		if (This.running) {
			This.stop();
			This.blockNext = true;
		}
	};

	$(window).keyup(function(e) {
		if (!This.opened)
			return;
		if (e.which == 32) {
			This.inputUp(e);
		}
	});

	$(window).keydown(function(e) {
		if (!This.opened)
			return;
		if (e.which == 32) {
			This.inputDown(e);
		}
	});

	This.timeObj.mousedown(function(event) {
		if (event.which == 1 && $.isMobile)
			This.inputDown(event);
	});

	This.timeObj.mouseup(function(event) {
		if (event.which == 1 && $.isMobile)
			This.inputUp(event);
	});

	This.updateStackmat = function(ms, status) {
		if (!This.stackmatActivated)
			return;

		if (status == "I") {
			This.controlsToScramble();
		} else if ("S".indexOf(status) > -1) {
			This.scrambleToControls();
		} else if (status == " ") {
			This.controlsToScramble();
		}

		This.time = ms;
		This.update();
	};

	This.stackmatReader = new StackmatReader(This.updateStackmat);

	This.activateStackmat = function() {
		alert("Dieses Feature ist experimentiell und funktioniert am besten im Google Chrome Browser.");
		This.stackmatReader.startRecording(function() {
			This.reset();
			This.stackmatActivated = true;
			$("#stackmatToggle").text("Leertaste verwenden");
			$("#stackmatGenToggle").text("");
		});
	};

	This.deactivateStackmat = function() {
		This.stackmatReader.stopRecording();
		This.stackmatActivated = false;
	};

	This.buttonOK = function(event) {
		handler.party.time(This.time);
		This.close();
		event.stopPropagation();
		return false;
	};

	This.buttonPlus2 = function(event) {
		handler.party.time(This.time + 2000);
		This.close();
		event.stopPropagation();
		return false;
	};

	This.buttonDNF = function(event) {
		handler.party.time(-1);
		This.close();
		event.stopPropagation();
		return false;
	};

	This.timerObj.fadeOut(0);

	// Link inside the timer toggling stackmat-input on and off
	$("#stackmatToggle").click(function() {
		if ($(this).text() == "Leertaste verwenden") {
			timer.deactivateStackmat();
			$(this).text("Stackmat verwenden");
			$("#stackmatGenToggle").text(timer.gen3 ? "(gen3)" : "(gen2)");
		} else {
			timer.activateStackmat();
		}
	});
	
	$("#stackmatGenToggle").click(function() {
		if ($(this).text() == "(gen2)") {
			timer.gen3 = true;
			$(this).text("(gen3)");
		} else {
			timer.gen3 = false;
			$(this).text("(gen2)");
		}
	});

}