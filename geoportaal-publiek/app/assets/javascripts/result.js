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
		
		// Handle sort
		on(win.doc, 'input[name=sort-records]:click', function(e) {
			var textSearch = domAttr.get(dom.byId('js-text-search'), 'value');
			
			if(domAttr.get(dom.byId('js-page'), 'value') === 'search') {
				var typesString = getDataTypes('.js-data-type:checked', 'data-md-type');
				var url = jsRoutes.controllers.Application.search(0, textSearch, typesString, true, true, getSortValue()).url;
				updateHTML(url, 'js-search-results-all', typesString);
			} else if(domAttr.get(dom.byId('js-page'), 'value') === 'browse') {
				var typesString = getDataTypes('.js-data-subject:checked', 'data-md-subject');
				var url = jsRoutes.controllers.Application.browse(0, textSearch, typesString, true, true, getSortValue()).url;
				updateHTML(url, 'js-browse-results-all', typesString);
			}
		});
		
		function getDataTypes(queryClass, typeAttribute) {
			var typeValues = [];
			var types = query(queryClass);
			array.forEach(types, function(type) {
				var value = domAttr.get(type, typeAttribute);
				typeValues.push(value);
			});
			
			return typeValues.join('+');
		}
		
		function getSortValue() {
			var sortDataset = dom.byId('sort-dataset');
			var sortDescription = dom.byId('sort-description');
			
			var sortValue = sortDataset.value;
			if(sortDescription.checked) {
				sortValue = sortDescription.value;
			}
			
			return sortValue;
		}
		
		function filterExpandTypes() {
			var start = domAttr.get(dom.byId('js-start-current'), 'value');
			var textSearch = domAttr.get(dom.byId('js-text-search'), 'value');
			var typesString = getDataTypes('.js-data-type:checked', 'data-md-type');
			var expandValue = domAttr.get(dom.byId('js-expand-value'), 'value');
			var expandValueBoolean = (expandValue === 'true');
			
			updateHTML(url, 'js-search-results-all', typesString);
		}
		
		function filterExpandSubjects() {
			var start = domAttr.get(dom.byId('js-start-current'), 'value');
			var textSearch = domAttr.get(dom.byId('js-text-search'), 'value');
			var typesString = getDataTypes('.js-data-subject:checked', 'data-md-subject');
			var expandValue = domAttr.get(dom.byId('js-expand-value'), 'value');
			var expandValueBoolean = (expandValue === 'true');
			var url = jsRoutes.controllers.Application.browse(start, textSearch, typesString, true, expandValueBoolean, getSortValue()).url;
			
			updateHTML(url, 'js-browse-results-all', typesString);
		}
		
		function updateHTML(url, containerId, typesString) {
			xhr(url, {
				handleAs: 'html'	
			}).then(function(data) {
				domConstruct.empty(dom.byId(containerId));
				domConstruct.place(data, dom.byId(containerId));
				domAttr.set(dom.byId('js-element-string'), 'value', typesString);
			});
		}
});