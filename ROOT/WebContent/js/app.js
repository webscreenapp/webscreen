$(function() {
	
	var loginContent = new LoginContent();
	var screenListContent = new ScreenListContent();
	var screenContent = new ScreenContent();
	var waitContent = new WaitContent();
	
	var currentContent = '';
	
	init();
	
	function init(){
		waitContent.render($('#content'));
		currentContent = 'wait';
		
		var loginInfoPost = post('api/web/login/info');
		loginInfoPost.fail(postFail);
		loginInfoPost.done(function(response) {
			if (response.isLoggedIn == true) {
				showScreenListContent();
			} else {
				showLoginContent();
			}
			
		});
	}
	
	function showLoginContent(){
		loginContent.render($('#content'));
		currentContent = 'login';
		
		loginContent.signUp(function(s) {
			notify('not implemented', 10000, 'error');
		});
		loginContent.login(function() {
			var loginPost = post('api/web/login', {login: loginContent.getLogin(), password: loginContent.getPassword()});
			loginPost.fail(postFail);
			loginPost.done(function(response) {
				showScreenListContent();
			});
		});
	}
	
	function showScreenListContent(){
		screenListContent.render($('#content'), [], true);
		currentContent = 'screenList';
		
		var screenListPost = post('api/web/screen/list');
		screenListPost.fail(postFail);
		screenListPost.done(function(response) {
			screenListContent.render($('#content'), response, false);
		});
		
		screenListContent.open(function(screenId) {
			showScreenContent(screenId);
		});
		
	}
	 
	function showScreenContent(screenId){

		var screenInfoPost = post('api/web/screen/info', {screenId: screenId});
		screenInfoPost.fail(postFail);
		screenInfoPost.done(function(response) {
			var screen = new Screen(response.screenWidth, response.screenHeight, response.segmentWidth, response.segmentHeight);
			screenContent.screen = screen;
			screenContent.screenId = screenId;
			screenContent.imgUrl = 'api/web/image';

			screenContent.render($('#content'));
			currentContent = 'screen';
			
			screenContent.getVersions(function() {
				var versionPost = post('api/web/image/version', {screenId: screenId});
				versionPost.fail(function(xhr, status, error) {
					postFail(xhr, status, error);
					screenContent.stop();
					showScreenListContent();
				});
				versionPost.done(function(response) {
					screenContent.setVersions(response);
				});
			});
			
			screenContent.start();
		});
		
	}

	
});

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
		if(!$('#screen').length || self.screen == null || self.imgUrl == null || self.screenId == null) {
			console.log('start failed');
			return;
		}
		
		$('#screen').width(self.screen.screenWidth);
		$('#screen').height(self.screen.screenHeight);
		
		for (var i = 0; i < self.screen.getNumOfSegments(); i++) {
			
			curVersions[i] = 0;

			var row = Math.floor( i / self.screen.getNumOfCols());
			var col = i % self.screen.getNumOfCols();
			console.log('img x:' + col + ', y:' + row);
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
	
	this.setVersions = function(versions) {
		newVersions = versions;
		createRandomOrder();
	}
	
	const GET_VERSIONS_INTERVAL = 500;
	const DRAW_IMAGE_INTERVAL = 1;
	
	function recursiveGetVersions() {
		if (run) {
			getVersionsCollback();
			
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
			
				images[index].attr('src', url); 
			
				curVersions[index] = newVersions[index];
				pointer++;
			}
			
			setTimeout(function() {
				recursiveDrawImage();
			}, DRAW_IMAGE_INTERVAL);
		}
	}
	
	this.getVersions = function(callback){
		getVersionsCollback = callback;
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

function post(url, data){
	var settings = {
			async: true,
			url: url,
			method: 'POST',
			cache: false,
	}

	if (data) {
		settings.contentType = 'application/json; charset=utf-8';
		settings.dataType = 'json';
		settings.data = JSON.stringify(data);
	}
	
	return $.ajax(settings);
}

var postFail = function(xhr, status, error) {
	try{
		notify(JSON.parse(xhr.responseText).message, 6000, 'error');
	} catch(e){
		notify('status: ' + xhr.status + ' - ' + xhr.statusText, 20000, 'error');
	}
}
