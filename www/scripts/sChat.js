function HandlerChat(chatHandler) {

	this.chatHandler = chatHandler;

	this.chat = function(msg, room) {
		if (msg == "")
			return; //msg = Math.random().toString(36).slice(2);
		if (room == undefined)
			// Keep this reference up to date
			room = handler.chatroom.selectedRoom;
		jws.sendRaw({
			type : "chat",
			msg : msg,
			roomID : +room
		});
	};

	this.handle = function(data) {
		obj = handler.chatroom.getRoomObj(data.roomID);
		if (obj.length == 0) {
			handler.chatroom.tabber.addTab(-1, "I am error", "");
			obj = handler.chatroom.getRoomObj(-1);
		}
		chatHandler(data, obj);
	};

}