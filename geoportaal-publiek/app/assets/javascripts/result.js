require([
	'dojo/dom',
	'dojo/query',
	'dojo/on',
	'dojo/_base/array',
	'dojo/_base/lang',
	'dojo/_base/window',
	'dojo/dom-attr',
	'dojo/dom-style',
	
	'dojo/NodeList-traverse',
	'dojo/domReady!'
	], function(dom, query, on, array, lang, win, domAttr, domStyle) {
		
		// Expand or collapse single metadata record
		on(win.doc, '.search-metadata > .row:click, .browse-metadata > .row:click', function(e) {
			var row = e.target.closest('.row');
			var description = query(row).query('.description')[0];
			
			var descDisplay = domStyle.get(description, 'display');
			if(descDisplay === 'none') {
				domStyle.set(description, 'display', 'block');
			} else {
				domStyle.set(description, 'display', 'none');
			}
			
			var descList = query('.search-metadata > .row > .description, .browse-metadata > .row > .description');
			array.forEach(descList, function(item) {
				if(domStyle.get(item, 'display') === 'none') {
					domAttr.set(dom.byId('js-expand-all'), 'checked', false);
				}
			});
		});
		
		// Expand or collapse all metadata records
		on(dom.byId('js-expand-all'), 'change', function(e) {
			var descList = query('.search-metadata > .row > .description, .browse-metadata > .row > .description');
			var checkStatus = domAttr.get(dom.byId('js-expand-all'), 'checked');
			
			array.forEach(descList, function(item) {
				if(checkStatus === true) {
					domStyle.set(item, 'display', 'block');
				} else {
					domStyle.set(item, 'display', 'none');
				}
			});
		});
		
		// Check all subjects
		on(dom.byId('js-subject-select-all'), 'click', function(e) {
			var subjects = query('.js-subject');
			array.forEach(subjects, function(item) {
				domAttr.set(item, 'checked', true);
			});
		});
		
		// Uncheck all subjects
		on(dom.byId('js-subject-select-none'), 'click', function(e) {
			var subjects = query('.js-subject');
			array.forEach(subjects, function(item) {
				domAttr.set(item, 'checked', false);
			});
		});
});