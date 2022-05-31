import java.lang.Math;

public class Matrix {
    int rows;
    int columns;
    double[][] matrix;
    public Matrix(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.matrix = new double[rows][columns];
    }

    public void fill(double n){
        // fill the matrix with value n
        for (int i=0; i<this.rows; i++){
            for(int j=0; j<this.columns; j++){
                this.matrix[i][j] = n;
            }
        }
    }

    public void randomize(){
        // place random doubles (0.0 to 0.99) in each matrix element
        for (int i=0; i<this.rows; i++){
            for(int j=0; j<this.columns; j++){
                this.matrix[i][j] = Math.random();
            }
        }
    }

    public void add(double n){
        // add a const to all elements of a matrix
        for (int i=0; i<this.rows; i++){
            for(int j=0; j<this.columns; j++){
                this.matrix[i][j] += n;
            }
        }
    }

    public double sigmoid(double x){
        // sigmoid function...
        return (1/( 1 + Math.pow(Math.E,(-1*x))));
    }

    public void mapSigmoid(){
        // map the sigmoid function to all elements of the matrix
        for (int i=0; i<this.rows; i++){
            for(int j=0; j<this.columns; j++){
                this.matrix[i][j] = sigmoid(this.matrix[i][j]);
            }
        }
    }

    public void mutate(double chance) throws Exception{ // chance between 0 and 1
        // x% chance of mutating an element in the matrix
        double n;
        if (chance >= 0 && chance <=1){
            for (int i=0; i<this.rows; i++){
                for(int j=0; j<this.columns; j++){
                    n = Math.random();
                    if (n < chance){ //todo check if prbability is correct
                        this.matrix[i][j] = Math.random();
                    }
                }
            }
        }
        else{
            throw new Exception("ERROR: chance has to be between 0.0 and 1.0");
        }
    }


    public static Matrix matrixMultiplication(Matrix m1, Matrix m2) throws Exception{
        // perform matrix multiplication 
        if (m1.columns == m2.rows){
            Matrix matrixProduct = new Matrix(m1.rows, m2.columns);
            for (int i=0; i<m1.rows; i++){
                for(int j=0; j<m2.columns; j++){
                    for (int k=0; k<m1.columns; k++){
                        matrixProduct.matrix[i][j] += m1.matrix[i][k] * m2.matrix[k][j];
                    }
                }
            }

            return matrixProduct;
        }
        else{
            throw new Exception("m1.rows must be equal to m2.columns");
        }
    }

    public static Matrix combineColumnsMatrices(Matrix m1, Matrix m2) throws Exception{
        // combine 2 matrices that have the same amount of columns
        if (m1.columns == m2.columns){
            Matrix combinedMatrix = new Matrix(m1.rows+m2.rows, m1.columns);
            for (int i=0; i<m1.rows; i++){
                for(int j=0; j<m1.columns; j++){
                    combinedMatrix.matrix[i][j] = m1.matrix[i][j];
                }
            }
            for (int i=0; i<m2.rows; i++){
                for(int j=0; j<m2.columns; j++){
                    combinedMatrix.matrix[i+m1.rows][j] = m2.matrix[i][j];
                }
            }
            return combinedMatrix;
        }
        else{
            throw new Exception("m1.columns must be equal to m2.columns");
        }
    }

    public static Matrix[] transverseCut(Matrix m, int incision) throws Exception{
        // cut a matrix by separating rows; m --> [m1, m2]
        // m.matrix[incision] is gonna be in the upper part of the cut
        if (incision <= m.rows){
            Matrix m1 = new Matrix(incision, m.columns);
            Matrix m2 = new Matrix(m.rows-incision, m.columns);

            for (int i=0; i<m1.rows; i++){
                for(int j=0; j<m1.columns; j++){
                    m1.matrix[i][j] = m.matrix[i][j];
                }
            }
            for (int i=0; i<m2.rows; i++){
                for(int j=0; j<m2.columns; j++){
                    m2.matrix[i][j] = m.matrix[i+incision][j];
                }
            }

            return new Matrix[] {m1, m2};
        }
        else{
            throw new Exception("incision must be less than or equal to m.rows");
        }

    }

    public static void print(Matrix m){
        //print a matrix to terminal
        boolean isEmpty = true;
        for (int i=0; i<m.rows; i++){
            isEmpty = false;
            System.out.printf("row %d:  ", i);
            for(int j=0; j<m.columns; j++){
                System.out.print(m.matrix[i][j]+" ");
            }
            System.out.println("");
        }

        if (isEmpty){
            System.out.println("Matrix is empty");
        }
    }
    
}
