require([
	'dojo/dom',
	'dojo/query',
	'dojo/on',
	'dojo/_base/lang',
	'dojo/_base/window',
	'dojo/dom-attr',
	'dojo/dom-construct',
	'dojo/dom-style',

	'dojo/NodeList-traverse',
	'dojo/domReady!'
	], function(dom, query, on, lang, win, domAttr, domConstruct, domStyle) {
		
		var addAttachment = on(win.doc, '.js-add-attachment:click', function(e) {
			var attachment = query('.js-add-attachment').parents('.js-attachment')[0];
			var attachmentClone = lang.clone(attachment);
			var attachmentCloneDiv = query(attachmentClone).query('.js-attachment-input')[0];
			
			var buttonNode = domConstruct.create("button");
			domAttr.set(buttonNode, 'class', 'knop js-remove-attachment');
			domAttr.set(buttonNode, 'type', 'button');
			
			var spanNode = domConstruct.create("span");
			domAttr.set(spanNode, 'class', 'glyphicon glyphicon-remove');
			
			domConstruct.place(spanNode, buttonNode, 'last');
			domConstruct.place(buttonNode, attachmentCloneDiv, 'last');
			domConstruct.place(attachmentClone, dom.byId('js-group-attachment'), 'last');
		});
		
		var addAttachment = on(win.doc, '.js-remove-attachment:click', function(e) {
			var attachment = query(this).parents('.js-attachment')[0];
			domConstruct.destroy(attachment);
		});
		
		var handleOtherCreator = on(dom.byId('js-creator-select'), 'change', function(e) {
			if(domAttr.get(this, 'value') === 'other') {
				domStyle.set(dom.byId('js-other-creator'), 'display', 'block');
				domAttr.remove(dom.byId('js-creator-select'), 'name');
				domAttr.set(dom.byId('js-other-creator-input'), 'name', 'creator');
			} else {
				domAttr.set(dom.byId('js-other-creator-input'), 'value', '');
				domStyle.set(dom.byId('js-other-creator'), 'display', 'none');
				domAttr.remove(dom.byId('js-other-creator-input'), 'name');
				domAttr.set(dom.byId('js-creator-select'), 'name', 'creator');
			}
		});
		
		var validateForm = on(dom.byId('js-validate-form'), 'click', function(e) {
			var mandatoryInputs = query('.js-mandatory');
			var validateCounter = 0;
			var validateFields = [];
			var subjectChecked = query('.js-subject-input:checked').length;
			
			domStyle.set(dom.byId('js-form-approval'), 'display', 'none');
			
			for(var i = 0; i < mandatoryInputs.length; i++) {
				if(domAttr.get(mandatoryInputs[i], 'value') === '') {
					validateCounter++;
					validateFields.push(domAttr.get(mandatoryInputs[i], 'data-field'));
				}
			}
			
			var textWarning = 'De volgende velden moeten nog ingevuld worden: ';
			for(var i = 0; i < validateFields.length -1; i++) {
				textWarning += validateFields[i] + ', '
			}
			for(var i = validateFields.length -1; i < validateFields.length; i++) {
				textWarning += validateFields[i]
			}
			
			if(validateCounter > 0) {
				var textWarningElmnt = query('#js-form-warning-fields div b')[0];
				domAttr.set(textWarningElmnt, 'innerHTML', textWarning);
				
				domStyle.set(dom.byId('js-form-warning-fields'), 'display', 'block');
			} else {
				domStyle.set(dom.byId('js-form-warning-fields'), 'display', 'none');
			}
			
			if(subjectChecked === 0) {
				domStyle.set(dom.byId('js-form-warning-subjects'), 'display', 'block');
			} else {
				domStyle.set(dom.byId('js-form-warning-subjects'), 'display', 'none');
			}
			
			if(validateCounter === 0 && subjectChecked > 0) {
				domStyle.set(dom.byId('js-form-approval'), 'display', 'block');
			}
		});
});