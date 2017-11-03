package cbn.webscreen.util;

import java.awt.Dimension;
import java.awt.Rectangle;

/**
 * stores screen parameters and calculates coordinates
 */
public class ScreenParameters {

    private Dimension screenSize;
    private Dimension segmentSize;

    
    /**
     *constructor - sets screen params
     * @param screenSize screen size
     * @param segmentSize segment size
     */
    public ScreenParameters(Dimension screenSize, Dimension segmentSize) {
        this.screenSize = screenSize;
        this.segmentSize = segmentSize;
    }

    /**
     * constructor
     */
    public ScreenParameters() {
    }

    /**
     * calculates segment coordinates
     * @param i position
     * @return coordinates
     */
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

    /**
     * calculates number of segment rows
     * @return number of rows
     */
    public int getNumOfRows() {
        return (int) Math.ceil((double) screenSize.height / (double) segmentSize.height);
    }

    /**
     * calculates number of segment colmuns
     * @return number of columns
     */
    public int getNumOfCols() {
        return (int) Math.ceil((double) screenSize.width / (double) segmentSize.width);
    }

    /**
     * calculates number of segments
     * @return number of segmnents
     */
    public int getNumOfSegments() {
        return getNumOfRows() * getNumOfCols();
    }
    
    /**
     * returns screen width
     * @return screen width
     */
    public int getWidth(){
        return getScreenSize().width;
    }
    
    /**
     * returns screen height
     * @return screen height
     */
    public int getHeight(){
        return getScreenSize().height;
    }

    /**
     * rerturn screen size
     * @return screen size
     */
    public Dimension getScreenSize() {
        return screenSize;
    }

    /**
     * sets screen size
     * @param screenSize screen size
     */
    public void setScreenSize(Dimension screenSize) {
        this.screenSize = screenSize;
    }

    /**
     * returns segment size
     * @return segment size
     */
    public Dimension getSegmentSize() {
        return segmentSize;
    }

    /**
     * sets segment size
     * @param segmentSize segment size
     */
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
