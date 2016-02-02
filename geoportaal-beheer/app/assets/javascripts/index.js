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
		
		var allRecords = query('.js-record-checkbox');
		var checkAll = on(dom.byId('js-check-all'), 'click', function(e) {
			var pageCheck = domAttr.get(this, 'data-page');
			
			if(pageCheck === '1') {
				setCheckAll(0, 20);
			} if(pageCheck === '2') {
				setCheckAll(20, 40);
			} if(pageCheck === '3') {
				setCheckAll(40, 60);
			} if(pageCheck === '4') {
				setCheckAll(60, 80);
			} if(pageCheck === '5') {
				setCheckAll(80, 100);
			} if(pageCheck === '6') {
				setCheckAll(100, 120);
			} if(pageCheck === '7') {
				setCheckAll(120, 140);
			} if(pageCheck === '8') {
				setCheckAll(140, 160);
			} if(pageCheck === '9') {
				setCheckAll(160, 180);
			} if(pageCheck === '10') {
				setCheckAll(180, 200);
			}
		});
		
		function setCheckAll(start, end) {
			if(allRecords.length < end) {
				var realEnd = allRecords.length;
			} else {
				var realEnd = end;
			}
			
			for(var i = start; i < realEnd; i++) {
				if(domAttr.get(dom.byId('js-check-all'), 'checked')) {
					domAttr.set(allRecords[i], 'checked', true);
				} else {
					domAttr.set(allRecords[i], 'checked', false);
				}
			}
		}
		
		var checkGeneral = on(win.doc, '.js-record-checkbox:change', function(e) {
			var pageCheck = domAttr.get(dom.byId('js-check-all'), 'data-page');
			
			if(pageCheck === '1') {
				setCheckGeneral(0, 20);
			} if(pageCheck === '2') {
				setCheckGeneral(20, 40);
			} if(pageCheck === '3') {
				setCheckGeneral(40, 60);
			} if(pageCheck === '4') {
				setCheckGeneral(60, 80);
			} if(pageCheck === '5') {
				setCheckGeneral(80, 100);
			} if(pageCheck === '6') {
				setCheckGeneral(100, 120);
			} if(pageCheck === '7') {
				setCheckGeneral(120, 140);
			} if(pageCheck === '8') {
				setCheckGeneral(140, 160);
			} if(pageCheck === '9') {
				setCheckGeneral(160, 180);
			} if(pageCheck === '10') {
				setCheckGeneral(180, 200);
			}
		});
		
		function setCheckGeneral(start, end) {
			if(allRecords.length < end) {
				var realEnd = allRecords.length;
			} else {
				var realEnd = end;
			}
			
			var count = start;
			for(var j = start; j < realEnd; j++) {
				if(domAttr.get(allRecords[j], 'checked')) {
					count++;
				}
			}
			
			if(count === realEnd) {
				domAttr.set(dom.byId('js-check-all'), 'checked', true);
			} else {
				domAttr.set(dom.byId('js-check-all'), 'checked', false);
			}
		}
		
		var indexPager = on(win.doc, '.js-index-page-btn:click', function(e) {
			var dataPage = domAttr.get(this, 'data-page');
			domAttr.set(dom.byId('js-check-all'), 'data-page', dataPage);
			
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
			
			if(dataPage === '1') {
				setIndexPage(1, pageOne);
				setCheckGeneral(0, 20);}
			if(dataPage === '2') {
				setIndexPage(2, pageTwo);
				setCheckGeneral(20, 40);}
			if(dataPage === '3') {
				setIndexPage(3, pageThree);
				setCheckGeneral(40, 60);}
			if(dataPage === '4') {
				setIndexPage(4, pageFour);
				setCheckGeneral(60, 80);}
			if(dataPage === '5') {
				setIndexPage(5, pageFive);
				setCheckGeneral(80, 100);}
			if(dataPage === '6') {
				setIndexPage(6, pageSix);
				setCheckGeneral(100, 120);}
			if(dataPage === '7') {
				setIndexPage(7, pageSeven);
				setCheckGeneral(120, 140);}
			if(dataPage === '8') {
				setIndexPage(8, pageEight);
				setCheckGeneral(140, 160);}
			if(dataPage === '9') {
				setIndexPage(9, pageNine);
				setCheckGeneral(160, 180);}
			if(dataPage === '10') {
				setIndexPage(10, pageTen);
				setCheckGeneral(180, 200);}
		});
		
		function setIndexPage(page, pageArray) {
			var allPages = query('.js-index-page');
			var allButtons = query('.js-page-status');
			
			if(pageArray.length !== 0) {
				array.forEach(allPages, function(item) {
					domStyle.set(item, 'display', 'none');
				});
				array.forEach(pageArray, function(item) {
					domStyle.set(item, 'display', 'table-row');
				});
				array.forEach(allButtons, function(item) {
					domAttr.set(item, 'class', 'js-page-status');
				});
				
				if(page === 1) {
					domAttr.set(dom.byId('js-page-status-1'), 'class', 'js-page-status active');
				} if(page === 2) {
					domAttr.set(dom.byId('js-page-status-2'), 'class', 'js-page-status active');
				} if(page === 3) {
					domAttr.set(dom.byId('js-page-status-3'), 'class', 'js-page-status active');
				} if(page === 4) {
					domAttr.set(dom.byId('js-page-status-4'), 'class', 'js-page-status active');
				} if(page === 5) {
					domAttr.set(dom.byId('js-page-status-5'), 'class', 'js-page-status active');
				} if(page === 6) {
					domAttr.set(dom.byId('js-page-status-6'), 'class', 'js-page-status active');
				} if(page === 7) {
					domAttr.set(dom.byId('js-page-status-7'), 'class', 'js-page-status active');
				} if(page === 8) {
					domAttr.set(dom.byId('js-page-status-8'), 'class', 'js-page-status active');
				} if(page === 9) {
					domAttr.set(dom.byId('js-page-status-9'), 'class', 'js-page-status active');
				} if(page === 10) {
					domAttr.set(dom.byId('js-page-status-10'), 'class', 'js-page-status active');
				}
			}	
		}
		
		var changeRecords = on(win.doc, '.js-check:change', function(e) {
			var recordsChecked = query('.js-record-checkbox:checked');
			domAttr.set(dom.byId('js-delete-records-count'), 'innerHTML', recordsChecked.length);
		});
		
		var deleteRecords = on(dom.byId('js-delete'), 'click', function(e) {
			var recordsChecked = query('.js-record-checkbox:checked');
			
			domAttr.remove(dom.byId('js-delete-execute'), 'disabled');
			if(recordsChecked.length === 0) {
				domAttr.set(dom.byId('js-delete-execute'), 'disabled', 'disabled');
			}
			
			domConstruct.empty(dom.byId('js-delete-records'));
			
			array.forEach(recordsChecked, function(item) {
				var metadataUuid = domAttr.get(item, 'data-uuid');
				var input = domConstruct.create('input');
				domAttr.set(input, 'type', 'hidden');
				domAttr.set(input, 'name', 'recordsToDel[]');
				domAttr.set(input, 'value', metadataUuid);
				domConstruct.place(input, dom.byId('js-delete-records'), 'last');
			});
		});
		
		var changeStatus = on(win.doc, '.js-status:click', function(e) {
			var recordsChecked = query('.js-record-checkbox:checked');
			var status = domAttr.get(this, 'data-status');
			
			array.forEach(recordsChecked, function(item) {
				var datasetId = domAttr.get(item, 'data-uuid');
				
				xhr(jsRoutes.controllers.Index.changeStatus(datasetId, status).url)
					.then(function() {
						document.location.reload();
					});
			});
			
		});
		
		var displaySupplierSelect = on(dom.byId('js-edit-supplier'), 'click', function(e) {
			domStyle.set(dom.byId('edit-supplier-select'), 'display', 'inline');
		});
		
		var changeSupplier = on(dom.byId('edit-supplier-select'), 'change', function(e) {
			var recordsChecked = query('.js-record-checkbox:checked');
			domConstruct.empty(dom.byId('js-supplier-records'));
			
			array.forEach(recordsChecked, function(item) {
				var metadataUuid = domAttr.get(item, 'data-uuid');
				var input = domConstruct.create('input');
				domAttr.set(input, 'type', 'hidden');
				domAttr.set(input, 'name', 'recordsChange[]');
				domAttr.set(input, 'value', metadataUuid);
				domConstruct.place(input, dom.byId('js-supplier-records'), 'last');
			});
			
			var supplierForm = dom.byId('js-supplier-form');
			supplierForm.submit();
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