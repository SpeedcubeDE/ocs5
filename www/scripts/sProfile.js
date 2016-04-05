function HandlerProfile() {
	var This = this;

	This.type = "profile";
	This.selectedProfile = {};

	This.fetchProfile = function(id) {
		jws.sendRaw({
			type : "profile",
			action : "get",
			userID: +id
		});
	}
	
	This.selectProfile = function(id) {
		if (id < 0) {
			$("#profileoverlay").fadeOut(200);
		} else {
			This.fetchProfile(id);
			$("#profile").text("");
			$("#profileoverlay").fadeIn(200);
		}
	};
	
	This.getSelectedProfile = function(id) {
		return This.selectedProfile;
	};
	
	This.updateData = function() {
		var html = "";
		
		var tts = getTimeString;
		profile = This.selectedProfile.userData;
		
		html += '<table>';
		html += '<tr><td>Chatmessages</td><td>'+profile.chatMsgCount+'</td></tr>';
		html += '<tr><td>Onlinezeit</td><td>'+(profile.onlineTime / (1000*60*60)).toFixed(1)+'h</td></tr>';
		html += '<tr><td>Logincount</td><td>'+profile.loginCount+'</td></tr>';
		html += '<tr><td>Registriert</td><td>'+new Date(profile.registerDate).toLocaleDateString("de-DE")+'</td></tr>';
		
		for (var i=0; i < profile.times.length; i++) {
			t = profile.times[i];
			html += '<tr><th colspan="2">--- '+t.type+' ---</th></tr>';
			html += '<tr><td>Best:</td><td>'+tts(t.bestN)+'</td></tr>';
			html += '<tr><td>Count:</td><td>'+t.countN+'</td></tr>';
			html += '<tr><td>time:</td><td>'+tts(t.timeN)+'</td></tr>';
			html += '<tr><td>avg:</td><td>'+tts(t.timeN / t.countN)+'</td></tr>';
		}
		
		html += '</table>';
		
		$("#profile").html(html);
	};

	This.handle = function(data) {

		if (data.type != "profile" || data.action != "data") return;
		This.selectedProfile = data;
		This.updateData()
		
	};
}