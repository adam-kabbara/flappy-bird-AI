// This class is used to serialize the nn and score of the best bird
// We cannot serialize the Bird class as it contains a BufferedImage
// So this class is used to save the important aspects of the best birds

class SerializedBird  implements java.io.Serializable{
    Integer score;
    Integer pipeScore;
    NeuralNetwork brain;

    public SerializedBird(NeuralNetwork brain, Integer score, Integer pipeScore){
        this.brain = brain;
        this.score = score;
        this.pipeScore = pipeScore;
    }

    public static Bird Deserialize(SerializedBird sb, int x, int y, int width){
        Bird bird = new Bird(x, y, width);
        bird.brain = sb.brain;
        bird.score = sb.score;
        bird.pipeScore = sb.pipeScore;
        return bird;
    }
}