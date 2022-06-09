import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowAdapter;
import javax.swing.*;
 
class mainWindow extends JComponent{
    private final int BIRD_COUNT = 100; //100
    final int WIDTH = 375;
    final int HEIGHT = 600;
    final int PAUSE = 0; //40
    static JFrame frame = new JFrame();
    static Random rand = new Random();
    final String bestBirdFileName = "bestBird.ser";


    ArrayList<Bird> birds = new ArrayList<Bird>();
    ArrayList<Bird> deadBirds = new ArrayList<Bird>(); // lmao to access their BRAINNSSS
    Bird bestBird; // place holder
    ArrayList<Bird> collidedBirds;
    Base base;
    BufferedImage bg;
    int generationCount=0;
    int generationBest=0;
    int allBest=0;
    private ArrayList<Pipe> pipes = new ArrayList<Pipe>();

    public mainWindow() throws IOException{
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT)); // we set the size to gameWindow
        this.bg = utils.loadImage("assets\\bg.png");
        this.bg = utils.scaleImage(this.bg, 1.3, 1.25);
        this.base = new Base(550, 5);

        //load best bird
        Bird bb = readBird(this.bestBirdFileName);
        if (bb != null){
            this.bestBird = Bird.copy(bb);
            this.bestBird.score = bb.score;
            this.bestBird.pipeScore = bb.pipeScore;
            System.out.println("Best Bird Found");
            System.out.println(this.bestBird.score);
        }
        else{
            this.bestBird = new Bird(WIDTH/2, HEIGHT/2, 15);
            System.out.println("Best Bird NOT Found");
        }
    }

    public static void main(String[] args) throws Exception {
        mainWindow gameWindow = new mainWindow();

        // ----------------- window listener stuff ----------------
        WindowListener myWindowListener = new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                gameWindow.birds.sort((s1, s2) -> s1.score.compareTo(s2.score));
                if (gameWindow.bestBird.score < gameWindow.birds.get(gameWindow.birds.size()-1).score){
                    gameWindow.saveBird(gameWindow.birds.get(gameWindow.birds.size()-1), gameWindow.bestBirdFileName);
                    System.out.println("New best bird has been save!!");
                }
                else{
                    System.out.println("Best bird has greater score than current bird");
                }        
            }
        };
        // ----------------- done window listener stuff -------------------

        frame.add(gameWindow);
        frame.addWindowListener(myWindowListener); 
        frame.pack(); // set frame size to content pane
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Flappy Bird AI");
        //frame.setResizable(false); todo
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Launch your animation!
        gameWindow.startLearning();
    }
    
    // This special method is automatically called when the scene needs to be drawn.
    public void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.drawImage(this.bg, 0, 0, this);

        for (int i=0; i<this.birds.size(); i++){
            this.birds.get(i).paintComponent(g);
        }

        for (int i=0; i<this.pipes.size(); i++){
            this.pipes.get(i).paintComponent(g);
        }
        
        this.base.paintComponent(g);
        drawInfo(g);
    }

    private void drawInfo(Graphics g){
        g.setColor(Color.ORANGE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Generation: "+this.generationCount, 0, 15);
        g.drawString("Population Size: "+this.birds.size(), 0, 30);
        g.drawString("Generation Best: "+this.generationBest, 0, 45);
        g.drawString("Era Best: "+this.allBest, 0, 60);
        g.drawString("All Best: "+this.bestBird.pipeScore, 0, 75);
    }

    public void startLearning() throws Exception {
        // populate birds & pipes 
        for (int i=0; i<this.BIRD_COUNT; i++){
            this.birds.add(new Bird(WIDTH/2, HEIGHT/2, 15));
        }
        this.birds.add(Bird.copy(this.bestBird));
        //when the best bird of a "era" is added
        //to the new "era", it will progress much
        //much faster than usual
        this.deadBirds = new ArrayList<>(this.birds);
        this.pipes.add(new Pipe(450));

        while (true) {
            this.base.move();

            for (int i=0; i<this.birds.size(); i++){
                this.birds.get(i).fall();
                this.birds.get(i).think(this.pipes);
                this.birds.get(i).score++;
            }

            for (int i=0; i<this.pipes.size(); i++){
                Pipe p = this.pipes.get(i);

                p.move();
                for (int j=0; j<this.birds.size(); j++){
                    Bird bird = this.birds.get(j);
                    if (p.x < bird.x && ! p.passed){
                        this.pipes.add(new Pipe(450));
                        p.passed = true;
                        for (int k=0; k<this.birds.size(); k++){
                            this.birds.get(k).pipeScore++; 
                            this.birds.get(k).score += 100; // increase fitness if passed a pipe
                        }
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
            
            this.collidedBirds = checkCollision(this.birds);
            if (!this.collidedBirds.isEmpty()){
                for (int i=0; i<collidedBirds.size(); i++){
                    this.birds.remove(this.collidedBirds.get(i));
                }
                if (this.birds.isEmpty()){
                    this.deadBirds.sort((s1, s2) -> s1.score.compareTo(s2.score)); //sort dead birds by score
                    this.generationCount++;
                    // keep the best bird in next generation
                    Bird genBestBird = Bird.copy(this.deadBirds.get(this.deadBirds.size()-1));
                    this.birds.add(genBestBird);

                    // keep best bird mutated in next gen 
                    Bird genBestBirdMutated = Bird.copy(this.deadBirds.get(this.deadBirds.size()-1));
                    genBestBirdMutated.brain.mutate(0.1, 0.1);
                    this.birds.add(genBestBirdMutated);

                    //save best bird
                    if (this.deadBirds.get(this.deadBirds.size()-1).score > this.bestBird.score){
                        this.bestBird = Bird.copy(genBestBird);
                        this.bestBird.score = this.deadBirds.get(this.deadBirds.size()-1).score;
                        this.bestBird.pipeScore = this.deadBirds.get(this.deadBirds.size()-1).pipeScore;
                        saveBird(this.bestBird, this.bestBirdFileName);
                        System.out.println("saved best bird");
                    }

                    normalizeFitness(this.deadBirds);
                    for (int i=0; i<this.BIRD_COUNT/2; i++){
                        Bird b1 = poolSelection(this.deadBirds); //pickBirdMate();
                        Bird b2 = poolSelection(this.deadBirds); //pickBirdMate();
                        Bird[] offspring = b1.bread(b2, WIDTH/2, HEIGHT/2, 15);
                        Bird baby1 = offspring[0];
                        Bird baby2 = offspring[1];
                        this.birds.add(baby1);
                        this.birds.add(baby2);
                    }
                    this.deadBirds.clear();
                    this.deadBirds = new ArrayList<>(this.birds);
                    this.pipes.clear();
                    this.generationBest = 0;
                    this.pipes.add(new Pipe(450));
                }
            }

            repaint();
            utils.pause(PAUSE);
        }
    }

    private ArrayList<Bird> checkCollision(ArrayList<Bird> birdsArray){
        // returns the birds that have collided for startLearning method
        Pipe p;
        ArrayList<Bird> birdsCollided = new ArrayList<Bird>();

        for (int j=0; j<birdsArray.size(); j++){
            Bird bird = birdsArray.get(j);
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


    public void saveBird(Bird bird, String fileName){
        SerializedBird sb = Bird.serialize(bird);
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(sb);
            out.close();
            fileOut.close();
            System.out.println("Serialized data is saved in "+fileName);
         } catch (IOException i) {
            i.printStackTrace();
         }
    }

    public Bird readBird(String fileName) throws IOException{
        try {
            FileInputStream fileIn = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            SerializedBird sb = (SerializedBird) in.readObject();
            Bird b = SerializedBird.Deserialize(sb, WIDTH/2, HEIGHT/2, 15);
            in.close();
            fileIn.close();
            return b;
         } catch (FileNotFoundException i) {
            System.out.println("File not found: "+fileName);
            i.printStackTrace();
            return null;
         } catch (ClassNotFoundException c) {
            System.out.println("Bird class not found");
            c.printStackTrace();
            return null;
         }
    }


    // ------------------ AI STUFFFF ----------------------------------

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

    public void normalizeFitness(ArrayList<Bird> birds) {

        // Make score exponentially better?
        for (int i = 0; i < birds.size(); i++) {
          birds.get(i).score = (int) Math.pow(birds.get(i).score, 2);
        }
      
        // Add up all the scores
        int sum = 0;
        for (int i = 0; i < birds.size(); i++) {
          sum += birds.get(i).score;
        }
        // Divide by the sum
        for (int i = 0; i < birds.size(); i++) {
          birds.get(i).fitness = (double) birds.get(i).score / (double) sum;
        }
    }

    public Bird poolSelection(ArrayList<Bird> birds) {
        // Start at 0
        int index = 0;
      
        // Pick a random number between 0 and 1
        double r = Math.random();
      
        // Keep subtracting probabilities until you get less than zero
        // Higher probabilities will be more likely to be fixed since they will
        // subtract a larger number towards zero
        try{
            while (r > 0) {
                r -= birds.get(index).fitness;
                // And move on to the next
                index += 1;
            }
        }
        catch (Exception IndexOutOfBoundsException){
            return birds.get(rand.nextInt(birds.size() - 1));
        }
      
        // Go back one
        index --;
      
        return birds.get(index);
    }
}