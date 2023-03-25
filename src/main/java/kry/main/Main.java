package kry.main;

import kry.spn.SPN;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

public class Main {
    public static void main(String[] args) {
        SPN spn = new SPN();
        TextCrypt tc = new TextCrypt(spn);

        /***
         * TextCrypt contains ctr logic and uses the defined SPN.
         * Didn't have time to make this more sophisticated/with parameters so basically anything is hardcoded. .
         */


        //Thanks to chatGpT for saving filecontent in to a variable
        String fileName = "chiffre.txt";
        URL resourceUrl = Main.class.getClassLoader().getResource(fileName);
        String line = null;
        StringBuilder sb = new StringBuilder();
        try {
            assert resourceUrl != null;
            FileReader fileReader = new FileReader(resourceUrl.getFile());
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            bufferedReader.close();
        } catch (IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");
        }

        String fileContents = sb.toString();


        System.out.println(tc.decryptText(fileContents));




    }
}
