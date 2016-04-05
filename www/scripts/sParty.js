function HandlerParty() {
	var This = this;

	This.type = "party";
	This.parties = [];
	This.selectedParty = -1;

	This.getParty = function(id) {
		if (id == undefined)
			id = This.selectedParty;
		for (var i = 0; i < This.parties.length; i++) {
			var party = This.parties[i];
			if (party.id == id)
				return party;
		}
		return null;
	};

	This.getPartyMembers = function(party) {
		if (party == null || party.data == null)
			return [];
		var users = [];
		for (var i = 0; i < party.data.result.length; i++) {
			if (!party.data.result[i].inParty)
				continue;
			var u = handler.userlist.getUser(party.data.result[i].userID);
			if (u != null)
				users.push(u);
		}
		return users;
	};

	This.hasRunningParty = function() {
		var p = This.getParty();
		if (p == null)
			return false;
		return !p.closed;
	};

	This.validSelection = function() {
		var party = This.getParty();
		if (party == null)
			return false;
		return party.inParty;
	};

	This.create = function(name, rounds, cubeType, ranking, mode) {
		if (mode == undefined)
			mode = 'normal';
		jws.sendRaw({
			type : "party",
			action : "create",
			rounds : +rounds,
			cubeType : cubeType,
			ranking : ranking,
			name : name,
			mode : mode
		});
	};

	This.enter = function(partyID) {
		jws.sendRaw({
			type : "party",
			action : "enter",
			partyID : +partyID
		});
	};

	This.leave = function(partyID) {
		if (partyID == undefined)
			partyID = This.selectedParty;
		jws.sendRaw({
			type : "party",
			action : "leave",
			partyID : +partyID
		});
	};

	This.start = function(partyID) {
		if (partyID == undefined)
			partyID = This.selectedParty;
		jws.sendRaw({
			type : "party",
			action : "start",
			partyID : +partyID
		});
	};

	This.remove = function(partyID) {
		if (partyID == undefined)
			partyID = This.selectedParty;
		jws.sendRaw({
			type : "party",
			action : "remove",
			partyID : +partyID
		});
	};

	This.kick = function(userID, partyID) {
		if (partyID == undefined)
			partyID = This.selectedParty;
		jws.sendRaw({
			type : "party",
			action : "kick",
			partyID : +partyID,
			userID : +userID
		});
	};

	This.time = function(time, round, partyID) {
		if (partyID == undefined)
			partyID = This.selectedParty;
		if (round == undefined)
			round = This.getParty(partyID).currentRound;
		jws.sendRaw({
			type : "party",
			action : "time",
			partyID : +partyID,
			round : +round,
			time : +time
		});
	};

	This.tabTo = function(to) {
		hideTab($("#parties > div:visible"));
		showTab(to);
	};

	This.tabTo($("#partyList"));

	This.getMinTime = function(times) {
		var min = Number.MAX_VALUE;
		for (var i = 0; i < times.length; i++) {
			var time = times[i];
			if (time > 0 && time < min)
				min = time;
		}
		if (min == Number.MAX_VALUE) return -1;
		return min;
	};

	This.getMaxTime = function(times) {
		var max = -1;
		for (var i = 0; i < times.length; i++) {
			var time = times[i];
			if (time < 0)
				return time;
			if (time > max)
				max = time;
		}
		return max;
	};

	This.getAvgName = function(name) {
		switch (name) {
		case "ra":
			return "Rolling Avg";
		case "ba":
			return "Best Avg";
		}
		return name;
	};

	This.updateData = function() {
		var partydata = $("#partydata");
		var html = "";
		var party = This.getParty();
		if (party == null) {
			html += "No party selected.";
			return;
		}
		if (party.started) {

			// sort result by place
			party.data.result.sort(function(a, b) {
				return a.place - b.place;
			});

			var length = party.data.result ? (party.data.result.length > 0 ? party.data.result[0].times.length : 0) : 0;
			html += "<table>";

			html += "<tr><td style='text-align:right;'>#</td>";
			var min = [], max = [];
			for (var j = 0; j < party.data.result.length; j++) {
				var r = party.data.result[j];
				min[j] = This.getMinTime(r.times);
				max[j] = This.getMaxTime(r.times);
				var user = handler.userlist.getUser(r.userID);
				html += "<td class='user_"+r.userID+"'>" + ((user == undefined) ? '?' : user.username) + "</td>";
			}
			html += "</tr>";

			for (var k = 0; k < length; k++) {

				html += "<tr class='partydata'><td style='text-align:right;'>" + (k + 1) + ".</td>";
				// html += "<td>" +
				// handler.userlist.getUser(r.userID).username + "</td>";

				for (var j = 0; j < party.data.result.length; j++) {

					var r = party.data.result[j];

					var time = r.times[k];
					var time_str = (time == 0) ? "..." : timer.getTimeString(time);
					if (k > party.currentRound) {
						html += "<td class='greyed'>" + time_str + "</td>";
					} else if (time == min[j]) {
						html += "<td class='min'>" + time_str + "</td>";
					} else if (time == max[j]) {
						html += "<td class='max'>" + time_str + "</td>";
					} else {
						html += "<td>" + time_str + "</td>";
					}
				}

				if (party.data.scrambles.length > k) {
					html += "<td><input onclick='$(this).select()' type='text' value=\"" + party.data.scrambles[k] + "\" class='scramble' /></td>";
				}

				html += "</tr>";

			}

			html += "<tr><td align='right'>";
			switch (party.data.ranking) {
			case "avg":
				html += "Avg";
				break;
			case "mean":
				html += "Mean";
				break;
			case "best":
				html += "Best";
				break;
			}
			if (party.closed) {
				html += " of " + party.rounds + "</td>";
			} else {
				html += " of " + party.currentRound + "</td>";
			}
			for (var j = 0; j < party.data.result.length; j++) {
				var r = party.data.result[j];

				html += "<td>";
				switch (party.data.ranking) {
				case "avg":
					html += timer.getTimeString(r.avg);
					break;
				case "mean":
					html += timer.getTimeString(r.mean);
					break;
				case "best":
					html += timer.getTimeString(min[j]);
					break;
				}
				html += "</td>";

			}
			html += "<td></td></tr>";

			var avgs = [];
			for (var j = 0; j < party.data.result.length; j++) {
				outer: for (var k = 0; k < party.data.result[j].avgs.length; k++) {
					for (var l = 0; l < avgs.length; l++) {
						if (avgs[l].name == party.data.result[j].avgs[k].name && avgs[l].length == party.data.result[j].avgs[k].length) {
							break outer;
						}
					}
					avgs[avgs.length] = party.data.result[j].avgs[k];
				}
			}
			for (var j = 0; j < avgs.length; j++) {
				var avg = avgs[j];
				html += "<tr><td align='right'>" + This.getAvgName(avg.name) + " of " + avg.length + "</td>";
				for (var k = 0; k < party.data.result.length; k++) {
					var a = party.data.result[k].avgs[j];
					if (a != undefined) {
						html += "<td>" + timer.getTimeString(a.time) + "<br /><span style='font-size:9px;'>#" + (a.start + 1) + " - #" + (a.start + a.length) + "</span></td>";
					} else {
						html += "<td>-</td>";
					}
				}
				html += "</tr>";
			}

			html += "</table>";

		} else {
			html += "Diese Party läuft noch nicht.";
		}
		partydata.html(html);
	};

	This.selectParty = function(id) {
		if (id < 0) {
			This.selectedParty = -1;
			$("#partyoverlay").fadeOut(200);
		} else {
			This.selectedParty = id;
			$("#partyoverlay").fadeIn(200);
		}
		This.updateData();
		This.updateList();
	};

	This.updateList = function() {

		var partylist = $("#partylist");
		var html = "<table style='width:100%; white-space: normal; overflow: hidden;'>";
		for (var i = 0; i < This.parties.length; i++) {
			var party = This.parties[i];

			// fixed 1-update-delay for cancelled lobbies
			if (party.closed && !party.started)
				continue;
			if (party.data != undefined) {
				if (party.closed && party.data.result.length == 0)
					continue;
			}

			html += "<tr class='partyrow'><td style='max-width: 110px; overflow: hidden;'>";
			if (party.closed)
				html += '<img src="img/ico/lock.png" /> ';
			else if (party.started)
				html += '<img src="img/ico/rotate.png" /> ';
			else
				html += '<img src="img/ico/door_in.png" /> ';
			if (party.id == This.selectedParty)
				html += "<b>" + party.name.escapeHTML() + "</b>";
			else
				html += "<a href='#' onclick='handler.party.selectParty(" + party.id + ");'>" + party.name.escapeHTML() + "</a>";

			html += "</td><td>" + party.cubeType.escapeHTML() + " - ";
			if (party.started)
				html += "Runde " + (party.currentRound + 1) + "/" + party.rounds;
			else
				html += party.rounds + " Runden";

			if (!party.closed) {
				if (!party.started && party.canEdit)
					html += " <a class='ico-go' href='javascript:handler.party.start(" + party.id + ");'> Start</a>";

				// if (!party.inParty && !party.started)
				// html += "<a class='ico-enter'
				// href='javascript:handler.party.enter(" + party.id + ");'>
				// Beitreten</a>";

				// if (party.inParty)
				// html += "<a class='ico-leave'
				// href='javascript:handler.party.leave(" + party.id + ");'>
				// Verlassen</a>";

				if (party.canEdit)
					html += " <a class='ico-close'href='javascript:handler.party.remove(" + party.id + ");'> Schließen</a>";
			}

			html += "<br />";
			var members = This.getPartyMembers(party);

			if (!party.inParty && !party.started)
				html += "<a class='ico-enter' href='javascript:handler.party.enter(" + party.id + ");'></a> ";

			for (var j = 0; j < members.length; j++) {
				if (j > 0)
					html += ", ";
				html += "<span class='username user_" + members[j].id + "'>" + members[j].username + "</span>";
				if (!party.closed) {
					if (members[j].username == username && party.inParty)
						html += " <a class='ico-leave' href='#' onclick='handler.party.leave(" + party.id + ");'></a>";
					else if (party.canEdit)
						html += " <a class='ico-leave' href='#' onclick='handler.party.kick(" + members[j].id + ", " + party.id + ");'> </a>";
				}
			}

			/*
			 * html += "<br /><img src='img/ico/user.png' /> " +
			 * This.getPartyMembers(party).map(function(elem) { return
			 * elem.username; }).join(", ");
			 */

			html += "</td></tr>";

		}
		html += "</table>";

		partylist.html(html);

	};

	This.handle = function(data) {

		switch (data.type) {
		case "partylist":

			// Copy all currently known party.data's to the new partylist
			for (var i = 0; i < data.parties.length; i++) {
				for (var j = 0; j < This.parties.length; j++) {
					if (data.parties[i].id == This.parties[j].id) {
						data.parties[i].data = This.parties[j].data;
						// remove party from current list and break inner loop
						This.parties.splice(j, 1);
						break;
					}
				}
			}

			// The remaining parties are deleted ones.
			// If they are closed and started and at least 1 member, keep them.
			// Discard otherwise.
			for (var i = 0; i < This.parties.length; i++) {
				var party = This.parties[i];
				if (!party.closed || !party.started)
					continue;
				if (party.data != undefined)
					if (party.data.result.length == 0)
						continue;
				data.parties.push(This.parties[i]);
			}

			// Asign the new partylist
			This.parties = data.parties;

			// sort. closed parties to the bottom
			This.parties.sort(function(a, b) {
				if (b.closed)
					return -1;
				if (!b.started)
					return 1;
				return 0;
			});

			This.updateList();
			This.updateData();
			timer.update();
			break;
		case "party":
			for (var i = 0; i < This.parties.length; i++) {
				if (This.parties[i].id == data.id) {
					// remove packet-specific information, just keep the
					// party-related data
					delete data.id;
					delete data.type;
					This.parties[i].data = data;
					break;
				}
			}
			This.updateList();
			This.updateData();
			timer.update();
			break;
		}

	};
}