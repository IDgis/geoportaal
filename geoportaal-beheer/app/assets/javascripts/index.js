$('.js-date').datepicker({
	dateFormat: 'dd-mm-yy'
});

require([
	'dojo/dom',
	'dojo/query',
	'dojo/on',
	'dojo/_base/array',
	'dojo/_base/lang',
	'dojo/_base/window',
	'dojo/dom-attr',
	'dojo/dom-construct',
	'dojo/dom-style',
	'dojo/request/xhr',
	'dojo/NodeList-traverse',
	
	'dojo/domReady!'
	], function(dom, query, on, array, lang, win, domAttr, domConstruct, domStyle, xhr) {
	
		var deleteRecords = on(dom.byId('js-delete'), 'click', function(e) {
			var recordsChecked = query('.js-record-checkbox:checked');
			
			array.forEach(recordsChecked, function(item) {
				var datasetId = domAttr.get(item, 'data-id');
				
				xhr(jsRoutes.controllers.Delete.delete(datasetId).url)
					.then(function() {
						document.location.reload();
					});
			});
			
		});
		
		var changeStatus = on(win.doc, '.js-status:click', function(e) {
			var recordsChecked = query('.js-record-checkbox:checked');
			var status = domAttr.get(this, 'data-status');
			
			array.forEach(recordsChecked, function(item) {
				var datasetId = domAttr.get(item, 'data-id');
				
				xhr(jsRoutes.controllers.Edit.changeStatus(datasetId, status).url)
					.then(function() {
						document.location.reload();
					});
			});
			
		});
		
		var displaySupplierSelect = on(dom.byId('js-edit-supplier'), 'click', function(e) {
			domStyle.set(dom.byId('js-edit-supplier-select'), 'display', 'inline');
		});
		
		var changeSupplier = on(dom.byId('js-edit-supplier-select'), 'change', function(e) {
			var recordsChecked = query('.js-record-checkbox:checked');
			var supplier = domAttr.get(this, 'value');
			
			array.forEach(recordsChecked, function(item) {
				var datasetId = domAttr.get(item, 'data-id');
				
				xhr(jsRoutes.controllers.Edit.changeSupplier(datasetId, supplier).url)
					.then(function() {
						document.location.reload();
					});
			});
			
		});
});