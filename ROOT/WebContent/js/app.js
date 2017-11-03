function debug(s) {
//	if(!$('#debug').length) {
//		$('body').prepend('<div id="debug" style="position: fixed; right: 0px; bottom: 0px; width: 400px; height: 120px; color: red; border:1px solid red;"></div>');
//	}
//	$('#debug').prepend(s + '<br />');
}

/**
 * @param text
 * @param time - hide after (ms)
 * @param style 
 * 	 	- 'success' for green color 
 * 		- 'warning' for yellow color 
 * 		- 'error'   for red color 
 * 		- 'info'    for blue color
 */
function notify(text, time, style) {
	if (!(style=='success' || style=='warning' || style=='error' || style=='info')){
		return;
	}
	if (typeof time !== 'number'){
		return;
	}
	
	if(!$('#notif').length) {
		$('body').prepend('<div id="notif"></div>');
	}
	
	var msg = $('<div class="msg ' + style + '">' + text + '</div>')
	$('#notif').append(msg);

	setTimeout(function() {
		msg.remove();
		
		if ($('#notif').html() == '') {
			$('#notif').remove();
		}
	}, time);
}



$(function() {
	
//	var test = new Test();
//	
//	notify(test.a, 2000, 'warning');
//	notify(test.b, 2000, 'warning');
//	
//	test.a = 3;
//	test.b = 4;
//	notify(test.a, 2000, 'warning');
//	notify(test.b, 2000, 'warning');
//
//	notify(test.test1(), 2000, 'error');
//	notify(test.test3(), 2000, 'error');
//	notify(test.test2(), 2000, 'error');
	
//	notify('test', 2000, 'warning');
//	notify('test1', 1000, 'info');
//	notify('test2', 6000, 'error');
//	notify('test3', 3000, 'success');
//	notify('test4!!!!!!!!!!!!!', 10000 , 'error');
	
	var data = [
		{screenId: "abc123", owner: "ADMIN", isMyScreen: false, hasAccess: true},
		{screenId: "xyz789", owner: "laksjdl", isMyScreen: false, hasAccess: false},
		{screenId: "21nlks", owner: "lolololol", isMyScreen: true, hasAccess: true},
		{screenId: "asffav", owner: "kaslkdlakmsndlkansolknlknxzlknclzmxnclmznxlssnxnxnxmcxinhjklnkljakmlsam", isMyScreen: false, hasAccess: true},
		{screenId: "0sqnm4", owner: "lolololol", isMyScreen: true, hasAccess: true},
		{screenId: "kqwnxx", owner: "testasdasd", isMyScreen: false, hasAccess: true},
		{screenId: "abc123", owner: "ADMIN", isMyScreen: false, hasAccess: true},
		{screenId: "xyz789", owner: "laksjdl", isMyScreen: false, hasAccess: false},
		{screenId: "21nlks", owner: "lolololol", isMyScreen: true, hasAccess: true},
		{screenId: "asffav", owner: "kaslkdlakmsndlkansolknlknxzlknclzmxnclmznxlssnxnxnxmcxinhjklnkljakmlsam", isMyScreen: false, hasAccess: true},
		{screenId: "0sqnm4", owner: "lolololol", isMyScreen: true, hasAccess: true},
		{screenId: "kqwnxx", owner: "testasdasd", isMyScreen: false, hasAccess: true},
		{screenId: "sj3xpz", owner: "opkokok", isMyScreen: false, hasAccess: false}
	];
	
	
	var loginContent = new LoginContent();
	var screenListContent = new ScreenListContent();
	var screenContent = new ScreenContent();
	
	
	 
	loginContent.render($('#content'));

	loginContent.login(function() {
		
		var loginPost = post('api/web/login', {login: 'test', password: 'test123'});
		
		loginPost.done(function(response) {
			
			var loginPost = post('api/web/login', {login: 'test', password: 'test123'});
			x.render($('#content'), [], true);
			
			setTimeout(function() {
				x.render($('#content'), data);
			}, 3000);
		});
			
		loginPost.fail(function(xhr, status, error) {
			try{
				notify(JSON.parse(xhr.responseText).message, 6000, 'error');
			} catch(e){
				notify(status, 6000, 'error');
			}
		});
		
	});
	
	loginContent.signUp(function(s) {
		notify("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut eu lacus ante. Aliquam magna ligula, vulputate vitae congue at, laoreet sit amet nisl. Aliquam ut vulputate nunc. Aenean vitae leo varius, hendrerit metus non, laoreet ante. Aliquam sit amet vehicula leo. Ut odio nulla, ultricies sit amet ultricies non, interdum sed lacus. Donec vulputate vestibulum arcu consequat dictum. Ut dictum sapien ac commodo condimentum. Maecenas facilisis fringilla magna, vitae pulvinar neque rutrum in. Duis et accumsan enim, nec aliquet arcu. Nam ac tincidunt mauris. ", 10000, 'warning');
	});
	
	x.open(function(screenId) {
		notify("openning " + screenId, 6000, 'info');
	});
	
	
	
	
	
});

