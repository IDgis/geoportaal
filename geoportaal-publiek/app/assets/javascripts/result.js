require([
	'dojo/dom',
	'dojo/dom-construct',
	'dojo/query',
	'dojo/on',
	'dojo/_base/array',
	'dojo/_base/lang',
	'dojo/_base/window',
	'dojo/dom-attr',
	'dojo/dom-style',
	'dojo/request/xhr',
	
	'dojo/domReady!'
	], function(dom, domConstruct, query, on, array, lang, win, domAttr, domStyle, xhr) {
		
		// Expand or collapse single metadata record
		on(win.doc, '.md-title:click', function(e) {
			var description = query('.description[data-uuid=' + domAttr.get(e.target, 'data-uuid') + ']')[0];
			
			var descDisplay = domStyle.get(description, 'display');
			if(descDisplay === 'none') {
				domStyle.set(description, 'display', 'block');
			} else {
				domStyle.set(description, 'display', 'none');
			}
		});
		
		// Expand or collapse all metadata records
		on(win.doc, '.js-expand-all-evt:change', function(e) {
			var descList = query('.search-metadata > .row > .description, .browse-metadata > .row > .description');
			var checkStatus = domAttr.get(dom.byId('js-expand-all'), 'checked');
			
			array.forEach(descList, function(item) {
				if(checkStatus === true) {
					domStyle.set(item, 'display', 'block');
					domAttr.set(dom.byId('js-expand-value'), 'value', "true");
				} else {
					domStyle.set(item, 'display', 'none');
					domAttr.set(dom.byId('js-expand-value'), 'value', "false");
				}
			});
			
			if(domAttr.get(dom.byId('js-page'), 'value') === 'search') {
				filterExpandTypes();
			} else if(domAttr.get(dom.byId('js-page'), 'value') === 'browse') {
				filterExpandSubjects();
			}
		});
		
		// Check all subjects
		on(win.doc, '.js-subject-select-all:click', function(e) {
			var subjects = query('.js-data-subject');
			array.forEach(subjects, function(item) {
				domAttr.set(item, 'checked', true);
			});
			
			filterExpandSubjects();
		});
		
		// Uncheck all subjects
		on(win.doc, '.js-subject-select-none:click', function(e) {
			var subjects = query('.js-data-subject');
			array.forEach(subjects, function(item) {
				domAttr.set(item, 'checked', false);
			});
			
			filterExpandSubjects();
		});
		
		// Filter event for metadata type
		on(win.doc, '.js-data-type:change', function(e) {
			filterExpandTypes();
		});
		
		// Filter event for subjects
		on(win.doc, '.js-data-subject:change', function(e) {
			filterExpandSubjects();
		});
		
		function filterExpandTypes() {
			var arrayElements = [];
			var elements = query('.js-data-type:checked');
			array.forEach(elements, function(item) {
				var element = domAttr.get(item, 'data-md-type');
				arrayElements.push(element);
			});
			
			var start = domAttr.get(dom.byId('js-start-current'), 'value');
			var textSearch = domAttr.get(dom.byId('js-text-search'), 'value');
			var elementString = arrayElements.join('+');
			var expandValue = domAttr.get(dom.byId('js-expand-value'), 'value');
			
			xhr(jsRoutes.controllers.Application.search(start, textSearch, elementString, true, expandValue).url, {
				handleAs: "html"	
			}).then(function(data) {
				domConstruct.empty(dom.byId('js-search-results-all'));
				domConstruct.place(data, dom.byId('js-search-results-all'));
				domAttr.set(dom.byId('js-element-string'), 'value', elementString);
			});
		}
		
		function filterExpandSubjects() {
			var arrayElements = [];
			var elements = query('.js-data-subject:checked');
			array.forEach(elements, function(item) {
				var element = domAttr.get(item, 'data-md-subject');
				arrayElements.push(element);
			});
			
			var start = domAttr.get(dom.byId('js-start-current'), 'value');
			var textSearch = domAttr.get(dom.byId('js-text-search'), 'value');
			var elementString = arrayElements.join('+');
			var expandValue = domAttr.get(dom.byId('js-expand-value'), 'value');
			
			xhr(jsRoutes.controllers.Application.browse(start, textSearch, elementString, true, expandValue).url, {
				handleAs: "html"	
			}).then(function(data) {
				domConstruct.empty(dom.byId('js-browse-results-all'));
				domConstruct.place(data, dom.byId('js-browse-results-all'));
				domAttr.set(dom.byId('js-element-string'), 'value', elementString);
			});
		}
});