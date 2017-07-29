function HandlerUserlist(userlist) {
	this.userlist = userlist;
	this.users = [];
	this.fetched = [];

	this.fetchUser = function(id) {
		if (this.fetched.indexOf(id) > -1)
			return;
		jws.sendRaw({
			type : "user",
			action : "get",
			userID : +id
		});
		this.fetched.push(id);
	};

	this.addUser = function(user) {
		this.users.push(user);
	};

	this.getUser = function(id) {
		if (id < 0) {
			return id;
		}
		var uid = this.hasUser(id);
		if (uid == null) {
			this.fetchUser(id);
		}
		return this.users[uid];
	};

	this.hasUser = function(id) {
		for (var i = 0; i < this.users.length; i++) {
			if (this.users[i].id == id)
				return i;
		}
		return null;
	};

	this.handle = function(data) {
		for (var i = 0; i < data.users.length; i++) {
			var user = data.users[i];
			var i2 = this.hasUser(user.id);
			if (i2 == null) {
				this.addUser(user);
			} else {
				this.users[i2] = user;
			}
		}
		this.update();
	};

	this.handleSingle = function(user) {
		var u = user.user;
		var i2 = this.hasUser(u.id);
		if (i2 == null) {
			this.addUser(u);
		} else {
			this.users[i2] = u;
		}
		this.update();
		$(".user_" + u.id).html(u.username);
		this.updateUserlist(handler.chatroom.getSelectedRoom());
	};

	this.whisper = function(id) {
		jws.sendRaw({
			type : "whisper",
			action : "start",
			userID : +id
		});
	};

	this.update = function() {
		// rebuild css
		var css = $("#additionalCSS");

		var style = "<style type='text/css'>";
		for (var i = 0; i < this.users.length; i++) {
			var user = this.users[i];
			style += ".user_" + user.id + " {color: #" + user.nameColor + ";}";
			/*
			 * style += ".user_" + user.id + ":after {content: '" +
			 * user.username + "'}";
			 */
		}
		style += "</style>";
		css.html(style);

		this.updateUserlist(handler.chatroom.getSelectedRoom());
	}.bind(this);

	this.updateUserlist = function(room) {
		var users;
		if (room == null)
			users = [];
		else
			users = room.users;
		var html = "<table>";
		for (var i = 0; i < users.length; i++) {
			var user = this.getUser(users[i].id);
			if (user) {
				//html += "<tr><td class='username user_" + user.id + "' id='" + user.id + "' onclick='handler.userlist.whisper(" + user.id + ")'>";
				html += "<tr><td class='username user_" + user.id + (user.connected ? '' : ' offline') + "' id='" + user.id + "' onclick='handler.profile.selectProfile(" + user.id + ")'>";
				if (user.username == username)
					html += "&raquo; ";
				html += user.username + "</td><td class='rank'>" + user.rank + "</td><td class='status'>" + user.status.escapeHTML() + "</td></tr>";
			}
		}
		html += "</table>";
		this.userlist.empty();
		this.userlist.append(html);
	};
}
