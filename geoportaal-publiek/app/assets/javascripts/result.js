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
				var subjects = query('.js-subject');
				array.forEach(subjects, function(item) {
					domAttr.set(item, 'checked', true);
				});
			});
		}
		
		// Uncheck all subjects
		if(dom.byId('js-subject-select-none')) {
			on(dom.byId('js-subject-select-none'), 'click', function(e) {
				var subjects = query('.js-subject');
				array.forEach(subjects, function(item) {
					domAttr.set(item, 'checked', false);
				});
			});
		}
		
		// Filter on metadata type
		on(win.doc, '.js-data-type-check:change', function(e) {
			var checkTypes = query('.js-data-type-check');
			array.forEach(checkTypes, function(item) {
				var checkStatus = domAttr.get(item, 'checked');
				var dataType = domAttr.get(item, 'data-md-type');
				var mds = query('.search-metadata[data-md-type="' + dataType + '"]');
				
				if(checkStatus) {
					array.forEach(mds, function(item) {
						domStyle.set(item, 'display', 'block');
					});
				} else {
					array.forEach(mds, function(item) {
						domStyle.set(item, 'display', 'none');
					});
				}
			});
			
			setExpandAllCheckBox();
		});
		
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