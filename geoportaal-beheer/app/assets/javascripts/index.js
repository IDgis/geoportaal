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
		
		var indexPager = on(win.doc, '.js-index-page-btn:click', function(e) {
			var dataPage = domAttr.get(this, 'data-page');
			
			var pageOne = query('.js-index-page-1');
			var pageTwo = query('.js-index-page-2');
			var pageThree = query('.js-index-page-3');
			var pageFour = query('.js-index-page-4');
			var pageFive = query('.js-index-page-5');
			var pageSix = query('.js-index-page-6');
			var pageSeven = query('.js-index-page-7');
			var pageEight = query('.js-index-page-8');
			var pageNine = query('.js-index-page-9');
			var pageTen = query('.js-index-page-10');
			
			if(dataPage === '1') {setPage(1, pageOne);}
			if(dataPage === '2') {setPage(2, pageTwo);}
			if(dataPage === '3') {setPage(3, pageThree);}
			if(dataPage === '4') {setPage(4, pageFour);}
			if(dataPage === '5') {setPage(5, pageFive);}
			if(dataPage === '6') {setPage(6, pageSix);}
			if(dataPage === '7') {setPage(7, pageSeven);}
			if(dataPage === '8') {setPage(8, pageEight);}
			if(dataPage === '9') {setPage(9, pageNine);}
			if(dataPage === '10') {setPage(10, pageTen);}
		});
		
		function setPage(page, pageArray) {
			var allPages = query('.js-index-page');
			var allButtons = query('.js-page-status');
			
			if(pageArray.length !== 0) {
				array.forEach(allPages, function(item) {domStyle.set(item, 'display', 'none');});
				array.forEach(pageArray, function(item) {domStyle.set(item, 'display', 'table-row');});
				array.forEach(allButtons, function(item) {domAttr.set(item, 'class', 'js-page-status');});
				
				if(page === 1) {domAttr.set(dom.byId('js-page-status-1'), 'class', 'js-page-status active');}
				if(page === 2) {domAttr.set(dom.byId('js-page-status-2'), 'class', 'js-page-status active');}
				if(page === 3) {domAttr.set(dom.byId('js-page-status-3'), 'class', 'js-page-status active');}
				if(page === 4) {domAttr.set(dom.byId('js-page-status-4'), 'class', 'js-page-status active');}
				if(page === 5) {domAttr.set(dom.byId('js-page-status-5'), 'class', 'js-page-status active');}
				if(page === 6) {domAttr.set(dom.byId('js-page-status-6'), 'class', 'js-page-status active');}
				if(page === 7) {domAttr.set(dom.byId('js-page-status-7'), 'class', 'js-page-status active');}
				if(page === 8) {domAttr.set(dom.byId('js-page-status-8'), 'class', 'js-page-status active');}
				if(page === 9) {domAttr.set(dom.byId('js-page-status-9'), 'class', 'js-page-status active');}
				if(page === 10) {domAttr.set(dom.byId('js-page-status-10'), 'class', 'js-page-status active');}
			}	
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