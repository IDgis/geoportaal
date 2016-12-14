require([
	'dojo/dom',
	'dojo/dom-construct',
	'dojo/dom-class',
	'dojo/query',
	'dojo/on',
	'dojo/_base/array',
	'dojo/_base/lang',
	'dojo/_base/window',
	'dojo/dom-attr',
	'dojo/dom-style',
	'dojo/request/xhr',
	
	'dojo/domReady!'
	], function(dom, domConstruct, domClass, query, on, array, lang, win, domAttr, domStyle, xhr) {
		
		// Expand or collapse single metadata record
		on(win.doc, '.md-title:click', function(e) {
			var description = query('.description[data-uuid=' + domAttr.get(e.target, 'data-uuid') + ']')[0];
			
			var descDisplay = domStyle.get(description, 'display');
			if(descDisplay === 'none') {
				domClass.remove(description, 'metadata-collapsed');
			} else {
				domClass.add(description, 'metadata-collapsed');
			}
		});
		
		// Expand or collapse all metadata records
		on(win.doc, '.js-expand-all-evt:change', function(e) {
			var descList = query('.search-metadata > .row > .description, .browse-metadata > .row > .description');
			var checkStatus = domAttr.get(dom.byId('js-expand-all'), 'checked');
			
			if(checkStatus === true) {
				domAttr.set(dom.byId('js-expand-value'), 'value', true);
			} else {
				domAttr.set(dom.byId('js-expand-value'), 'value', false);
			}
			
			array.forEach(descList, function(item) {
				if(checkStatus === true) {
					domClass.remove(item, 'metadata-collapsed');
				} else {
					domClass.add(item, 'metadata-collapsed');
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
			var expandValueBoolean = (expandValue === 'true');
			
			xhr(jsRoutes.controllers.Application.search(start, textSearch, elementString, true, expandValueBoolean).url, {
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
			var expandValueBoolean = (expandValue === 'true');
			
			xhr(jsRoutes.controllers.Application.browse(start, textSearch, elementString, true, expandValueBoolean).url, {
				handleAs: "html"	
			}).then(function(data) {
				domConstruct.empty(dom.byId('js-browse-results-all'));
				domConstruct.place(data, dom.byId('js-browse-results-all'));
				domAttr.set(dom.byId('js-element-string'), 'value', elementString);
			});
		}
});