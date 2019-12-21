import java.io.*;

public class Nussinov {
    // Variables
    private static double DPMatrix[][];
    private static String sequence;
    private static int sequenceLength;

    // Pair penalties
    private static final double unpairedPenalty = .1;
    private static final int minimumLoop = 3;

    // Constructor
    Nussinov(String sequence) {
        this.sequence = sequence;
        this.sequenceLength = sequence.length();
        DPMatrix = new double[sequenceLength][sequenceLength];
        // Initialize the DPMatrix
        for (int i = 0; i < sequenceLength; i++){
            for (int j = 0; j < sequenceLength; j++){
                // Initialize to zero unless bottom left one in from diagonal and top right corner after shift
                if (i == j) {
                    DPMatrix[i][j] = 0;
                } else if (i-1 == j){
                    DPMatrix[i][j] = 0;
                } else if (i+1 == j){
                    DPMatrix[i][j] = 0;
                } else if (i+2 == j){
                    DPMatrix[i][j] = 0;
                } else if (i+3 == j){
                    DPMatrix[i][j] = 0;
                } else {
                    DPMatrix[i][j] = Double.MAX_VALUE ;
                }
            }
        }
    }

    // Modifies nussinov to find MFE fills DPMatrix
    public static void modifiedNussinov() {
        // Declare j
        int j;

        // Fill in DPMatrix
        for (int k = 0; k < sequence.length(); k++) {
            for (int i = 0; i < sequence.length()-1; i++) {
                // If bigger than allowed loop size
                if (k > minimumLoop - 1) {
                    // Index is in bounds
                    if ((i + k + 1) <= sequence.length()-1) {
                        // Set j to i + k + 1
                        j = i + k + 1;

                        // Set min to max value
                        double minimum = Double.MAX_VALUE;

                        // Check for minimum
                        minimum = Math.min(minimum, Math.min((DPMatrix[i + 1][j] ), (DPMatrix[i][j - 1] )) );

                        // Adjust for pair scores
                        if (checkPair(sequence.charAt(i), sequence.charAt(j)) == 1) {
                            // Check for minimum
                            minimum = Math.min(minimum, DPMatrix[i + 1][j - 1] - 2);
                        } else if (checkPair(sequence.charAt(i), sequence.charAt(j)) == 2) {
                            // Check for minimum
                            minimum = Math.min(minimum, DPMatrix[i + 1][j - 1] - 3);
                        }
                        for (int l = i + 1; l < j; l++) {
                            // Check for minimum
                            minimum = Math.min(minimum, (DPMatrix[i][l]) + (DPMatrix[l + 1][j]));
                        }

                        // Set the matrix value
                        DPMatrix[i][j] = minimum;
                    }
                }
            }
        }

        // Run the traceback
        String structure = structureTraceback();

        // Create file for writer
        File file = new File("5.o1");

        // Create writer
        try {
            // Writer
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            // Write sequence
            writer.write(sequence);
            writer.write('\n');

            // Write MFE
            writer.write(Double.toString(DPMatrix[0][sequenceLength - 1]));
            writer.write('\n');

            // Write structure
            writer.write(structure);

            // Close writer
            writer.close();

        } catch (IOException e) {
            // Print error
            System.out.println("Error opening file 5.o1");
        }

    }

    // Method to check if there is a pair and what kind for scoring
    public static int checkPair(char x, char y) {
        // Check what kind of pair it is if it is one
        if ((x == 'A' && y == 'U') || (x == 'U' && y == 'A') || (x == 'G' && y == 'U') || (x == 'U' && y == 'G')){
            // Pair is -2
            return 1;
        } else if ((x == 'C' && y == 'G') || (x == 'G' && y == 'C')){
            // Pair is -3
            return 2;
        }
        // Not a pair
        return 0;
    }

