$(function () {
  $('[data-toggle="popover"]').popover()
})

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
			
			var recordsChecked = query('.js-record-checkbox:checked');
			var buttons = query('.js-restriction');
			domStyle.set(dom.byId('published-warning'), 'display', 'none');
			
			array.forEach(buttons, function(item) {
				domStyle.set(item, 'pointer-events', '');
				domStyle.set(item, 'filter', '');
				domStyle.set(item, '-webkit-box-shadow', '');
				domStyle.set(item, 'box-shadow', '');
				domStyle.set(item, 'opacity', '');
			});
			
			array.forEach(recordsChecked, function(item) {
				var role = domAttr.get(item, 'data-role');
				var status = domAttr.get(item, 'data-status');
				
				if(role === '2' && status === '4') {
					array.forEach(buttons, function(item) {
						domStyle.set(item, 'pointer-events', 'none');
						domStyle.set(item, 'filter', 'alpha(opacity=65)');
						domStyle.set(item, '-webkit-box-shadow', 'none');
						domStyle.set(item, 'box-shadow', 'none');
						domStyle.set(item, 'opacity', '.65');
					});
					
					domStyle.set(dom.byId('published-warning'), 'display', 'block');
				}
			});
		}
		
		var checkGeneral = on(win.doc, '.js-record-checkbox:change', function(e) {
			var pageCheck = domAttr.get(dom.byId('js-check-all'), 'data-page');
			var recordsChecked = query('.js-record-checkbox:checked');
			var buttons = query('.js-restriction');
			domStyle.set(dom.byId('published-warning'), 'display', 'none');
			
			array.forEach(buttons, function(item) {
				domStyle.set(item, 'pointer-events', '');
				domStyle.set(item, 'filter', '');
				domStyle.set(item, '-webkit-box-shadow', '');
				domStyle.set(item, 'box-shadow', '');
				domStyle.set(item, 'opacity', '');
			});
			
			array.forEach(recordsChecked, function(item) {
				var role = domAttr.get(item, 'data-role');
				var status = domAttr.get(item, 'data-status');
				
				if(role === '2' && status === '4') {
					array.forEach(buttons, function(item) {
						domStyle.set(item, 'pointer-events', 'none');
						domStyle.set(item, 'filter', 'alpha(opacity=65)');
						domStyle.set(item, '-webkit-box-shadow', 'none');
						domStyle.set(item, 'box-shadow', 'none');
						domStyle.set(item, 'opacity', '.65');
					});
					
					domStyle.set(dom.byId('published-warning'), 'display', 'block');
				}
			});
			
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
		
		var changeRecords = on(win.doc, '.js-check:click', function(e) {
			var recordsChecked = query('.js-record-checkbox:checked');
			var recordsSelectedMsg = query('.js-records-selected');
			var recordsNoneSelectedMsg = query('.js-records-none-selected');
			var executeBtns = query('.js-execute-btn');
			
			domAttr.set(dom.byId('js-status-records-count'), 'innerHTML', recordsChecked.length);
			domAttr.set(dom.byId('js-delete-records-count'), 'innerHTML', recordsChecked.length);
			domAttr.set(dom.byId('js-supplier-records-count'), 'innerHTML', recordsChecked.length);
			
			if(recordsChecked.length > 0) {
				array.forEach(recordsSelectedMsg, function(item) {
					domStyle.set(item, 'display', 'block');
				});
				
				array.forEach(recordsNoneSelectedMsg, function(item) {
					domStyle.set(item, 'display', 'none');
				});
				
				domStyle.set(dom.byId('perm-delete-checkbox'), 'display', 'block');
				
				array.forEach(executeBtns, function(item) {
					domStyle.set(item, 'display', 'inline-block');
				});
			} else {
				array.forEach(recordsSelectedMsg, function(item) {
					domStyle.set(item, 'display', 'none');
				});
				
				array.forEach(recordsNoneSelectedMsg, function(item) {
					domStyle.set(item, 'display', 'block');
				});
				
				domStyle.set(dom.byId('perm-delete-checkbox'), 'display', 'none');
				
				array.forEach(executeBtns, function(item) {
					domStyle.set(item, 'display', 'none');
				});
			}
			
			domConstruct.empty(dom.byId('js-sort-records'));
			
			array.forEach(recordsChecked, function(item) {
				var metadataUuid = domAttr.get(item, 'data-uuid');
				var input = domConstruct.create('input');
				domAttr.set(input, 'type', 'hidden');
				domAttr.set(input, 'name', 'recordsChecked[]');
				domAttr.set(input, 'value', metadataUuid);
				domConstruct.place(input, dom.byId('js-sort-records'), 'last');
			});
		});
		
		var deleteRecords = on(dom.byId('js-delete'), 'click', function(e) {
			var recordsChecked = query('.js-record-checkbox:checked');
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
			domConstruct.empty(dom.byId('js-status-records'));
			var statusValue = domAttr.get(this, 'data-status');
			domAttr.set(dom.byId('js-status-value'), 'value', statusValue);
			
			array.forEach(recordsChecked, function(item) {
				var metadataUuid = domAttr.get(item, 'data-uuid');
				var input = domConstruct.create('input');
				domAttr.set(input, 'type', 'hidden');
				domAttr.set(input, 'name', 'recordsChange[]');
				domAttr.set(input, 'value', metadataUuid);
				domConstruct.place(input, dom.byId('js-status-records'), 'last');
			});
			
			var statusForm = dom.byId('js-status-form');
			if(recordsChecked.length > 20) {
				$('#status-modal').modal({})
			} else {
				statusForm.submit();
			}
		});
		
		if(dom.byId('js-edit-supplier')) {
			var displaySupplierSelect = on(dom.byId('js-edit-supplier'), 'click', function(e) {
				domStyle.set(dom.byId('edit-supplier-select'), 'display', 'inline');
			});
		}
		
		if(dom.byId('edit-supplier-select')) {
			var changeSupplier = on(dom.byId('edit-supplier-select'), 'change', function(e) {
				var recordsChecked = query('.js-record-checkbox:checked');
				domConstruct.empty(dom.byId('js-supplier-records'));
				var supplierValue = domAttr.get(dom.byId('edit-supplier-select'), 'value');
				domAttr.set(dom.byId('js-supplier-value'), 'value', supplierValue);
				
				array.forEach(recordsChecked, function(item) {
					var metadataUuid = domAttr.get(item, 'data-uuid');
					var input = domConstruct.create('input');
					domAttr.set(input, 'type', 'hidden');
					domAttr.set(input, 'name', 'recordsChange[]');
					domAttr.set(input, 'value', metadataUuid);
					domConstruct.place(input, dom.byId('js-supplier-records'), 'last');
				});
				
				var supplierForm = dom.byId('js-supplier-form');
				if(recordsChecked.length > 20) {
					$('#supplier-modal').modal({})
				} else {
					supplierForm.submit();
				}
			});
		}
		
		var searchRecordsEnter = on(win.doc, '.js-search-input:keypress', function(e) {
			var code = e.keyCode
			
			if(code === 13) {
				console.log('called');
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
			}
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