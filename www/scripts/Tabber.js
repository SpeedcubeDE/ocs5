function Tabber(tabs, tabcontents, onSelect, onClose) {

	var s_current = "current";
	var s_content = "_tabcontent";

	this.selectTab = function(id) {
		var contentname = id + s_content;
		// hide all other tabs

		hideTab(tabcontents.find(".p"));
		// tabcontents.find("p").fadeOut("fast");
		tabs.find("li").removeClass(s_current);
		// show current tab
		showTab($("#" + contentname));
		// $("#" + contentname).fadeIn("fast");
		var tab = tabs.find("#" + id).parent();
		tab.removeClass("notify");
		tab.addClass(s_current);
		onSelect(id);
	};

	this.closeTab = function(id) {

		tabs.find("a.tab[id=" + id + "]").parent().remove();

		var content = $("#" + id + s_content);
		content.attr("id", "");
		hideTab(content);
		setTimeout(function() {
			content.remove();
		}, 2000);

		if (tabs.find("li." + s_current).length == 0) {
			if (tabs.find("li").length > 0) {
				// find the first tab
				var firsttab = tabs.find("li:first-child");
				firsttab.addClass(s_current);
				// get its link name and show related content
				var firsttabid = $(firsttab).find("a.tab").attr("id");
				showTab($("#" + firsttabid + s_content));
				onSelect(+firsttabid);
			} else {
				onSelect(-1);
			}
		}
		onClose(id);
	};

	this.updateListeners = function() {
		// var st = this.selectTab;
		tabs.find('a.tab').off("click").on("click", {
			selectTab : this.selectTab
		}, function(event) {
			var tabid = $(this).attr("id");
			event.data.selectTab(+tabid);
		});

		tabs.find('a.remove').off("click").on("click", {
			closeTab : this.closeTab
		}, function(event) {
			var tabid = $(this).parent().find(".tab").attr("id");
			event.data.closeTab(+tabid);
		});
	};

	this.addTab = function(id, title, content) {
		if (tabs.find("#" + id).length != 0)
			return;

		tabs.append("<li><a class='tab' id='" + id + "' nohref>" + title.escapeHTML() + "</a><a nohref class='remove'>x</a></li>");
		tabcontents.append("<div class='p' id='" + id + s_content + "'><div class='chatwrapper'><table class='chattable'><tbody>" + content + "</tbody></table></div></div>");

		updateResize();

		$("#" + id + s_content).hide();

		if (handler.chatroom.selectedRoom == -1)
			this.selectTab(id);

		this.updateListeners();
	};

	this.getTab = function(id) {
		return $("#" + id + s_content);
	};

	this.cleanupTabs = function(roomIDs) {
		tabcontents.find(".p").each(function() {
			var id = $(this).attr("id").split(s_content[0])[0];
			if (!$.inArray(id, roomIDs)) {
				this.closeTab(id);
			}
		});
	};

	this.highlight = function(id) {
		if (id != handler.chatroom.selectedRoom)
			tabs.find("#" + id).parent().addClass("notify");
	};

}