//function Test() {
//	this.a = 1;
//	var b = 2;
//	
//	function test2(){
//		return 10;
//	}
//	
//	this.test1 = function() {
//		return 111;
//	} 
//	this.test3 = function() {
//		return test2() + 10;
//	} 
//};

function ScreenListContent(){
	
	var self = this;
	
	this.open = function(callback){
		self.openCallback = callback;
	};
	
	this.openCallback = null;
	
	this.render = function(target, data, waiting) {
		
		var waitingAnimation = '<div class="placeholder waiting"><span>.</span><span>.</span><span>.</span></div>';
		var empty = '<div class="placeholder empty">no screens active</div>';
		
		var myScreensHeader = 
			'<h2>my screens</h2> ' +
			'<button id="button-manage-access">manage access</button>' +
			'<div class="row-header">' +
			'<div class="cell cell-1 screen-id-header">screen id</div> ' +
			'<div class="cell cell-2 owner-header">owner</div> ' +
			'<div class="cell cell-3"></div>' +
			'</div>' +
			'';
		
		var otherScreensHeader = 
			'<h2>other screens</h2>' +
			'<div class="row-header">' +
			'<div class="cell cell-1 screen-id-header">screen id</div> ' +
			'<div class="cell cell-2 owner-header">owner</div> ' +
			'<div class="cell cell-3"></div>' +
			'</div>' +
			'';
		
		var myScreenRows = "";
		var otherScreenRows = "";
		
		var myScreenRowsCount = 0;
		var otherScreenRowsCount = 0;
		
		for (var r in data) {
			var row = 
				'<div class="row">' +
				'<div class="cell cell-1 screen-id">' + data[r].screenId + '</div> ' +
				'<div class="cell cell-2 owner">' + data[r].owner + '</div> ' +
				'';
			
			if (data[r].hasAccess) {
				row = row +
				'<div class="cell cell-3"><button class="button-open" data-screen-id="' + data[r].screenId + '">open</div>' +
				'';
			} else {
				row = row +
				'<div class="cell cell-3 no-access">no access</div>' +
				'';
			}
			
			row = row +
			'</div>' +
			'';
			
			if (data[r].isMyScreen) {
				myScreenRows = myScreenRows + row;
				myScreenRowsCount++;
			} else {
				otherScreenRows = otherScreenRows + row;
				otherScreenRowsCount++;
			}
		}
		
		if(myScreenRowsCount == 0){
			myScreenRows = empty;
		}
		
		if(otherScreenRowsCount == 0){
			otherScreenRows = empty;
		}
		
		if (waiting) {
			myScreenRows = waitingAnimation;
			otherScreenRows = waitingAnimation;
		}
		
		var html = '<div id="container-screen-list">' + 
			myScreensHeader +
			myScreenRows +
			otherScreensHeader +
			otherScreenRows +
			'</div>';
		
		target.html(html);
		
		$('.button-open').each( function (i, obj) {
		    $(this).click(function() {
		    	if (typeof self.openCallback === 'function') {
		    		self.openCallback($(this).data('screen-id'));
		    	}
		    });
		});
		
	};
	
	this.open = function(callback){
		self.openCallback = callback;
	};
	
	this.openCallback = null;

	this.openManageAccess = function(callback){
		self.openManageAccessCallback = callback;
	};
	
	this.openManageAccessCallback = null;
		
	$('#button-manage-access').click(function() {
		if (typeof self.openManageAccessCallback === 'function') {
			self.openManageAccessCallback();
		}
	});
}




function WaitContent(){

	var self = this;

	this.html = '<div id="container-wait" class="waiting"><span>.</span><span>.</span><span>.</span></div>';

	this.render = function(target) {
		target.html(self.html);
	}
}

function LoginContent(){
	
	var self = this;
	
	this.html = 
		'<div id="container-login">' +
		'<label>login</label>' +
		'<div><input type="text" id="login" /></div>' +
		'<label>password</label>' +
		'<div><input type="password" id="password" /></div>' +
		'<div>' +
		'<button id="button-signup">sign up</button>' +
		'<button id="button-login">login</button>' +
		'</div>' +
		'</div>';
	
	this.login = function(callback){
		self.loginCallback = callback;
	};
	
	this.loginCallback = null;
	
	this.signUp = function(callback){
		self.signUpCallback = callback;
	};
	
	this.signUpCallback = null;
	
	this.render = function(target) {
		
		target.html(self.html);
		
		$('#button-login').click(function() {
			if (typeof self.loginCallback === 'function') {
				self.loginCallback();
			}
		});
		
		$('#button-signup').click(function() {
			if (typeof self.signUpCallback === 'function') {
				self.signUpCallback();
			}
		});
		
	};
	
	this.getLogin = function() {
		return $('#login').length ? $('#login').val() : "";
	};

	this.setLogin = function(login) {
		if ($('#login').length) {
			$('#login').val(login);
		}
	};
	
	this.getPassword = function() {
		return $('#password').length ? $('#password').val() : "";
	};
}


