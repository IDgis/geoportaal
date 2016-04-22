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
		if(dom.byId('js-expand-all')) {
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
		}
		
		// Check all subjects
		if(dom.byId('js-subject-select-all')) {
			on(dom.byId('js-subject-select-all'), 'click', function(e) {
				var subjects = query('.js-data-subject');
				array.forEach(subjects, function(item) {
					domAttr.set(item, 'checked', true);
				});
				
				filterTypeSubject(e);
				setExpandAllCheckBox();
			});
		}
		
		// Uncheck all subjects
		if(dom.byId('js-subject-select-none')) {
			on(dom.byId('js-subject-select-none'), 'click', function(e) {
				var subjects = query('.js-data-subject');
				array.forEach(subjects, function(item) {
					domAttr.set(item, 'checked', false);
				});
				
				filterTypeSubject(e);
				setExpandAllCheckBox();
			});
		}
		
		// Filter event for metadata type
		on(win.doc, '.js-data-type:change', function(e) {
			filterTypeSubject(e);
			setExpandAllCheckBox();
		});
		
		// Filter event for subjects
		on(win.doc, '.js-data-subject:change', function(e) {
			filterTypeSubject(e);
			setExpandAllCheckBox();
		});
		
		// Filter actions on metadata types or subjects
		function filterTypeSubject(e) {
			var boolType = domAttr.has(e.target, 'data-md-type');
			var arrayChecksValue = [];
			
			if(boolType) {
				var arrayResult = query('.search-metadata');
				var arrayChecks = query('.js-data-type:checked');
				array.forEach(arrayChecks, function(item) {
					var value = domAttr.get(item, 'data-md-type');
					arrayChecksValue.push(value);
				});
			} else {
				var arrayResult = query('.browse-metadata');
				var arrayChecks = query('.js-data-subject:checked');
				array.forEach(arrayChecks, function(item) {
					var value = domAttr.get(item, 'data-md-subject');
					arrayChecksValue.push(value);
				});
			}
			
			var arrayChecksValueJoin = arrayChecksValue.join([separator = ' ']);
			
			array.forEach(arrayResult, function(item) {
				if(boolType) {
					var typeVal = domAttr.get(item, 'data-md-type');
					
					domStyle.set(item, 'display', 'none');
					if(arrayChecksValueJoin.indexOf(typeVal) > -1) {
						domStyle.set(item, 'display', 'block');
					}
				} else {
					var subjectVal = domAttr.get(item, 'data-md-subject');
					var subjectValArray = subjectVal.split(' ');
					
					domStyle.set(item, 'display', 'none');
					var executeDisplay = true;
					array.forEach(subjectValArray, function(subject) {
						if(executeDisplay && arrayChecksValueJoin.indexOf(subject) > -1) {
							if(subject !== "") {
								domStyle.set(item, 'display', 'block');
								executeDisplay = false;
							}
						}
					});
				}
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