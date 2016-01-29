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
		
		var searchHide = on(dom.byId('btn-search-hide'), 'click', function(e) {
			var searchStatus = domAttr.get(this, 'data-search-status');
			var searchBlock = dom.byId('search-block');
			
			if(searchStatus === 'true') {
				domStyle.set(searchBlock, 'display', 'none');
				domAttr.set(this, 'data-search-status', 'false');
				domAttr.set(dom.byId('btn-search-hide-text'), 'innerHTML', 'Zoekblok uitklappen');
			} else {
				domStyle.set(searchBlock, 'display', 'block');
				domAttr.set(this, 'data-search-status', 'true');
				domAttr.set(dom.byId('btn-search-hide-text'), 'innerHTML', 'Zoekblok dichtklappen');
			}
		});
		
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
		
		var searchRecords = on(dom.byId('search-button'), 'click', function(e) {
			var searchButton = dom.byId('search-button');
			var form = dom.byId('js-form');
			
			var formData = new FormData();
			
			var dateStartChrome = domAttr.get(dom.byId('js-date-update-start'), 'value');
			var dateStartRest = domAttr.get(dom.byId('js-hidden-date-update-start'), 'value');
			var dateEndChrome = domAttr.get(dom.byId('js-date-update-end'), 'value');
			var dateEndRest = domAttr.get(dom.byId('js-hidden-date-update-end'), 'value');
			
			if(!Modernizr.inputtypes.date) {
				formData.append('dateUpdateStart', dateStartRest);
				formData.append('dateUpdateEnd', dateEndRest);
			} else {
				formData.append('dateUpdateStart', dateStartChrome);
				formData.append('dateUpdateEnd', dateEndChrome);
			}
			
			xhr(jsRoutes.controllers.Index.validateForm().url, {
					handleAs: "html",
					data: formData,
					method: "POST"	
			}).then(function(data) {
				var nfBoolean = data.indexOf('data-error="true"') > -1;
				
				if(nfBoolean) {
					if(dom.byId('js-form-validation-result')) {
						domConstruct.destroy(dom.byId('js-form-validation-result'));
					}
					
					var result = dom.byId('js-form-validation');
					domConstruct.place(data, result);
				} else {
					form.submit();
				}
			});
		});
});