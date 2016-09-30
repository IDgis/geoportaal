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
			
			domAttr.set(div, 'class', 'alert alert-info alert-dismissible');
			domAttr.set(div, 'role', 'alert');
			
			domAttr.set(button, 'type', 'button');
			domAttr.set(button, 'class', 'close');
			domAttr.set(button, 'data-dismiss', 'alert');
			domAttr.set(button, 'aria-label', 'Close');
			
			domAttr.set(spanClose, 'aria-hidden', 'true');
			domAttr.set(spanClose, 'innerHTML', '&times;');
			
			domAttr.set(p1, 'innerHTML', domAttr.get(dom.byId('js-report-start-msg-1'), 'value'));
			domAttr.set(p2, 'innerHTML', domAttr.get(dom.byId('js-report-start-msg-2'), 'value'));
			domAttr.set(p3, 'innerHTML', domAttr.get(dom.byId('js-report-start-msg-3'), 'value'));
			
			domConstruct.place(div, query('.report-start')[0], 'after');
			domConstruct.place(button, div, 'last');
			domConstruct.place(spanClose, button, 'last');
			domConstruct.place(p1, button, 'after');
			domConstruct.place(p2, p1, 'after');
			domConstruct.place(p3, p2, 'after');
		});
});