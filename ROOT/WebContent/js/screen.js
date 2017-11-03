
function Screen(screenWidth, screenHeight, segmentWidth, segmentHeight){
	
	this.screenWidth = screenWidth;
	this.screenHeight = screenHeight;
	this.segmentWidth = segmentWidth;
	this.segmentHeight = segmentHeight;
	
	this.toString = function(){
		return '{screenWidth: ' + this.screenWidth + ', screenHeight: ' + this.screenHeight + ', segmentWidth: ' + this.segmentWidth + ', segmentHeight: ' +	this.segmentHeight + '}'; 
	}

	this.getSegmentRectangle = function(i) {
		
		var row = Math.floor( i / this.getNumOfCols());
		var col = i % this.getNumOfCols();
		
		var segmentWidth;
		var segmentHeight;
		
		if (col * this.segmentWidth + this.segmentWidth > this.screenWidth) {
			segmentWidth = this.screenWidth - col * this.segmentWidth;
		} else {
			segmentWidth = this.segmentWidth;
		}
		
		if (row * this.segmentHeight + this.segmentHeight > this.screenHeight) {
			segmentHeight = this.screenHeight - row * this.segmentHeight;
		} else {
			segmentHeight = this.segmentHeight;
		}
		
		var xy = { 
			x: col * screen.segmentWidth,
			y: row * screen.segmentHeight
		}
		
		return new Rectangle(col * screen.segmentWidth, row * screen.segmentHeight, segmentWidth, segmentHeight);
	}

	this.getNumOfCols = function() {
		return  Math.ceil( this.screenWidth / this.segmentWidth);
	}
	
	this.getNumOfRows = function() {
		return  Math.ceil( this.screenHeight / this.segmentHeight);
	}
	
	this.getNumOfSegments = function() {
		return this.getNumOfRows() * this.getNumOfCols();
	}
}

function Rectangle(x, y, width, height){
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
}



