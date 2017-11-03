package cbn.webscreen.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * encodes/decodes image (png)
 */
public class PngImageCodec  {

    /**
     * encodes image to png
     * @param image
     * @return png bytes
     */
    public static byte[] encodeImage(BufferedImage image) {
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png" , baos);
        } catch (IOException ex) {
            Logger.getLogger(PngImageCodec.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return baos.toByteArray();
    }

    /**
     * decodes image from png
     * @param png bytes
     * @return image
     */
    public static BufferedImage decodeImage(byte[] imageData) {
        BufferedImage image = null;
        
        try {
            image = ImageIO.read(new ByteArrayInputStream(imageData));
        } catch (IOException ex) {
            Logger.getLogger(PngImageCodec.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return image;
    }
    
    
    
}
