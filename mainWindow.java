import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.awt.image.BufferedImage;

import javax.swing.*;
 
class mainWindow extends JComponent{
    private final int BIRD_COUNT = 100; //100
    final int WIDTH = 375;
    final int HEIGHT = 600;
    final int PAUSE = 10; //40
    static JFrame frame = new JFrame();
    static Random rand = new Random();


    ArrayList<Bird> birds = new ArrayList<Bird>();
    ArrayList<Bird> deadBirds = new ArrayList<Bird>(); // lmao to access their BRAINNSSS
    ArrayList<Bird> collidedBirds;
    Base base;
    BufferedImage bg;
    int generationCount=0;
    int generationBest=0;
    int allBest=0;
    private ArrayList<Pipe> pipes = new ArrayList<Pipe>();

    public mainWindow(){
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT)); // we set the size to gameWindow
        this.bg = utils.loadImage("sprites\\bg.png");
        this.bg = utils.scaleImage(this.bg, 1.3, 1.25);
    }

    public static void main(String[] args) throws Exception {
        mainWindow gameWindow = new mainWindow();
        frame.add(gameWindow);         
        frame.pack(); // set frame size to content pane

        frame.setTitle("Snake Game");
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

        for (Bird bird: this.birds){
            bird.paintComponent(g);
        }

        for (Pipe p : this.pipes){
            p.paintComponent(g);
        }
        
        this.base.paintComponent(g);
        drawInfo(g);
    }

    private void drawInfo(Graphics g){
        g.setColor(Color.ORANGE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Generation: "+this.generationCount, 0, 15);
        g.drawString("Generation Best: "+this.generationBest, 0, 30);
        g.drawString("All Best: "+this.allBest, 0, 45);
    }

    public void start() throws Exception {
        this.base = new Base(550, 5);
        for (int i=0; i<this.BIRD_COUNT; i++){
            this.birds.add(new Bird(WIDTH/2, HEIGHT/2, 15));
        }
        this.deadBirds = new ArrayList<>(this.birds);
        this.pipes.add(new Pipe(450));

        while (true) {
            this.base.move();

            for (Bird bird: this.birds){
                bird.fall();
                bird.think(pipes);
                bird.score++;
            }

            for (int i=0; i<this.pipes.size(); i++){
                Pipe p = this.pipes.get(i);

                p.move();
                for (Bird bird: this.birds){
                    if (p.x < bird.x && ! p.passed){
                        this.pipes.add(new Pipe(450));
                        p.passed = true;
                        for (Bird b: this.birds)
                            b.pipeScore++; 
                        if (generationBest < bird.pipeScore){
                            generationBest = bird.pipeScore;
                        }
                        if (allBest < bird.pipeScore){
                            allBest = bird.pipeScore;
                        }
                    }
                    else if(p.x + p.width < 0){
                        this.pipes.remove(p);
                    }
                }
            }
            
            this.collidedBirds = checkCollision();
            if (!checkCollision().isEmpty()){
                for (int i=0; i<collidedBirds.size(); i++){
                    this.birds.remove(this.collidedBirds.get(i));
                }
                if (this.birds.isEmpty()){
                    this.generationCount++;
                    // keep the best bird in next generation
                    this.deadBirds.sort((s1, s2) -> s1.score.compareTo(s2.score));
                    Bird bestBird = new Bird(WIDTH/2, HEIGHT/2, 15);
                    bestBird.brain = this.deadBirds.get(this.deadBirds.size()-1).brain;
                    this.birds.add(bestBird);
                    for (int i=0; i<this.BIRD_COUNT-1; i++){
                        Bird b1 = pickBirdMate();
                        Bird b2 = pickBirdMate();
                        Bird baby = b1.bread(b2, WIDTH/2, HEIGHT/2, 15);
                        this.birds.add(baby);
                        this.deadBirds.add(baby);
                        // bread best AI's
                    }
                    //this.deadBirds.clear();
                    //this.deadBirds = new ArrayList<>(this.birds);
                    this.pipes.clear();
                    this.generationBest = 0;
                    this.pipes.add(new Pipe(450));
                }
            }

            repaint();
            utils.pause(PAUSE);
        }
    }

    private Bird pickBirdMate(){
        int randomIndex;
        this.deadBirds.sort((s1, s2) -> s1.score.compareTo(s2.score));
        // top 20% of birds with greater score are consider "superior"
        int arrayIndexCut = this.deadBirds.size()*80/100;
        ArrayList<Bird> superiorBirds = new ArrayList<Bird>(this.deadBirds.subList(arrayIndexCut, this.deadBirds.size()));
        ArrayList<Bird> otherBirds = new ArrayList<Bird>(this.deadBirds.subList(0, arrayIndexCut));
        double chance = Math.random(); // to increase diversity

        if (chance < 0.8){ // 80% of the time get a superior bird
            randomIndex = rand.nextInt(superiorBirds.size());//todo cahnge back to 0.8
            return superiorBirds.get(randomIndex);
        }
        else{ // the rest of the time, get a normal/dumb bird
            randomIndex = rand.nextInt(otherBirds.size());
            return otherBirds.get(randomIndex);
        }
    }

    private ArrayList<Bird> checkCollision(){
        // returns the birds that have collided
        Pipe p;
        ArrayList<Bird> birdsCollided = new ArrayList<Bird>();

        for (Bird bird: this.birds){
            if (bird.y + bird.rotatedImage.getHeight() > this.base.y){ // ground collision
                birdsCollided.add(bird);
            }
            else if(bird.y < 0){ //sky collision
                birdsCollided.add(bird);
            }
            else{
                for (int i=0; i<this.pipes.size(); i++){
                    p = this.pipes.get(i);
                    if (bird.x + bird.rotatedImage.getWidth() > p.x && p.x + p.width > bird.x){
                        if (bird.y + bird.rotatedImage.getHeight() > p.bottomY){
                            birdsCollided.add(bird); //bottom pip collision
                        }
                        else if(p.topY + p.topHeight - 40> bird.y){
                            birdsCollided.add(bird); //top pip collision
                        }
                    }
                }
            }
        }
        return birdsCollided;
    }
}