if(!Modernizr.inputtypes.date) {
	$('#js-date-update-start').datepicker({
		dateFormat: 'dd-mm-yy',
		altField: '#js-hidden-date-update-start',
		altFormat: 'yy-mm-dd'
	});
	
	$('#js-date-update-end').datepicker({
		dateFormat: 'dd-mm-yy',
		altField: '#js-hidden-date-update-end',
		altFormat: 'yy-mm-dd'
	});
}

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
	
		var datesArray = query('input[type=date]');
		if(!Modernizr.inputtypes.date) {
			array.forEach(datesArray, function(item) {
				var name = domAttr.get(item, 'name');
				domAttr.remove(item, 'name');
				domAttr.set(query(item).query('~ input')[0], 'name', name);
			});
			
			array.forEach(datesArray, function(item) {
				var dateLocal = domAttr.get(item, 'data-date-value-local');
				var dateUS = domAttr.get(item, 'data-date-value-US');
				
				domAttr.set(item, 'value', dateLocal);
				domAttr.set(query(item).query('~ input')[0], 'value', dateUS);
			});
		} else {
			array.forEach(datesArray, function(item) {
				var date = domAttr.get(item, 'data-date-value-US');
				domAttr.set(item, 'value', date);
			});
		}
		
		var deleteRecords = on(dom.byId('js-delete'), 'click', function(e) {
			var recordsChecked = query('.js-record-checkbox:checked');
			
			array.forEach(recordsChecked, function(item) {
				var datasetId = domAttr.get(item, 'data-id');
				
				xhr(jsRoutes.controllers.Index.deleteMetadata(datasetId).url)
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
				
				xhr(jsRoutes.controllers.Index.changeStatus(datasetId, status).url)
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
				
				xhr(jsRoutes.controllers.Index.changeSupplier(datasetId, supplier).url)
					.then(function() {
						document.location.reload();
					});
			});
			
		});
});