import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.*;

class Bird extends JComponent{
    int x;
    int y;
    int width;
    int rotation;
    Integer score;
    double fitness; // normilized score
    int pipeScore;
    int jumpPos;
    boolean isJumping;
    BufferedImage image;
    BufferedImage rotatedImage;
    double gravity;
    NeuralNetwork brain;
    final double gravityConst = 1.5;

    public Bird(int x, int y, int width){
        this.x = x;
        this.y = y;
        this.width = width;
        this.gravity = 0;
        this.rotation = 0;
        this.jumpPos = this.y;
        this.isJumping = false;
        this.image = utils.loadImage("assets\\yellowbird.png");
        this.rotatedImage = this.image;
        this.brain = new NeuralNetwork(5, 8, 2);
        this.score = 0;
        this.fitness = 0.0;
        this.pipeScore = 0;

    }

    public void paintComponent(Graphics g) {
        if (this.isJumping){
            this.isJumping = false;
            if (this.rotation > -30)
                this.rotation = -30;
        }
        else if (this.y > this.jumpPos){
            if (this.rotation < 80)
                this.rotation += 10;
        }

        this.rotatedImage = utils.rotateImage(this.image, this.rotation);
        g.drawImage(this.rotatedImage, this.x, this.y, this);
    }

    public void jump() {
        this.gravity = gravityConst * 8 * -1;
        this.isJumping = true;
        this.jumpPos = this.y;
    }

    public void fall(){
        this.y += gravity;
        gravity += gravityConst;
    }

    public void think(ArrayList<Pipe> pipes) throws Exception{
        //get nearest pipe infront of bird
        Pipe closestPipe = null;
        double diff;
        double record = Double.POSITIVE_INFINITY;
        for (int i=0; i<pipes.size(); i++) {
            diff = pipes.get(i).x - this.x;
            if (diff > 0 && diff < record) {
                record = diff;
                closestPipe = pipes.get(i);
          }
        }
        if (closestPipe != null){
            double [][] inputArray ={ // if i change these values the ai changes a LOT
                                    {this.y},
                                    {closestPipe.x - this.x + this.rotatedImage.getWidth()}, 
                                    {closestPipe.topY + closestPipe.topHeight - this.y},
                                    {closestPipe.bottomY - this.y - this.rotatedImage.getHeight()},
                                    {this.gravity}
                                    };
            Matrix inputs = new Matrix(inputArray.length, 1);
            inputs.mapSigmoid(); // normalize inputs
            inputs.matrix = inputArray;
            Matrix action = this.brain.predict(inputs);
            if (action.matrix[0][0] > action.matrix[1][0]){
                this.jump();
            }
        }
        
    }

    public Bird[] bread(Bird mate, int x, int y, int width) throws Exception{
        // mush parents brains and put it baby
        Bird [] offspring = {new Bird(x, y, width), new Bird(x, y, width)};
        NeuralNetwork[] offspringBrains = NeuralNetwork.crossover(this.brain, mate.brain);
        offspring[0].brain = offspringBrains[0];
        offspring[1].brain = offspringBrains[1];
        offspring[0].brain.mutate(0.1, 0.1);
        offspring[1].brain.mutate(0.1, 0.1);
        return offspring; 
    }

    public static Bird copy(Bird toCopy){
        Bird birdCopy = new Bird(toCopy.x, toCopy.y, toCopy.width);
        birdCopy.brain = toCopy.brain;
        return birdCopy;
    }

    public static SerializedBird serialize(Bird toSerialize){
        return new SerializedBird(toSerialize.brain, toSerialize.score, toSerialize.pipeScore);
    }
}
