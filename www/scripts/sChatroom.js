function HandlerChatroom(tabs, tabcontents) {
	this.selectedRoom = -1;
	this.rooms = [];

	this.getSelectedRoom = function() {
		for (var i = 0; i < this.rooms.length; i++) {
			// var room = this.rooms[i];
			if (this.rooms[i].id == this.selectedRoom) {
				return this.rooms[i];
			}
		}
		return null;
	};

	this.select = function(id) {
		this.selectedRoom = id;
		handler.userlist.updateUserlist(this.getSelectedRoom());
		scrollToBottom(0);
	}.bind(this);

	this.leave = function(roomID) {
		jws.sendRaw({
			type : "chatRoom",
			action : "leave",
			roomID : roomID
		});
	};

	this.tabber = new Tabber(tabs, tabcontents, this.select, this.leave);

	this.create = function(roomName, pw, minPower) {
		if (roomName == "" || roomName == null || pw == null) return;
		jws.sendRaw({
			type : "chatRoom",
			action : "create",
			roomName : roomName,
			password : pw,
			minPower : +minPower
		});
	};

	this.enter = function(roomID, password) {
		jws.sendRaw({
			type : "chatRoom",
			action : "enter",
			roomID : +roomID,
			password : password
		});
	};

	this.remove = function(roomID) {
		jws.sendRaw({
			type : "chatRoom",
			action : "remove",
			roomID : +roomID
		});
	};

	this.getRoomObj = function(roomID) {
		return this.tabber.getTab(roomID);
	};

	this.update = function() {

		// Step 1: Fix tabs
		var roomIDs = [];
		for (var i = 0; i < this.rooms.length; i++) {
			var room = this.rooms[i];
			if (room.inRoom) {
				roomIDs.push(room.id);
				if (this.getRoomObj(room.id).length == 0) {
					this.addRoom(room.id, room.name, "");
					// if a private chat was opened, play pling sound
					if (room.type == "whisper") {
						audio.newPM.play();
					}
				}
			}
		}
		this.tabber.cleanupTabs(roomIDs);

		// Step 2: Fix Channel List
		var obj = $("#rooms");
		obj.empty();
		var html = "<table class='room'><tr><th>Channel <a class='ico-enter' href='javascript:handler.chatroom.create(window.prompt(\"Channelname:\"), window.prompt(\"Channelpasswort (leer für keines):\"), 0);'></a></th><th>User</th><th></th></tr>";

		for (var i = 0; i < this.rooms.length; i++) {
			var room = this.rooms[i];
			if (i % 2 != 0) {
				html += "<tr><td>";
			} else {
				html += "<tr class='other'><td>";
			}
			if (room.inRoom) {
				html += room.name.escapeHTML() + "</td><td>" + room.userNum + "</td><td>";
			} else if (room.hasPW) {
				html += "<a class='locked' href='javascript:handler.chatroom.enter(" + room.id + ",window.prompt(\"Bitte gib das Raumpasswort ein.\"));'>" + room.name.escapeHTML() + "</a></td><td>" + room.userNum + "</td><td>";
			} else {
				html += "<a href='javascript:handler.chatroom.enter(" + room.id + ",\"\");'>" + room.name.escapeHTML() + "</a></td><td>" + room.userNum + "</td><td>";
			}
			if (room.canClose) {
				html += "<a class='ico-close' href='javascript:if (window.confirm(\"Folgenden Raum wirklich entfernen?: "+room.name.split('"').join('\\\"')+"\")) handler.chatroom.remove("+room.id+");'></a>";
			}
			html += "</td></tr>";
		}
		html += "</table>";
		obj.append(html);
	};

	this.addRoom = function(id, title, content) {
		this.tabber.addTab(id, title, content);
		if (this.selectedRoom == -1) {
			this.tabber.selectTab(id);
		}
	};

	this.handle = function(data) {

		if (data.type == "roomlist") {

			// see sParty.js handle(). Basically the same

			for (var i = 0; i < data.rooms.length; i++) {
				data.rooms[i].users = [];
				for (var j = 0; j < this.rooms.length; j++) {
					if (data.rooms[i].id == this.rooms[j].id) {
						data.rooms[i].users = this.rooms[j].users || [];
						this.rooms.splice(j, 1);
						break;
					}
				}
			}
			
			this.rooms = data.rooms;

			this.update();

		} else if (data.type == "roomUserlist") {
			for (var i = 0; i < this.rooms.length; i++) {
				if (this.rooms[i].id == data.roomID) {
					this.rooms[i].users = data.users;
				}
			}
			handler.userlist.update();
		}

	};
}