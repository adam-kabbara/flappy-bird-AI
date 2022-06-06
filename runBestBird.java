import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.awt.image.BufferedImage;

import javax.swing.*;

class runBestBird extends JComponent{
    // Instance variables that define the current characteristics
    // of the animated objecs.
    final int WIDTH = 375;
    final int HEIGHT = 600;
    final int PAUSE = 40;
    static JFrame frame = new JFrame();

    Bird bestBird;
    Base base = new Base(550, 5); // gives error if it was somewhere else
    final String bestBirdFileName = "bestBird.ser";
    BufferedImage bg;
    private ArrayList<Pipe> pipes = new ArrayList<Pipe>();
    private int score;

    public runBestBird(){
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT)); // we set the size to gameWindow 
        this.bg = utils.loadImage("assets\\bg.png");
        this.bg = utils.scaleImage(this.bg, 1.3, 1.25);

        //load best bird
        Bird bb = readBird(this.bestBirdFileName);
        if (bb != null){
            this.bestBird = Bird.copy(bb);
            System.out.println("Best Bird Found");
            System.out.println(bb.score);
        }
        else{
            System.out.println("Best Bird NOT Found");
        }
    }

    public static void main(String[] args) throws Exception {
        runBestBird gameWindow = new runBestBird();
        frame.add(gameWindow);         
        frame.pack(); // set frame size to content pane

        frame.setTitle("Flappy Bird");
        //frame.setResizable(false); todo
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Launch your animation!
        gameWindow.start();
    }
    
    // This special method is automatically called when the scene needs to be drawn.
    public void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.drawImage(this.bg, 0, 0, this);
        this.bestBird.paintComponent(g);

        for (int i=0; i<this.pipes.size(); i++){
            this.pipes.get(i).paintComponent(g);
        }
        
        this.base.paintComponent(g);
        drawScore(g);
    }

    private void drawScore(Graphics g){
        g.setColor(Color.ORANGE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("score: "+score, 0, 15);
    }

    public void start() throws Exception {
        this.pipes.add(new Pipe(450));

        while (true) {
            this.bestBird.fall();
            this.bestBird.think(this.pipes);
            this.base.move();

            for (int i=0; i<this.pipes.size(); i++){
                Pipe p = this.pipes.get(i);

                p.move();
                if (p.x < this.bestBird.x && ! p.passed){
                    this.pipes.add(new Pipe(450));
                    p.passed = true;
                    score++;
                }
                else if(p.x + p.width < 0){
                    this.pipes.remove(p);
                }
            }
            
            if (checkCollision(this.bestBird)){
                repaint();
                showLostPopup();
                break;
            }

            repaint();
            utils.pause(PAUSE);
        }
    }

    private void showLostPopup() {
        System.out.println("Best bird lost");
        JOptionPane.showMessageDialog(frame, "Best bird lost with a score of "+this.bestBird.pipeScore);
        frame.dispose();
    }

    private boolean checkCollision(Bird bird){
        // returns the boolean if bestBird has collided for runBestBird method
        Pipe p;
        if (bird.y + bird.rotatedImage.getHeight() > this.base.y){ // ground collision
            return true;
        }
        else if(bird.y < 0){ //sky collision
            return true;
        }
        else{
            for (int i=0; i<this.pipes.size(); i++){
                p = this.pipes.get(i);
                if (bird.x + bird.rotatedImage.getWidth() > p.x && p.x + p.width > bird.x){
                    if (bird.y + bird.rotatedImage.getHeight() > p.bottomY){
                        return true; //bottom pip collision
                    }
                    else if(p.topY + p.topHeight -40> bird.y){
                        return true; //top pip collision
                    }
                }
            }
            return false;
        }
    }
    public Bird readBird(String fileName){
        try {
            FileInputStream fileIn = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            SerializedBird sb = (SerializedBird) in.readObject();
            Bird b = SerializedBird.Deserialize(sb, WIDTH/2, HEIGHT/2, 15);
            in.close();
            fileIn.close();
            return b;
         } catch (IOException i) {
            i.printStackTrace();
            return null;
         } catch (ClassNotFoundException c) {
            System.out.println("Bird class not found");
            c.printStackTrace();
            return null;
         }
    }
}