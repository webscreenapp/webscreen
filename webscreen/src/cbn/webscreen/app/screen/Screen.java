package cbn.webscreen.app.screen;

import java.awt.Dimension;
import java.awt.Rectangle;

public class Screen {

    private Dimension screenSize;
    private Dimension segmentSize;

    
    public Screen(Dimension screenSize, Dimension segmentSize) {
        this.screenSize = screenSize;
        this.segmentSize = segmentSize;
    }

    public Screen() {
    }

    public Rectangle getSegmentRectangle(int i) {

        int row = (int) Math.floor((double) i / (double) getNumOfCols());
        int col = i % getNumOfCols();

        int segmentWidth;
        int segmentHeight;

        if (col * segmentSize.width + segmentSize.width > screenSize.width) {
            segmentWidth = screenSize.width - col * segmentSize.width;
        } else {
            segmentWidth = segmentSize.width;
        }

        if (row * segmentSize.height + segmentSize.height > screenSize.height) {
            segmentHeight = screenSize.height - row * segmentSize.height;
        } else {
            segmentHeight = segmentSize.height;
        }

        return new Rectangle(col * segmentSize.width, row * segmentSize.height, segmentWidth, segmentHeight);
    }

    public int getNumOfRows() {
        return (int) Math.ceil((double) screenSize.height / (double) segmentSize.height);
    }

    public int getNumOfCols() {
        return (int) Math.ceil((double) screenSize.width / (double) segmentSize.width);
    }

    public int getNumOfSegments() {
        return getNumOfRows() * getNumOfCols();
    }
    
    public int getWidth(){
        return getScreenSize().width;
    }
    
    public int getHeight(){
        return getScreenSize().height;
    }

    public Dimension getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(Dimension screenSize) {
        this.screenSize = screenSize;
    }

    public Dimension getSegmentSize() {
        return segmentSize;
    }

    public void setSegmentSize(Dimension segmentSize) {
        this.segmentSize = segmentSize;
    }
    
    public int getSegmentIndexByPixel(int pixel){
    	
    	int x = pixel % screenSize.width;
    	int y = pixel / screenSize.width;
    	
    	int col = x / segmentSize.width;
     	int row = y / segmentSize.height;
     	
     	int index = row * getNumOfCols() + col;
    	
    	return index;
    }
    
    
}
