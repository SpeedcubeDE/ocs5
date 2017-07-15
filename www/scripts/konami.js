(function($) {

	$.fn.konami = function(callback, code) {
		if (code === undefined)
			code = "38,38,40,40,37,39,37,39,66,65";

		return this.each(function() {
			var kkeys = new Array();
			$(this).on("keydown", function(e) {
				kkeys.push(e.keyCode);
				if (kkeys.toString().indexOf(code) >= 0) {
					//$(this).off("keydown", arguments.callee);
					callback(e);
					kkeys = new Array();
				}
			});
		});
	};

})(jQuery);
