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
	
	'dojo/NodeList-traverse',
	'dojo/domReady!'
	], function(dom, domConstruct, query, on, array, lang, win, domAttr, domStyle, xhr) {
		
		// Expand or collapse single metadata record
		on(win.doc, '.search-metadata > .row:click, .browse-metadata > .row:click', function(e) {
			var description = query('.description', e.target.closest('.row'))[0];
			
			var descDisplay = domStyle.get(description, 'display');
			if(descDisplay === 'none') {
				domStyle.set(description, 'display', 'block');
			} else {
				domStyle.set(description, 'display', 'none');
			}
			
			setExpandAllCheckBox();
		});
		
		// Expand or collapse all metadata records
		on(win.doc, '.js-expand-all-evt:change', function(e) {
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
		on(win.doc, '.js-subject-select-all:click', function(e) {
			var subjects = query('.js-data-subject');
			array.forEach(subjects, function(item) {
				domAttr.set(item, 'checked', true);
			});
			
			filterSubjects();
		});
		
		// Uncheck all subjects
		on(win.doc, '.js-subject-select-none:click', function(e) {
			var subjects = query('.js-data-subject');
			array.forEach(subjects, function(item) {
				domAttr.set(item, 'checked', false);
			});
			
			filterSubjects();
		});
		
		// Filter event for metadata type
		on(win.doc, '.js-data-type:change', function(e) {
			filterTypes();
		});
		
		// Filter event for subjects
		on(win.doc, '.js-data-subject:change', function(e) {
			filterSubjects();
		});
		
		function filterTypes() {
			var arrayElements = [];
			var elements = query('.js-data-type:checked');
			array.forEach(elements, function(item) {
				var element = domAttr.get(item, 'data-md-type');
				arrayElements.push(element);
			});
			
			var elementString = arrayElements.join('+');
			var start = domAttr.get(dom.byId('js-start-current'), 'value');
			xhr(jsRoutes.controllers.Application.search(start, elementString, true).url, {
				handleAs: "html"	
			}).then(function(data) {
				domConstruct.empty(dom.byId('js-search-results-all'));
				domConstruct.place(data, dom.byId('js-search-results-all'));
				setExpandAllCheckBox();
			});
		}
		
		function filterSubjects() {
			var arrayElements = [];
			var elements = query('.js-data-subject:checked');
			array.forEach(elements, function(item) {
				var element = domAttr.get(item, 'data-md-subject');
				arrayElements.push(element);
			});
			
			var elementString = arrayElements.join('+');
			var start = domAttr.get(dom.byId('js-start-current'), 'value');
			xhr(jsRoutes.controllers.Application.browse(start, elementString, true).url, {
				handleAs: "html"	
			}).then(function(data) {
				domConstruct.empty(dom.byId('js-browse-results-all'));
				domConstruct.place(data, dom.byId('js-browse-results-all'));
				setExpandAllCheckBox();
			});
		}
		
		// Determine what the expand all checkbox should be set as
		function setExpandAllCheckBox() {
			var descList = query('.search-metadata > .row > .description, .browse-metadata > .row > .description');
			var descVisibleCount = 0;
			array.forEach(descList, function(item) {
				if(domStyle.get(item.closest('.search-metadata, .browse-metadata'), 'display') === 'block') {
					if(domStyle.get(item, 'display') === 'block') {
						descVisibleCount++;
					}
				}
			});
			
			var mdList = query('.search-metadata, .browse-metadata');
			var elVisibleCount = 0;
			array.forEach(mdList, function(item) {
				if(domStyle.get(item, 'display') === 'block') {
					elVisibleCount++;
				}
			});
			
			if(elVisibleCount === descVisibleCount) {
				domAttr.set(dom.byId('js-expand-all'), 'checked', true);
			} else {
				domAttr.set(dom.byId('js-expand-all'), 'checked', false);
			}
		}
});