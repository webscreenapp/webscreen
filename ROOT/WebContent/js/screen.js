
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