function ScreenContent(){
	
	var self = this;
	
	this.screen = null;
	this.imgUrl = null;
	this.screenId = null;
	
	this.render = function(target) {
		
		var html = '<div id="container-screen">';
		
		html = html + '<button id="button-back">&lt;</button>';
		html = html + '<div id="screen"></div>';
		html = html + '';
		
		html = html + '</div>';
		
		target.html(html);
		
	};
	
	var run = false;
	
	var newVersions = [];
	var curVersions = [];
	var images = [];
	var order = [];
	var pointer = 0;
	
	function createRandomOrder() {		
		for (var i = 0; i < self.screen.getNumOfSegments(); i++) {
			order[i] = i;
		}
		shuffle(order);
	}
	
	this.start = function(){
		if(!$('#screen').length || self.screen == null || self.imgUrl == null || screenId == null) {
			return;
		}
		
		$('#screen').width(self.screen.screenWidth);
		$('#screen').height(self.screen.screenHeight);
		
		for (var i = 0; i < self.screen.getNumOfSegments(); i++) {

			var row = Math.floor( i / self.screen.getNumOfCols());
			var col = i % self.screen.getNumOfCols();
			
			var x = col * self.screen.segmentWidth;
			var y = row * self.screen.segmentHeight;
			
			img = $('<img />');
	        img.css('position', 'absolute');
	        img.css('left', x + 'px');
	        img.css('top', y +'px');

	        $('#screen').append(img);
	        
	        images[i] = img;
		}
		
		run = true;
		recursiveGetVersions();
		recursiveDrawImage();
	}
	
	this.stop = function(){
		run = false;
	}
	
	const GET_VERSIONS_INTERVAL = 200;
	const DRAW_IMAGE_INTERVAL = 10;
	
	function recursiveGetVersions() {
		if (run) {
			newVersions = getVersionsCollback();
			
			setTimeout(function() {
				recursiveGetVersions();
			}, GET_VERSIONS_INTERVAL);
		}
	}

	function recursiveDrawImage() {
		if (run) {
						
			while (!(newVersions[order[pointer]] > curVersions[order[pointer]])) {
				pointer++;
				if (pointer > self.screen.getNumOfSegments() - 1){
					break;
				}
			}
			
			if (pointer > self.screen.getNumOfSegments() - 1){
				pointer = 0;
			}
			
			var index = order[pointer];
			
			if (newVersions[index] > curVersions[index]) {

				var url = self.imgUrl + '/' + self.screenId + '/' + index + '/' + new Date().getTime();
			
				image[index].attr('src', url); 
			
				curVersions[index] = newVersions[index];
				pointer++;
			}
			
			setTimeout(function() {
				recursiveDrawImage();
			}, DRAW_IMAGE_INTERVAL);
		}
	}
	
	this.getVersions = function(callback){
		getVersionsCallback = callback;
	};
	
	var getVersionsCallback = null;
	
	/**
	 * Shuffles array in place. ES6 version
	 * @param {Array} a items An array containing the items.
	 */
	function shuffle(a) {
		for (let i = a.length - 1; i > 0; i--) {
			const j = Math.floor(Math.random() * (i + 1));
			[a[i], a[j]] = [a[j], a[i]];
		}
	}
}

function post(url, data){
	var settings = {
			async: true,
//			url: 'api/web/login',
			url: url,
			method: 'POST',
			contentType: 'application/json; charset=utf-8',
			cache: false,
			dataType: 'json',
//			data: JSON.stringify({login: c.getLogin(), password: c.getPassword()})
			data: JSON.stringify(data)
	}
	
	return $.ajax(settings);
	
//	loginPost.done(function(response) {
//		notify('login successful', 1000, 'success');
//		x.render($('#content'), [], true);
//		
//		setTimeout(function() {
//			x.render($('#content'), data);
//		}, 3000);
//	});
//		
//	loginPost.fail(function(xhr, status, error) {
//		try{
//			notify(JSON.parse(xhr.responseText).message, 6000, 'error');
//		} catch(e){
//			notify(status, 6000, 'error');
//		}
//	});
//	
//	x.open(function(screenId) {
//		notify("openning " + screenId, 6000, 'info');
//	});

}
