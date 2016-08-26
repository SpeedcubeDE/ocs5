jQuery.fn.extend({
	slideRight : function(d) {
		return this.each(function() {
			jQuery(this).animate({
				"width" : "99.9%"
			}, d);
		});
	},
	slideLeft : function(d) {
		return this.each(function() {
			jQuery(this).animate({
				"width" : '0'
			}, d);
		});
	}
});

// Mobile device test
// by http://www.jquery4u.com/mobile/detect-mobile-devices-jquery/
(function(a) {
	jQuery.isMobile = /android.+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|symbian|treo|up\.(browser|link)|vodafone|wap|windows (ce|phone)|xda|xiino/i.test(a)
			|| /1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|e\-|e\/|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(di|rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|xda(\-|2|g)|yas\-|your|zeto|zte\-/i
					.test(a.substr(0, 4));
})(navigator.userAgent || navigator.vendor || window.opera);

var hideTab = function(obj) {
	obj.stop(true, false);
	obj.slideLeft(400);
	obj.fadeOut(0);
};

var showTab = function(obj) {
	obj.stop(true, false);
	obj.fadeIn(0);
	obj.slideRight(400);
};

function startCountdown(n, obj, func) {
	countdown = n;
	obj.html(countdown);
	var interval = null;
	interval = setInterval(function() {
		countdown--;
		obj.html(countdown);
		if (countdown <= 0) {
			func();
			clearInterval(interval);
		}
	}, 1000);
}

var audio = {};
audio.newMsg = new Audio("res/newMsg.ogg");
audio.newMsg.baseVolume = 0.1;
audio.newPM = new Audio("res/newPM.ogg");
audio.newPM.baseVolume = 0.3;
audio.konami = new Audio("res/batman.ogg");
audio.konami.baseVolume = 0.5;
audio.konami.loop = true;

var jws;
var username;
var handler = new Object();
var timer;

var MAX_CHATMESSAGES = 100;
var countdown = 0;

function Socket() {
	this.socket;

	if (!window.WebSocket) {
		window.WebSocket = window.MozWebSocket;
	}

	if (window.WebSocket) {
		if (ip == "")
			ip = "ocs.speedcube.de";
		socket = new WebSocket("ws://" + ip + ":34543/websocket");
		socket.onopen = onopen;
		socket.onmessage = onmessage;
		socket.onclose = onclose;
		socket.onerror = onerror;
	} else {
		alert("Your browser does not support HTML5 Web Sockets.\nMake your browser go die in a cancer fire. Or update it.");
	}

	this.send = function(event) {
		event.preventDefault();
		if (window.WebSocket) {
			if (socket.readyState == WebSocket.OPEN) {
				if (username != null) {
					var text = event.target.message.value;
					var textContainer = {};
					textContainer.type = "chat";
					textContainer.msg = text;
					textContainer.room = "main";

					this.sendRaw(textContainer);
				} else {
					socket.send(event.target.message.value);
				}
				event.target.message.value = '';
			} else {
				alert("The socket is not open.");
			}
		}
	};

	this.sendJSON = function(event) {
		event.preventDefault();
		if (window.WebSocket) {
			if (socket.readyState == WebSocket.OPEN) {
				socket.send(event.target.jsonText.value);
			} else {
				alert("The socket is not open.");
			}
		}
	};

	this.sendRaw = function(jsonObject) {
		console.log("SEND: " + JSON.stringify(jsonObject));
		socket.send(JSON.stringify(jsonObject));
	};

}

function onopen(event) {
	console.log("Web Socket opened!");
	// login
	jws.sendRaw({
		type : 'login',
		key : token
	});
}

function onmessage(event) {
	var data = JSON.parse(event.data);
	console.log("RESPONSE:" + event.data);
	log(data);
	handleResponse(data);
}

function onclose(event) {
	log("Web Socket closed", event);
	noty({
		type : "warning",
		text : "Die Verbindung zum Server wurde getrennt. Automatischer Reload in <span id='countdown'>10</span> Sekunden.",
		closeWith : []
	});
	startCountdown(10, $("span#countdown"), function() {
		location.reload();
	});
	// location.reload();
}

function onerror(event) {
	log("Some sort of WebSocket error", event);
	// noty({type: "error", text: "Es ist ein Verbindungsfehler aufgetreten!"});
}

function log(newData, event) {
	// logging is annoying
}

$(document).ready(function() {
	jws = new Socket();
	handler.chat = new HandlerChat(handleChatmessage);
	handler.chatroom = new HandlerChatroom($("#tabs"), $("#tabcontents"));
	handler.party = new HandlerParty();
	handler.profile = new HandlerProfile();
	handler.userlist = new HandlerUserlist($("div#users"));
	timer = new Timer();

	$("input[type=number]").keydown(function(event) {
		// backspace, delete, enter, 0-9, numpad 0-9
		if (event.keyCode != 46 && event.keyCode != 8 && event.keyCode != 13 && (event.keyCode < 48 || event.keyCode > 57) && (event.keyCode < 96 || event.keyCode > 105)) {
			event.preventDefault();
		}
	});

	$("form.nosubmit").submit(false);

	setInterval(function() {
		jws.sendRaw({
			type : "heartbeat"
		});
	}, 60000);

	$(window).resize(function() {
		updateResize();
	});
	
	// timer shortcut
	$(window).keyup(function(e) {
		if (e.ctrlKey && e.keyCode == 32) { // ctrl-space
	        timer.open();
	    }
	})

	$(document).konami(function() {
		audio.konami.volume = 1;
		audio.konami.play();
		$("body").css("width", "50%");
		$("body").css("height", "50%");

		$('body').animate({
			borderSpacing : 100
		}, {
			step : function(now, fx) {
				$(this).css('transform', 'rotate(' + (now / 100) * 1080 + 'deg)');
				$("body").css("width", now + "%");
				$("body").css("height", now + "%");
			},
			duration : 1200,
			complete : function() {
				$("body").css("borderSpacing", "5");
				var a = null;
				a = function() {
					$('body').animate({
						borderSpacing : 0
					}, {
						step : function(now, fx) {
							$(this).css('transform', 'rotate(' + (now - 3) + 'deg)');
						},
						duration : 372
					});
					$('body').animate({
						borderSpacing : 6
					}, {
						step : function(now, fx) {
							$(this).css('transform', 'rotate(' + (now - 3) + 'deg)');
						},
						duration : 372,
						complete : a
					});
				};
				a();
			},
			easing : "linear"
		});
	});

});

// /////////////////////////////////////////////////////

function handleResponse(data) {
	switch (data.type) {
	case "login":
		if (data.login) {
			username = data.name;
			noty({
				type : "success",
				timeout : 2000,
				layout : "topCenter",
				text : "logged in as " + username
			});
			console.log("logged in as " + username);
		}
		break;
	case "chat":
		handler.chat.handle(data);
		break;
	case "partylist":
	case "party":
		handler.party.handle(data);
		break;
	case "alert":
		var timeout = 5000;
		if (data.sticky)
			timeout = false;
		noty({
			type : data.action,
			timeout : timeout,
			text : data.msg,
			layout : "topCenter"
		});
		break;
	case "roomlist":
	case "roomUserlist":
		handler.chatroom.handle(data);
		break;
	case "userlist":
		handler.userlist.handle(data);
		break;
	case "user":
		handler.userlist.handleSingle(data);
		break;
	case "profile":
		handler.profile.handle(data);
		break;
	case "sound":
		audio.temp = new Audio("res/" + data.filename);
		audio.temp.volume = volume * data.volume;
		audio.temp.baseVolume = data.volume;
		audio.temp.play();
		break;
	}
}

function scrollToBottom(duration) {
	if (duration == undefined)
		duration = 1000;
	var chat = $("div#chat");
	var h = chat.prop("scrollHeight");
	chat.stop(true, false);
	chat.animate({
		scrollTop : h + "px"
	}, duration);
}

function getTimeString(ms) {
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

function handleChatmessage(msg, obj) {
	user = handler.userlist.getUser(msg.userID);

	var time = formatTime(msg.time.toString());

	$(obj).find("tbody").append("<tr class='msg'><td class='time'>" + time + "</td><td>" + getChatLine(msg, user) + "</td></tr>").linkify({
		target : "_blank"
	});

	var username = "";
	if (user != null)
		username = user.username;
	if (username != window.username) {
		audio.newMsg.play();
	}

	// Check for too many lines
	num = obj.find(".msg").length + 1;
	overflow = num - MAX_CHATMESSAGES;
	if (overflow > 0)
		obj.find(".msg:lt(" + overflow + ")").remove();

	handler.chatroom.tabber.highlight(msg.roomID);

	if (autoscroll) {
		scrollToBottom();
	}
}

function getChatLine(msg, user) {
	for (var i = 0; i < msg.msg.length; i++)
		msg.msg[i] = msg.msg[i].escapeHTML();
	var text = msg.msg.join("<br />\n");
	if (msg.userID < 0) {
		return "<span class='system'>" + text + "</span>";
	} else {
		var username;
		if (user == null) {
			handler.userlist.fetchUser(msg.userID);
			username = "User(" + msg.userID + ")";
		} else {
			username = user.username;
		}
		if (msg.me == true)
			return "<span class='username user_" + msg.userID + "' style='color:white;' onclick='handler.profile.selectProfile(\"" + msg.userID + "\")'>" + username + "</span> " + text;
		else
			return "<span class='username user_" + msg.userID + "' onclick='handler.profile.selectProfile(\"" + msg.userID + "\")'>" + username + "</span> - " + text;
	}
}

function updateResize() {
	// alert("a");
	// alert(parseInt($("#tabcontents").innerWidth()));
	var w = parseInt($("#tabcontents").innerWidth()) - (parseInt($("#tabcontents").css("padding-left")) + parseInt($("#tabcontents").css("padding-right")));
	// alert(w);
	$(".chatwrapper").width((w) + "px");
}

function formatTime(timestamp) {
	var time = new Date(timestamp * 1000);
	return time.toTimeString().replace(/.*(\d{2}:\d{2}):\d{2}.*/, "$1");
}

function emptyInput(obj) {
	obj.value = "";
	return false;
}

var volume = 0.5;
function setGlobalVolume(v) {
	volume = v;
	for (var key in audio) {
		audio[key].volume = v * audio[key].baseVolume;
	}	                                     
}

var autoscroll = true;
function setAutoscroll(scroll) {
	autoscroll = scroll;
}