package cbn.webscreen.app.screen;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class PngImageCodec  {

    public static byte[] encodeImage(BufferedImage image) {
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png" , baos);
        } catch (IOException ex) {
            Logger.getLogger(PngImageCodec.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return baos.toByteArray();
    }

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
