$(function() {
	
	var loginContent = new LoginContent();
	var signupContent = new SignupContent();
	var screenListContent = new ScreenListContent();
	var accessListContent = new AccessListContent();
	var screenContent = new ScreenContent();
	var waitContent = new WaitContent();
	var updater = new Updater();
	
	var currentContent = '';
	
	var lastUpdate = 0;
	var updates = [];
	var updateScreenId = null;
	
	init();
	
	function init(){
		waitContent.render($('#content'));
		currentContent = 'wait';
		
		var loginInfoPost = post('api/web/login/info');
		loginInfoPost.fail(postFail);
		loginInfoPost.done(function(response) {
			if (response.isLoggedIn == true) {
				showScreenListContent();
				startUpdater();
			} else {
				showLoginContent();
			}
			
		});
	}
	
	function showLoginContent(){
		loginContent.render($('#content'));
		currentContent = 'login';
		
		loginContent.signUp(function(s) {
			showSignupContent()
		});
		loginContent.login(function() {
			var loginPost = post('api/web/login', {login: loginContent.getLogin(), password: loginContent.getPassword()});
			loginPost.fail(postFail);
			loginPost.done(function(response) {
				showScreenListContent();
				startUpdater();
			});
		});
	}

	function showSignupContent(){
		signupContent.render($('#content'));
		currentContent = 'signup';
		
		signupContent.signUp(function() {
			if (signupContent.passwordMatch()){
				var signupPost = post('api/web/signup', {login: loginContent.getLogin(), password: loginContent.getPassword()});
				signupPost.fail(postFail);
				signupPost.done(function(response) {
					showLoginContent();
				});
			} else {
				notify('passwords do not match', 6000, 'error');
			}
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

		screenListContent.openManageAccess(function() {
			showAccessListContent();
		});
		
	}
	
	function showAccessListContent(){
		currentContent = 'accessList';
		
		var accessListPost = post('api/web/access/list');
		accessListPost.fail(postFail);
		accessListPost.done(function(response) {
			accessListContent.render($('#content'), response, false);
		});
		accessListContent.save(function(data) {
			var saveAccessPost = post('api/web/access/set', data);
			saveAccessPost.done(function(response) {
				showScreenListContent()
			});
		});
		accessListContent.back(function() {
			showScreenListContent()
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
			updateScreenId = screenId;
			
			screenContent.back(function() {
				screenContent.stop();
				showScreenListContent();
			});
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

	function startUpdater(){
		
		updater.update(function() {
			
			var updatePost = post('api/web/update', {lastUpdate: lastUpdate, screenId: updateScreenId, updates: updates});
			updatePost.fail(postFail);
			updatePost.done(function(response) {
				updates = [];
				lastUpdate = response.lastUpdate;
				
				for (var i = 0; i < response.updates.length; i++) {
					var update = response.updates[i];
					
					if (update == 'screen.new' && currentContent == 'screenList') {
						showScreenListContent();
					}
					
					if (update == 'screen.remove' && currentContent == 'screenList') {
						showScreenListContent();
					}
					
					if (update == 'screen.stop' && currentContent == 'screen') {
						screenContent.stop();
						showScreenListContent();
					}

					if (update == 'screen.update' && currentContent == 'screen') {
						screenContent.stop();
						showScreenContent(screenContent.screenId);
					}
				}
				
			});
			
		});
		
		updater.start();
	}
	
});


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

function SignupContent(){
	
	var self = this;
	
	this.html = 
		'<div id="container-signup">' +
		'<label>login</label>' +
		'<div><input type="text" id="login" /></div>' +
		'<label>password</label>' +
		'<div><input type="password" id="password" /></div>' +
		'<label>password confirm</label>' +
		'<div><input type="password" id="password-confirm" /></div>' +
		'<div>' +
		'<button id="button-signup">sign up</button>' +
		'</div>' +
		'</div>';
	
	
	this.signUp = function(callback){
		self.signUpCallback = callback;
	};
	
	this.signUpCallback = null;
	
	this.render = function(target) {
		
		target.html(self.html);
		
		$('#button-signup').click(function() {
			if (typeof self.signUpCallback === 'function') {
				self.signUpCallback();
			}
		});
		
	};
	
	this.passwordMatch = function(){
		return ($('#password').length && $('#password-confirm').length && $('#password').val() == $('#password-confirm').val());
	}
	
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
		
		$('#button-manage-access').click(function() {
			if (typeof self.openManageAccessCallback === 'function') {
				self.openManageAccessCallback();
			}
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
		
}

function AccessListContent(){

	var self = this;
	
	var unsaved = false;
	
	this.data = null;
	this.target = null;
	
	this.render = function(target, data, waiting) {
		
		self.data = data;
		self.target = target;
		
		var waitingAnimation = '<div class="placeholder waiting"><span>.</span><span>.</span><span>.</span></div>';
		
		var header = 
			'<h2>manage access</h2> ' +
			'<div class="row-header">' +
			'<div class="cell cell-1 login-header">login</div> ' +
			'<div class="cell cell-2 access-header"></div> ' +
			'</div>' +
			'';
		
		var rows = "";
		
		var rowCount = 0;
		
		for (var r in data) {
			var row = 
				'<div class="row">' +
				
				'<div class="cell cell-1 login">' + data[r].login + '</div> ' +
				'';
			
			if (data[r].hasAccess) {
				row = row +
				'<div class="cell cell-2"><button class="button-deny" data-deny-id="' + data[r].id + '">deny</button></div>' +
				'';
			} else {
				row = row +
				'<div class="cell cell-2"><button class="button-allow" data-allow-id="' + data[r].id + '">allow</button></div>' +
				'';
			}
			
			row = row +
			'</div>' +
			'';
			
			rows = rows + row;
		}
		
		var btnBack = '<button id="button-back">back</button>';
		var btnSave = '';
		if (unsaved){
			btnSave = btnSave + '<button id="button-save">save</button>';
		}
		
		var html = '<div id="container-access-list">' + 
		header +
		rows +
		btnBack +
		btnSave +
		'</div>';
		
		target.html(html);
		
		$('.button-allow').each( function (i, obj) {
			$(this).click(function() {
				setAccess($(this).data('allow-id'), true);
			});
		});

		$('.button-deny').each( function (i, obj) {
			$(this).click(function() {
				setAccess($(this).data('deny-id'), false);
			});
		});
		
		$('#button-save').click(function() {
			unsaved = false;
			self.saveCallback(data);
		});
		
		$('#button-back').click(function() {
			unsaved = false;
			self.backCallback();
		});
		
	};
	
	function setAccess(id, access){
		for (var i in self.data) {
			if (self.data[i].id == id) {
				self.data[i].hasAccess = access;
				unsaved = true;
				self.render(self.target, self.data);
				break;
			}
		}
	}
	
	this.save = function(callback){
		self.saveCallback = callback;
	};
	
	this.saveCallback = null;

	this.back = function(callback){
		self.backCallback = callback;
	};
	
	this.backCallback = null;
	
}

function ScreenContent(){
	
	var self = this;
	
	this.screen = null;
	this.imgUrl = null;
	this.screenId = null;
	
	this.render = function(target) {
		
		var html = '';

		html = html + '<button id="button-back-arrow">&lt;</button>';
		html = html + '<div id="container-screen">';
		html = html + '<div id="screen"></div>';
		html = html + '';
		
		html = html + '</div>';
		
		target.html(html);
		
		$('#button-back-arrow').click(function() {
			if (typeof self.backCallback === 'function') {
				self.backCallback();
			}
		});
		
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
		if (!run) {
			startContinue();
		} else {
			self.stop();
			//delayed start
			setTimeout(function() {
				startContinue();
			}, START_DELAY);
		}
	}

	function startContinue(){

		if(!$('#screen').length || self.screen == null || self.imgUrl == null || self.screenId == null) {
			console.log('start failed');
			return;
		}
		
		newVersions = [];
		curVersions = [];
		
		for (var i = 0; i < images.length; i++) {
			images[i].remove();
		}
		
		images = [];
		
		
		$('#screen').css('width', self.screen.screenWidth + 'px');
		$('#screen').css('height', self.screen.screenHeight + 'px');
		
		for (var i = 0; i < self.screen.getNumOfSegments(); i++) {
			
			curVersions[i] = -1;

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
	
	this.setVersions = function(versions) {
		newVersions = versions;
		createRandomOrder();
	}
	
	const GET_VERSIONS_INTERVAL = 100;
	const DRAW_IMAGE_INTERVAL = 2;
	const START_DELAY = 200;
	
	function recursiveGetVersions() {
		if (run) {
			getVersionsCallback();
			
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
		getVersionsCallback = callback;
	};
	
	var getVersionsCallback = null;
	
	this.back = function(callback){
		self.backCallback = callback;
	};
	
	this.backCallback = null;
	
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

function Updater(){
	
	var self = this;
	
	var run = false;
	
	this.start = function(){
		
		run = true;
		recursiveUpdate();
	}
	
	this.stop = function(){
		run = false;
	}
	
	const UPDATE_INTERVAL = 500;
	
	function recursiveUpdate() {
		if (run) {
			
			updateCallback();
			
			setTimeout(function() {
				recursiveUpdate();
			}, UPDATE_INTERVAL);
		}
	}
	
	var updateCallback = null;
	
	this.update = function(callback) {
		updateCallback = callback;
	};
}

function Screen(screenWidth, screenHeight, segmentWidth, segmentHeight){
	
	var self = this;
	
	this.screenWidth = screenWidth;
	this.screenHeight = screenHeight;
	this.segmentWidth = segmentWidth;
	this.segmentHeight = segmentHeight;
	
	this.toString = function(){
		return '{screenWidth: ' + self.screenWidth + ', screenHeight: ' + self.screenHeight + ', segmentWidth: ' + self.segmentWidth + ', segmentHeight: ' +	self.segmentHeight + '}'; 
	}

	this.getSegmentRectangle = function(i) {
		
		var row = Math.floor( i / self.getNumOfCols());
		var col = i % self.getNumOfCols();
		
		var segmentWidth;
		var segmentHeight;
		
		if (col * self.segmentWidth + self.segmentWidth > self.screenWidth) {
			segmentWidth = self.screenWidth - col * self.segmentWidth;
		} else {
			segmentWidth = self.segmentWidth;
		}
		
		if (row * self.segmentHeight + self.segmentHeight > self.screenHeight) {
			segmentHeight = self.screenHeight - row * self.segmentHeight;
		} else {
			segmentHeight = self.segmentHeight;
		}
		
		return new Rectangle(col * self.segmentWidth, row * self.segmentHeight, segmentWidth, segmentHeight);
	}

	this.getNumOfCols = function() {
		return  Math.ceil( self.screenWidth / self.segmentWidth);
	}
	
	this.getNumOfRows = function() {
		return  Math.ceil( self.screenHeight / self.segmentHeight);
	}
	
	this.getNumOfSegments = function() {
		return self.getNumOfRows() * self.getNumOfCols();
	}
}

function Rectangle(x, y, width, height){
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
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
