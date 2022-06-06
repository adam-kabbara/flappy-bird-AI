import javax.swing.JComponent;
import java.awt.*;
import java.awt.image.BufferedImage;



public class Base extends JComponent{
    int x1;
    int x2;
    int y;
    int vel;
    int width;
    BufferedImage image;

    public Base(int y, int vel){
        this.y = y;
        this.vel = 5;
        x1 = 0;

        this.image = utils.loadImage("assets\\base.png");
        this.image = utils.scaleImage(this.image, 1.4, 1);

        this.width = this.image.getWidth();
        this.x2 = this.width-1;

    }

    public void paintComponent(Graphics g){
        g.drawImage(this.image, this.x1, this.y, this);
        g.drawImage(this.image, this.x2, this.y, this);
    }

    public void move(){
        this.x1 -= this.vel;
        this.x2 -= this.vel;
        if (this.x1 + this.width < 0)
            this.x1 = this.x2 + this.width-1;
        if (this.x2 + this.width < 0)
            this.x2 = this.x1 + this.width-1;
    }
}
