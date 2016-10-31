require([
	'dojo/dom',
	'dojo/query',
	'dojo/on',
	'dojo/dom-attr',

	'dojo/domReady!'
	], function(dom, query, on, domAttr) {
		
		on(dom.byId('js-login'), 'click', function(e) {
			domAttr.set(this, 'type', 'button');
			var email = domAttr.get(dom.byId('js-login-email'), 'value');
			var finalEmail = email.trim();
			domAttr.set(dom.byId('js-login-email'), 'value', finalEmail);
			
			var form = dom.byId('js-login-form');
			form.submit();
		});
});