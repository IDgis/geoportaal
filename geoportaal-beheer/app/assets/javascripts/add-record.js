$(function () {
  $('[data-toggle="popover"]').popover()
})

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
	'dojo/parser',
	'dijit/registry',
	
	'dijit/form/DateTextBox',
	'dojo/NodeList-traverse',
	
	'dojo/domReady!'
	], function(dom, query, on, lang, win, array, domAttr, domConstruct, domStyle, xhr, parser, registry) {
		
		var create = domAttr.get(dom.byId('js-date-creation'), 'data-create');
		var validate = domAttr.get(dom.byId('js-date-creation'), 'data-validate');
		
		var datesArray = query('input[type=date]');
		if(!Modernizr.inputtypes.date) {
			array.forEach(datesArray, function(item) {
				var className = domAttr.get(item, 'class');
				var id = domAttr.get(item, 'id');
				var title = domAttr.get(item, 'title');
				var name = domAttr.get(item, 'name');
				var value = domAttr.get(item, 'value');
				var language = domAttr.get(item, 'data-language');
				
				var newItem = domConstruct.create('input');
				domAttr.set(newItem, 'class', className);
				domAttr.set(newItem, 'id', id);
				domAttr.set(newItem, 'type', 'text');
				domAttr.set(newItem, 'data-toggle', 'tooltip');
				domAttr.set(newItem, 'data-placement', 'top');
				domAttr.set(newItem, 'title', title);
				domAttr.set(newItem, 'name', name);
				domAttr.set(newItem, 'data-dojo-type', 'dijit/form/DateTextBox');
				domAttr.set(newItem, 'data-dojo-props', "lang:'" + language + "'");
				
				domConstruct.place(newItem, item.parentNode);
				domConstruct.destroy(item);
				
				parser.parse();
				
				if(value !== '') {
					registry.byId(id).set('value', value);
				}
			});
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
			domAttr.set(buttonNode, 'title', domAttr.get(dom.byId('js-remove-attachment-label'), 'value'));
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
			
			if(!Modernizr.inputtypes.date) {
				var dateCreation = domAttr.get(query('#js-date-creation ~ input')[0], 'value');
				var datePublication = domAttr.get(query('#js-date-publication ~ input')[0], 'value');
				var dateRevision = domAttr.get(query('#js-date-revision ~ input')[0], 'value');
				var dateValidFrom = domAttr.get(query('#js-date-valid-from ~ input')[0], 'value');
				var dateValidUntil = domAttr.get(query('#js-date-valid-until ~ input')[0], 'value');
			} else {
				var dateCreation = domAttr.get(dom.byId('js-date-creation'), 'value');
				var datePublication = domAttr.get(dom.byId('js-date-publication'), 'value');
				var dateRevision = domAttr.get(dom.byId('js-date-revision'), 'value');
				var dateValidFrom = domAttr.get(dom.byId('js-date-valid-from'), 'value');
				var dateValidUntil = domAttr.get(dom.byId('js-date-valid-until'), 'value');
			}
			
			formData.append('dateSourceCreation', dateCreation);
			formData.append('dateSourcePublication', datePublication);
			formData.append('dateSourceRevision', dateRevision);
			formData.append('dateSourceValidFrom', dateValidFrom);
			formData.append('dateSourceValidUntil', dateValidUntil);
			
			var dateCreation = domAttr.get(query('#js-date-creation ~ input')[0], 'value');
			var datePublication = domAttr.get(query('#js-date-publication ~ input')[0], 'value');
			var dateRevision = domAttr.get(query('#js-date-revision ~ input')[0], 'value');
			var dateValidFrom = domAttr.get(query('#js-date-valid-from ~ input')[0], 'value');
			var dateValidUntil = domAttr.get(query('#js-date-valid-until ~ input')[0], 'value');
			
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