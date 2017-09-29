$(function () {
	$('[data-toggle="popover"]').popover()
})

require([
	'dojo/dom',
	'dojo/query',
	'dojo/on',
	'dojo/_base/array',
	'dojo/dom-attr',
	'dojo/dom-construct',
	
	'dojo/domReady!'
	], function(dom, query, on, array, domAttr, domConstruct) {
	
		on(dom.byId('js-report-start-btn'), 'click', function(e) {
			var alerts = query('.alert-dismissible');
			array.forEach(alerts, function(item) {
				domConstruct.destroy(item);
			});
			
			var div = domConstruct.create('div');
			var button = domConstruct.create('button');
			var spanClose = domConstruct.create('span');
			var p1 = domConstruct.create('p');
			var p2 = domConstruct.create('p');
			var p3 = domConstruct.create('p');
			var p4 = domConstruct.create('p');
			var ol = domConstruct.create('ol');
			var l1 = domConstruct.create('li');
			var l2 = domConstruct.create('li');
			var l3 = domConstruct.create('li');
			var l4 = domConstruct.create('li');
			var l5 = domConstruct.create('li');
			var l6 = domConstruct.create('li');
			
			domAttr.set(div, 'class', 'alert alert-info alert-dismissible');
			domAttr.set(div, 'role', 'alert');
			
			domAttr.set(button, 'type', 'button');
			domAttr.set(button, 'class', 'close');
			domAttr.set(button, 'data-dismiss', 'alert');
			domAttr.set(button, 'aria-label', 'Close');
			
			domAttr.set(spanClose, 'aria-hidden', 'true');
			domAttr.set(spanClose, 'innerHTML', '&times;');
			
			domAttr.set(p4, 'id', 'utf-8-info');
			domAttr.set(ol, 'id', 'ordered-list-instruction');
			
			domAttr.set(p1, 'innerHTML', domAttr.get(dom.byId('js-report-start-msg-1'), 'value'));
			domAttr.set(p2, 'innerHTML', domAttr.get(dom.byId('js-report-start-msg-2'), 'value'));
			domAttr.set(p3, 'innerHTML', domAttr.get(dom.byId('js-report-start-msg-3'), 'value'));
			domAttr.set(p4, 'innerHTML', domAttr.get(dom.byId('js-report-start-msg-4'), 'value'));
			domAttr.set(l1, 'innerHTML', domAttr.get(dom.byId('js-report-start-msg-5.1'), 'value'));
			domAttr.set(l2, 'innerHTML', domAttr.get(dom.byId('js-report-start-msg-5.2'), 'value'));
			domAttr.set(l3, 'innerHTML', domAttr.get(dom.byId('js-report-start-msg-5.3'), 'value'));
			domAttr.set(l4, 'innerHTML', domAttr.get(dom.byId('js-report-start-msg-5.4'), 'value'));
			domAttr.set(l5, 'innerHTML', domAttr.get(dom.byId('js-report-start-msg-5.5'), 'value'));
			domAttr.set(l6, 'innerHTML', domAttr.get(dom.byId('js-report-start-msg-5.6'), 'value'));
			
			domConstruct.place(div, query('.report-start')[0], 'after');
			domConstruct.place(button, div, 'last');
			domConstruct.place(spanClose, button, 'last');
			domConstruct.place(p1, button, 'after');
			domConstruct.place(p2, p1, 'after');
			domConstruct.place(p3, p2, 'after');
			domConstruct.place(p4, p3, 'after');
			domConstruct.place(ol, p4, 'after');
			domConstruct.place(l1, ol, 'last');
			domConstruct.place(l2, ol, 'last');
			domConstruct.place(l3, ol, 'last');
			domConstruct.place(l4, ol, 'last');
			domConstruct.place(l5, ol, 'last');
			domConstruct.place(l6, ol, 'last');
		});
});