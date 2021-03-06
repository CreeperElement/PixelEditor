/*
 * SE 1021
 * Winter 2018
 * Lab Eight Image Viewer
 * Seth Fenske
 * Created 2/7/18
 */
package fenske;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class ErrorLogger {

    //Default logger location
    private static String LOG_PATH = "log.txt";

    /**
     * Prints the stackTrace to the file as a new entrance
     * @param stackTrace Dump exception stack trace here
     */
    public static void Log(String stackTrace){
        File file = new File(LOG_PATH);
        String previousInfo = "";

        try (Scanner scanner = new Scanner(file)){
            while(scanner.hasNextLine()){
                previousInfo+=scanner.nextLine();
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try(PrintWriter writer = new PrintWriter(file);){
            writer.append(previousInfo +" \n");
            writer.append(stackTrace);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Takes the error log file, and whipes all text present. Leaing only a message saying when the last wipe occured.
     */
    public static void clearLog(){
        File file = new File(LOG_PATH);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        try(PrintWriter writer = new PrintWriter(file);){
            writer.print("Log Cleared On: " + dateFormat.format(date));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
