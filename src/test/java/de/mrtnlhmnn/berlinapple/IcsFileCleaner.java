package de.mrtnlhmnn.berlinapple;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

// parses and cleans the ics file - multiline strings starting with " " in the description are moved to the same line
public class IcsFileCleaner {
    public static void main(String[] args) throws IOException {

//TODO release lock on file - use try with resources?!
        boolean inDescription = false;

        String fileName = "src/main/resources/data/program.ics";
        File file = new File(fileName);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;
        String descriptionLines = "";
        while((line = br.readLine()) != null){
            if (!inDescription) {
                if (line.startsWith("DESCRIPTION")) {
                    inDescription = true;
                    descriptionLines = line;
                }
                else {
                    System.out.println(line);
                }
            }
            else {
                if (line.startsWith(" ")) {
                    descriptionLines += line.substring(1);
                }
                else {
                    System.out.println(descriptionLines);
                    System.out.println(line);
                    descriptionLines = "";
                    inDescription = false;
                }
            }
        }
    }
}
