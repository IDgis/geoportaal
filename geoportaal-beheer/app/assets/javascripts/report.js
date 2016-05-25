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
			var spanLabel = domConstruct.create('span');
			
			domAttr.set(div, 'class', 'alert alert-info alert-dismissible');
			domAttr.set(div, 'role', 'alert');
			
			domAttr.set(button, 'type', 'button');
			domAttr.set(button, 'class', 'close');
			domAttr.set(button, 'data-dismiss', 'alert');
			domAttr.set(button, 'aria-label', 'Close');
			
			domAttr.set(spanClose, 'aria-hidden', 'true');
			domAttr.set(spanClose, 'innerHTML', '&times;');
			
			domAttr.set(spanLabel, 'innerHTML', domAttr.get(dom.byId('js-report-start-msg'), 'value'));
			
			domConstruct.place(div, query('.report-start')[0], 'after');
			domConstruct.place(button, div, 'last');
			domConstruct.place(spanClose, button, 'last');
			domConstruct.place(spanLabel, button, 'after');
		});
});