// A class for a 3 layered neural network
import java.util.Random;

public class NeuralNetwork implements java.io.Serializable{
    int inputCount;
    int hiddenCount;
    int outputCount;
    Matrix weightsIH;
    Matrix weightsHO;
    Matrix biasH;
    Matrix biasO;

    public NeuralNetwork(int inputCount, int hiddenCount, int outputCount){
        this.inputCount = inputCount;
        this.hiddenCount = hiddenCount;
        this.outputCount = outputCount;
        this.weightsIH = new Matrix(this.hiddenCount, this.inputCount);
        this.weightsHO = new Matrix(this.outputCount, this.hiddenCount);
        this.biasH = new Matrix(this.hiddenCount, 1);
        this.biasO = new Matrix(this.outputCount, 1);
        this.weightsIH.randomize();
        this.weightsHO.randomize();
        this.biasH.randomize();
        this.biasO.randomize();
    }

    public Matrix predict(Matrix inputMatrix) throws Exception{
        //Multiply input matrix by wights and get hidden matrix
        Matrix hiddenMatrix = Matrix.matrixMultiplication(this.weightsIH, inputMatrix);
        //Add bias to all hidden matrix nodes
        hiddenMatrix.add(biasH);
        //Apply activation function to make all values btwn 0 and 1
        hiddenMatrix.mapSigmoid();
        //Reapeat process for hidden/output layer weights 
        Matrix outputMatrix = Matrix.matrixMultiplication(this.weightsHO, hiddenMatrix);
        outputMatrix.add(biasO);
        outputMatrix.mapSigmoid();
        //Matrix.print(outputMatrix);
        return outputMatrix;
        
    }

    public static NeuralNetwork[] crossover(NeuralNetwork dad, NeuralNetwork mom) throws Exception{
        // lol this is essentially preforming the crossing over proccess
        Random rand = new Random();
        Matrix[] dadSplit;
        Matrix[] momSplit;
        NeuralNetwork baby1 = new NeuralNetwork(dad.inputCount, dad.hiddenCount, dad.outputCount);
        NeuralNetwork baby2 = new NeuralNetwork(dad.inputCount, dad.hiddenCount, dad.outputCount);

        int cutIH = rand.nextInt(dad.weightsIH.rows);
        int cutHO = rand.nextInt(dad.weightsHO.rows);

        // crossover of intput-hidden weights
        dadSplit = Matrix.transverseCut(dad.weightsIH, cutIH);
        momSplit = Matrix.transverseCut(mom.weightsIH, cutIH);
        baby1.weightsIH = Matrix.combineColumnsMatrices(dadSplit[0], momSplit[1]);
        baby2.weightsIH = Matrix.combineColumnsMatrices(momSplit[0], dadSplit[1]);

        // crossover of hidden-output weights
        dadSplit = Matrix.transverseCut(dad.weightsHO, cutHO);
        momSplit = Matrix.transverseCut(mom.weightsHO, cutHO);
        baby1.weightsHO = Matrix.combineColumnsMatrices(dadSplit[0], momSplit[1]);
        baby2.weightsHO = Matrix.combineColumnsMatrices(momSplit[0], dadSplit[1]);

        return new NeuralNetwork[] {baby1, baby2};
    }

    public void mutate(double chance) throws Exception{
        this.weightsIH.mutate(chance);
        this.weightsHO.mutate(chance);
        this.biasH.mutate(chance);
        this.biasO.mutate(chance);   
    }
    public void mutate(double chance, double rangeOfChange) throws Exception{
        this.weightsIH.mutate(chance, rangeOfChange);
        this.weightsHO.mutate(chance, rangeOfChange);
        this.biasH.mutate(chance, rangeOfChange);
        this.biasO.mutate(chance, rangeOfChange);   
    }
}