    // Traceback to find optimal structure
    public static String structureTraceback(){
        // Set multipleOptimal to false
        boolean multipleOptimal = false;

        // Set i,j values to 0
        int i = 0;
        int j = 0;

        // Set isPair to false
        boolean isPair = false;

        // Initialize string to store structure
        StringBuilder optimalStructure = new StringBuilder();

        // Loop through to traceback
        for (int k = 0; k < sequence.length()-1; k++) {
            // First thing to be added to string builder
            if (k == 0) {
                // Check if there are multiple paths
                if(DPMatrix[0][sequence.length() - 1] == DPMatrix[0][sequence.length() - 2] && DPMatrix[0][sequence.length() -1] == DPMatrix[1][sequence.length()-1]){
                    // Set multiple optimal to true
                    multipleOptimal = true;

                    // Came from the left
                    i = 0;

                    // Keep going left
                    j = sequence.length() - 2;

                    // Add to optimal structure
                    optimalStructure.insert(0,".");

                } else if (DPMatrix[0][sequence.length() - 1] == DPMatrix[0][sequence.length() - 2]) {
                    // Came from left
                    i = 0;

                    // Keep going left
                    j = sequence.length() - 2;

                    // Add to optimal structure
                    optimalStructure.insert(0,".");

                } else if (DPMatrix[0][sequence.length() - 1]  == DPMatrix[1][sequence.length() - 1]){
                    // Came from up
                    i = 1;

                    // Don't go left
                    j = sequence.length() - 1;

                    // Add to optimal structure
                    optimalStructure.insert(0,".");

                } else if (checkPair(sequence.charAt(0), sequence.charAt(sequence.length() - 1)) == 1) {
                    // Came from up (diagonal)
                    i = 1;

                    // Go left
                    j = sequence.length() - 2;

                    // It is a pair (with -2)
                    isPair = true;

                    // Add to optimal structure
                    optimalStructure.insert(0, "(");

                } else if (checkPair(sequence.charAt(0), sequence.charAt(sequence.length() - 1)) == 2) {
                    // Came form up (diagonal)
                    i = 1;

                    // Go left
                    j = sequence.length() - 2;

                    // It is a pair (with -3)
                    isPair = true;

                    // Add to optimal structure
                    optimalStructure.insert(0, "(");

                } else {
                    // Bifurcation
                    optimalStructure.insert(0,".");
                }
            // Not first thing in string builder
            } else {
                //
                if (DPMatrix[i][j] == DPMatrix[i][j - 1] && DPMatrix[i][j]  == DPMatrix[i + 1][j]){
                    // Go left
                    j = j-1;

                    // Add to optimal structure
                    optimalStructure.insert(0,".");

                    // There is multiple optimal
                    multipleOptimal = true;

                } else if (DPMatrix[i][j] == DPMatrix[i][j - 1]) {
                    // Go left
                    j = j-1;

                    // Add to optimal structure
                    optimalStructure.insert(0,".");

                } else if (DPMatrix[i][j]  == DPMatrix[i + 1][j]){
                    // Go down
                    i = i+1;

                    // Add to optimal structure
                    optimalStructure.insert(0,".");

                // Check if it is a pair resulting in -2
                } else if (checkPair(sequence.charAt(i), sequence.charAt(j)) == 1) {
                    // Move diagonally
                    i = i+1;
                    j = j-1;

                    // If not pair
                    if (!isPair) {
                        // Add to optimal structure
                        optimalStructure.insert(0, ")");
                    } else if (isPair){
                        // Add to optimal structure
                        optimalStructure.insert(0, "(");
                    }

                    // Set pair to true
                    isPair = true;

                // Check if it is a pair resulting in -3
                } else if (checkPair(sequence.charAt(i), sequence.charAt(j)) == 2) {
                    // Check if it is a pair resulting in -2
                    if (DPMatrix[i][j] == DPMatrix[i + 1][j - 1] - 3){
                        // Move diagonally
                        i = i+1;
                        j = j-1;

                        // If not pair
                        if (!isPair) {
                            // Add to optimal structure
                            optimalStructure.insert(0, ")");
                        } else if (isPair){
                            // Add to optimal structure
                            optimalStructure.insert(0, "(");
                        }

                        // Set pair to true
                        isPair = true;
                    }
                } else {
                    // add to optimal structure
                    optimalStructure.insert(0,".");
                }
            }
        }

        // If pair
        if (isPair) {
            // Add to optimal structure
            optimalStructure.insert(0, "(");
        } else {
            // Add to optimal structure
            optimalStructure.insert(0, ".");
        }

        // Create file for multiple optimal
        File file = new File("5.o2");

        // Try opening a file to write to
        try {
            // Create writer
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            // Determine what to write
            if (multipleOptimal){
                writer.write("YES");
            } else {
                writer.write("NO");
            }

            // Close writer
            writer.close();

        } catch (IOException e) {
            // Display error message
            System.out.println("Error opening file 5.o2");
        }

        // Change structure into a string
        String structure = optimalStructure.toString();

        // Return the string
        return structure;

    }

    // Function for printing the DPMatrix
    public static void printDP(){
        for (int i = 0; i < sequence.length(); i++) {
            for (int j = 0; j < sequence.length(); j++) {
                System.out.print(DPMatrix[i][j] + " ");
            }
            System.out.println("");
        }
    }
}
