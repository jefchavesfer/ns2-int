
package io.java;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;


/**
 * @author jchaves
 */
public class FileReader {

    private BufferedReader br;
    private Logger log;

    /**
     * @param fileName
     */
    public FileReader(String fileName) {
        this.log = Logger.getLogger(this.getClass().getName());
        try {
            FileInputStream fstream = new FileInputStream(fileName);
            DataInputStream in = new DataInputStream(fstream);
            this.br = new BufferedReader(new InputStreamReader(in));
        } catch (Exception e) {
            this.log.severe("Error: " + e.getMessage());
            this.log.severe("StackTrace: " + e.getStackTrace());
        }
    }

    /**
     * @return line
     */
    public String readLine() {
        String line = null;
        try {
            line = this.br.readLine();
        } catch (IOException e) {
            this.log.severe("Error: " + e.getMessage());
            this.log.severe("StackTrace: " + e.getStackTrace());
        }
        return line;
    }

    /**
     */
    public void close() {
        try {
            this.br.close();
        } catch (IOException e) {
            this.log.severe("Error: " + e.getMessage());
            this.log.severe("StackTrace: " + e.getStackTrace());
        }
    }
}
