import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.awt.image.BufferedImage;



public class Pipe extends JComponent{
    int x;
    int topY;
    int bottomY;
    int topHeight;
    int width;
    int bottomHeight;
    BufferedImage topPipe;
    BufferedImage bottomPipe;
    final int vel = 5; 
    final int WIDTH = 10;
    Random rand = new Random();
    public boolean passed;

    public Pipe(int x){
        this.x = x;
        this.topHeight = 600;
        this.bottomHeight = 600;
        this.passed = false;
        this.bottomPipe = utils.loadImage("sprites\\pipe.png");
        this.bottomPipe = utils.scaleImage(this.bottomPipe, 1, 1.75);
        this.topPipe = utils.rotateImage(this.bottomPipe, 180);
        this.width = this.topPipe.getWidth();
        this.setHeight();
    }

    public void paintComponent(Graphics g){
        //g.setColor(Color.GREEN);
        //g.fillRect(this.x, this.topY, this.WIDTH, this.topHeight);
        //g.fillRect(this.x, this.bottomY, this.WIDTH, this.bottomHeight);
        g.drawImage(this.topPipe, this.x, this.topY, this);
        g.drawImage(this.bottomPipe, this.x, this.bottomY, this);
    }

    public void move(){
        this.x -= vel; // do we access it with 'this' or no
    }

    private void setHeight(){
        int gap = 100; //70
        int height = rand.nextInt(350) + 50;
        this.topY = height - this.topHeight;
        this.bottomY = gap + height;
    }
}
