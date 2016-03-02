$(function () {
  $('[data-toggle="popover"]').popover()
})

if(!Modernizr.inputtypes.date) {
	$('#js-date-creation').datepicker({
		dateFormat: 'dd-mm-yy',
		altField: '#js-hidden-date-creation',
		altFormat: 'yy-mm-dd'
	});
	
	$('#js-date-publication').datepicker({
		dateFormat: 'dd-mm-yy',
		altField: '#js-hidden-date-publication',
		altFormat: 'yy-mm-dd'
	});
	
	$('#js-date-revision').datepicker({
		dateFormat: 'dd-mm-yy',
		altField: '#js-hidden-date-revision',
		altFormat: 'yy-mm-dd'
	});
	
	$('#js-date-valid-from').datepicker({
		dateFormat: 'dd-mm-yy',
		altField: '#js-hidden-date-valid-from',
		altFormat: 'yy-mm-dd'
	});
	
	$('#js-date-valid-until').datepicker({
		dateFormat: 'dd-mm-yy',
		altField: '#js-hidden-date-valid-until',
		altFormat: 'yy-mm-dd'
	});
}

require([
	'dojo/dom',
	'dojo/query',
	'dojo/on',
	'dojo/_base/lang',
	'dojo/_base/window',
	'dojo/_base/array',
	'dojo/dom-attr',
	'dojo/dom-construct',
	'dojo/dom-style',
	'dojo/request/xhr',
	'dojo/NodeList-traverse',
	
	'dojo/domReady!'
	], function(dom, query, on, lang, win, array, domAttr, domConstruct, domStyle, xhr) {
		
		var create = domAttr.get(dom.byId('js-date-creation'), 'data-create');
		var validate = domAttr.get(dom.byId('js-date-creation'), 'data-validate');
		
		var datesArray = query('input[type=date]');
		if(!Modernizr.inputtypes.date) {
			array.forEach(datesArray, function(item) {
				var name = domAttr.get(item, 'name');
				domAttr.remove(item, 'name');
				domAttr.set(query(item).query('~ input')[0], 'name', name);
			});

			if(create === "true") {
				if(validate === "true") {
					array.forEach(datesArray, function(item) {
						var validateDate = domAttr.get(item, 'data-date-value-US');
						var validateDateLocal = domAttr.get(item, 'data-date-value-local');
						
						domAttr.set(item, 'value', validateDateLocal);
						domAttr.set(query(item).query('~ input')[0], 'value', validateDate);
					});
				} else {
					var todayLocal = domAttr.get(dom.byId('js-date-creation'), 'data-date-create-today-local');
					var todayUS = domAttr.get(dom.byId('js-date-creation'), 'data-date-create-today-US');
					
					domAttr.set(dom.byId('js-date-creation'), 'value', todayLocal);
					domAttr.set(dom.byId('js-hidden-date-creation'), 'value', todayUS);
				}
			} else {
				array.forEach(datesArray, function(item) {
					var dateLocal = domAttr.get(item, 'data-date-value-local');
					var dateUS = domAttr.get(item, 'data-date-value-US');
					
					domAttr.set(item, 'value', dateLocal);
					domAttr.set(query(item).query('~ input')[0], 'value', dateUS);
				});
			}
		} else {
			if(create === "true") {
				if(validate === "true") {
					array.forEach(datesArray, function(item) {
						var validateDate = domAttr.get(item, 'data-date-value-US');
						domAttr.set(item, 'value', validateDate);
					});
				} else {
					var today = domAttr.get(dom.byId('js-date-creation'), 'data-date-create-today-US');
					domAttr.set(dom.byId('js-date-creation'), 'value', today);
				}
			} else {
				array.forEach(datesArray, function(item) {
					var date = domAttr.get(item, 'data-date-value-US');
					
					domAttr.set(item, 'value', date);
				});
			}
		}
	
		var addAttachment = on(win.doc, '.js-add-attachment:click', function(e) {
			var attachment = query('.js-add-attachment').parents('.js-attachment')[0];
			var attachmentClone = lang.clone(attachment);
			var attachmentCloneDiv = query(attachmentClone).query('.js-attachment-input')[0];
			var attachmentCloneTooltip = query(attachmentClone).query('.js-attachment-tooltip')[0];
			domConstruct.destroy(attachmentCloneTooltip);
			
			var buttonNode = domConstruct.create("button");
			domAttr.set(buttonNode, 'class', 'knop js-remove-attachment');
			domAttr.set(buttonNode, 'type', 'button');
			domAttr.set(buttonNode, 'title', 'Bijlage verwijderen');
			domAttr.set(buttonNode, 'tabindex', '-1');
			
			var spanNode = domConstruct.create("span");
			domAttr.set(spanNode, 'class', 'glyphicon glyphicon-remove');
			
			domConstruct.place(spanNode, buttonNode, 'last');
			domConstruct.place(buttonNode, attachmentCloneDiv, 'last');
			domConstruct.place(attachmentClone, dom.byId('js-group-attachment'), 'last');
		});
		
		var removeAttachment = on(win.doc, '.js-remove-attachment:click', function(e) {
			var attachment = query(this).parents('.js-attachment')[0];
			domConstruct.destroy(attachment);
		});
		
		var emptyAttachment = on(win.doc, '.js-empty-attachment:click', function(e) {
			var inputAttachment = query(this).siblings('.input-attachment')[0];
			domAttr.set(inputAttachment, 'value', '');
		});
		
		var removeSavedAttachment = on(win.doc, '.delete-attachment-button:click', function(e) {
			var attToDel = query(this).parents('.attachment-file')[0];
			var attachmentName = domAttr.get(query(this).query('~ span')[0], 'innerHTML');
			var idDelEl = domConstruct.create('input');
			
			domAttr.set(idDelEl, 'type', 'hidden');
			domAttr.set(idDelEl, 'value', attachmentName);
			domAttr.set(idDelEl,  'name', 'deletedAttachment[]')
			domConstruct.place(idDelEl, dom.byId('deleted-attachments'));
			
			domConstruct.destroy(attToDel);
		});
		
		var creatorSelect = dom.byId('js-creator-select');
		if(domAttr.get(creatorSelect, 'value') === 'other') {
			domStyle.set(dom.byId('js-other-creator'), 'display', 'block');
		} else {
			domStyle.set(dom.byId('js-other-creator'), 'display', 'none');
		}
		
		var handleOtherCreator = on(creatorSelect, 'change', function(e) {
			if(domAttr.get(this, 'value') === 'other') {
				domStyle.set(dom.byId('js-other-creator'), 'display', 'block');
			} else {
				domStyle.set(dom.byId('js-other-creator'), 'display', 'none');
			}
		});
		
		var saveRecord = on(dom.byId('js-save-form'), 'click', function(e) {
			domAttr.set(this, 'type', 'button');
			var form = dom.byId('js-form');
			
			var id = domAttr.get(this, 'data-id');
			var formData = new FormData();
			
			var titleVal = domAttr.get(dom.byId('js-title'), 'value');
			var descriptionVal = domAttr.get(dom.byId('js-description'), 'value');
			var locationVal = domAttr.get(dom.byId('js-location'), 'value');
			var fileIdVal = domAttr.get(dom.byId('js-file-id'), 'value');
			
			var dateCreationChrome = domAttr.get(dom.byId('js-date-creation'), 'value');
			var dateCreationRest = domAttr.get(dom.byId('js-hidden-date-creation'), 'value');
			var datePublicationChrome = domAttr.get(dom.byId('js-date-publication'), 'value');
			var datePublicationRest = domAttr.get(dom.byId('js-hidden-date-publication'), 'value');
			var dateRevisionChrome = domAttr.get(dom.byId('js-date-revision'), 'value');
			var dateRevisionRest = domAttr.get(dom.byId('js-hidden-date-revision'), 'value');
			var dateValidFromChrome = domAttr.get(dom.byId('js-date-valid-from'), 'value');
			var dateValidFromRest = domAttr.get(dom.byId('js-hidden-date-valid-from'), 'value');
			var dateValidUntilChrome = domAttr.get(dom.byId('js-date-valid-until'), 'value');
			var dateValidUntilRest = domAttr.get(dom.byId('js-hidden-date-valid-until'), 'value');
			
			var subjectList = query('.js-subject-input:checked');
			var creatorVal = domAttr.get(dom.byId('js-creator-select'), 'value');
			
			formData.append('title', titleVal);
			formData.append('description', descriptionVal);
			formData.append('location', locationVal);
			formData.append('fileId', fileIdVal);
			formData.append('creator', creatorVal);
			
			if(creatorVal === 'other') {
				var otherCreatorVal = domAttr.get(dom.byId('js-other-creator-input'), 'value');
				
				formData.append('creatorOther', otherCreatorVal);
			}
			if(!Modernizr.inputtypes.date) {
				formData.append('dateSourceCreation', dateCreationRest);
				formData.append('dateSourcePublication', datePublicationRest);
				formData.append('dateSourceRevision', dateRevisionRest);
				formData.append('dateSourceValidFrom', dateValidFromRest);
				formData.append('dateSourceValidUntil', dateValidUntilRest);
			} else {
				formData.append('dateSourceCreation', dateCreationChrome);
				formData.append('dateSourcePublication', datePublicationChrome);
				formData.append('dateSourceRevision', dateRevisionChrome);
				formData.append('dateSourceValidFrom', dateValidFromChrome);
				formData.append('dateSourceValidUntil', dateValidUntilChrome);
			}
			array.forEach(subjectList, function(item) {
				var subjectValue = domAttr.get(item, 'value');
				formData.append('subject[]', subjectValue);
			});
			
			
			xhr(jsRoutes.controllers.Metadata.validateForm(id).url, {
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