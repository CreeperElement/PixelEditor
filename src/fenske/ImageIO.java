package fenske;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import org.omg.CORBA.INTERNAL;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.Buffer;
import java.util.EmptyStackException;
import java.util.IllegalFormatException;
import java.util.Scanner;

public class ImageIO {

    public static BufferedImage read(File file) throws IOException {
        String[] fileNameParts = file.getName().split("\\.");
        MSOEImageIO msoeImageIO = new MSOEImageIO();
        BufferedImage image = null;

        switch (fileNameParts[fileNameParts.length - 1]) {
            case "msoe":
                image = msoeImageIO.readMSOE(file);
                break;
            case "bmsoe":
                image = msoeImageIO.readBMSOE(file);
                break;
            default:
                image = SwingFXUtils.fromFXImage(new Image(file.toURI().toURL().toString()), null);
                break;
        }
        return image;
    }

    public static void write(BufferedImage image, File file) throws IOException {
        //Split into filename, extension
        String[] fileNameParts = file.getName().split("\\.");
        //Extension letters
        String extension = fileNameParts[fileNameParts.length-1];
        //Easier to instantiate here instead of doing wherever needed
        MSOEImageIO msoeImageIO = new MSOEImageIO();

        switch (extension) {
            case "msoe":
                msoeImageIO.writeMSOE(image, file);
                break;
            case "bmsoe":
                break;
            default:
                //Write the buffered image with the extension where file points it to.
                javax.imageio.ImageIO.write(image, extension, file);
                break;
        }
    }

    /**
     * Helper inner class. Used to read .msoe and .bmsoe files
     */
    private static class MSOEImageIO{
        /**
         * Turns a .msoe file into a Image class
         * @param file File pointing to file
         * @return Image class of .msoe file
         */
        public static BufferedImage readMSOE(File file) throws IOException {
            Scanner scanner = new Scanner(file);
            if(!scanner.nextLine().equals("MSOE")){
                throw new IOException("MSOE File Is Corrupt: Doesn't contain MSOE on first line.");
            }
            String[] numberArgs = scanner.nextLine().split(" ");
            int width = Integer.parseInt(numberArgs[0]);
            int height  = Integer.parseInt(numberArgs[1]);
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);


                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width && scanner.hasNext(); x++) {
                        String color = scanner.next();
                        int red = Integer.valueOf(color.substring(1, 3), 16);
                        int green = Integer.valueOf(color.substring(3, 5), 16);
                        int blue = Integer.valueOf(color.substring(5, 7), 16);
                        int alpha = Integer.valueOf(color.substring(7), 16);
                        image.setRGB(x, y, new Color(red, green, blue, alpha).getRGB());
                    }
                }


            //scanner.close();
            return image;
        }

        /**
         * Turns a .bmsoe file into a Image class
         * @param file File pointing to file
         * @return Image class of .bmsoe file
         */
        public static BufferedImage readBMSOE(File file){
          /*  if(fileNameParts[fileNameParts.length-1].equals("MSOE")||fileNameParts[fileNameParts.length-1].equals("BMSOE")){
                imageLoader.readMSOE(imageFile);
            }else{
                imageLoader.read(file);
                try{
                    FileInputStream inputStream = new FileInputStream(file.getAbsolutePath());
                    selectedImage = imageFile;
                    editedImage = new Image(inputStream);
                    imageViewer.setImage(editedImage);
                }catch (FileNotFoundException e){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Error reading image...");
                }
            }*/
          return null;
        }

        /**
         * Writes the image to where file points it
         * @param image Generic image class
         * @param file .msoe file
         */
        public static void writeMSOE(BufferedImage image, File file) throws IOException {
            FileWriter writer = new FileWriter(file);
            writer.append("MSOE"+System.lineSeparator());
            writer.append(image.getWidth() + " " + image.getHeight() + System.lineSeparator());
            for(int y = 0; y < image.getHeight(); y++){
                for(int x = 0; x < image.getWidth(); x++){
                    Color color = new Color(image.getRGB(x, y));
                    String red = String.format("%02X", color.getRed());
                    String green = String.format("%02X", color.getGreen());
                    String blue = String.format("%02X", color.getBlue());
                    String alpha = String.format("%02X", color.getAlpha());
                    writer.write("#"+red+green+blue+alpha+" ");
                }
                writer.append(System.lineSeparator());
                writer.flush();
            }
            writer.close();
        }

        /**
         * Writes the image to where file points it
         * @param image Generic image class
         * @param file .bmsoe file
         */
        public static void writeBMSOE(BufferedImage image, File file){

        }
    }
}
