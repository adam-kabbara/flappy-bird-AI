import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;


public class utils {
    public static BufferedImage loadImage(String path){
        BufferedImage image;
        try{
            image = ImageIO.read(new File(path));
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
        return image;
    }

    public static BufferedImage scaleImage(BufferedImage image, double xScale, double yScale){
        AffineTransform tx = new AffineTransform();
        tx.scale(xScale, yScale);  
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        return op.filter(image, null);
    }

    public static BufferedImage rotateImage(BufferedImage imageToRotate, int angle) {
        int widthOfImage = imageToRotate.getWidth();
        int heightOfImage = imageToRotate.getHeight();
        BufferedImage newImageFromBuffer = new BufferedImage(widthOfImage, heightOfImage, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImageFromBuffer.createGraphics();                                 // ARGB for transparent bg
        g.rotate(Math.toRadians(angle), widthOfImage / 2, heightOfImage / 2);
        g.drawImage(imageToRotate, null, 0, 0);
        return newImageFromBuffer;
    }

    public static void pause(int ms) {
        try {
            Thread.sleep(ms);
        }
        catch (InterruptedException ex) {
            System.out.println("Error occurred!");
        }
    }


}
