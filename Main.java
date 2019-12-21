import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;

public class Main {
    public static void main(String args[]) {
        // Create file
        File file = new File("5.in");

        // Create string to hold sequence
        String sequence = "";

        // Try reading from file
        try {
            // Create reader
            BufferedReader reader = new BufferedReader(new FileReader(file));

            // Read in from file for the sequence
            sequence = reader.readLine();

            // Make sure the line is the sequence
            if (sequence.charAt(0) == '>'){
                sequence = reader.readLine();
            }
        } catch (IOException e) {
            // Display error message
            System.out.println("Cannot open file 5.in");
        }

        // Create nussinov
        Nussinov nussinov = new Nussinov(sequence);

        // Run nussinov algorithm
        nussinov.modifiedNussinov();
    }
}
