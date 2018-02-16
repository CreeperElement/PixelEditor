/*
 * SE 1021
 * Winter 2018
 * Lab Eight Image Viewer
 * Seth Fenske
 * Created 2/7/18
 */
package fenske;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class ImageIO {

    /**
     *
     * @param file File location to be read
     * @return BufferedImage containing the Image data
     */
    public BufferedImage read(File file) throws IOException, CorruptDataException, NumberFormatException {
        String[] fileNameParts = file.getName().split("\\.");
        BufferedImage image = null;

        switch (fileNameParts[fileNameParts.length - 1]) {
            case "msoe":
                image = readMSOE(file);
                break;
            case "bmsoe":
                image = readBMSOE(file);
                break;
            default:
                image = SwingFXUtils.fromFXImage(new Image(file.toURI().toURL().toString()), null);
                break;
        }
        return image;
    }

    /**
     * General write method. Decides what kind of file is being written and calls appropriate helper methods
     * @param image Image to be written
     * @param file File Location to be written to
     */
    public void write(BufferedImage image, File file) throws IOException {
        //Split into filename, extension
        String[] fileNameParts = file.getName().split("\\.");
        //Extension letters
        String extension = fileNameParts[fileNameParts.length-1];

        switch (extension) {
            case "msoe":
                writeMSOE(image, file);
                break;
            case "bmsoe":
                writeBMSOE(image, file);
                break;
            default:
                //Write the buffered image with the extension where file points it to.
                javax.imageio.ImageIO.write(image, extension, file);
                break;
        }
    }

    /**
     * Turns a .msoe file into a Image class
     * @param file File pointing to file
     * @return Image class of .msoe file
     */
    private BufferedImage readMSOE(File file) throws IOException, CorruptDataException, NumberFormatException {
        Scanner scanner = new Scanner(file);
        if(!scanner.nextLine().equals("MSOE")){
            throw new CorruptDataException("MSOE File Is Corrupt: Doesn't contain MSOE on first line.");
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

        scanner.close();
        return image;
    }

    /**
     * Turns a .bmsoe file into a Image class
     * @param file File pointing to file
     * @return Image class of .bmsoe file
     */
    private  BufferedImage readBMSOE(File file) throws IOException, CorruptDataException {
        //Set up the stream which references the file
        FileInputStream fileInputStream = new FileInputStream(file);
        //DataInStream is easier to use than FileInStream, needs an existing stream to initialize
        DataInputStream inputStream = new DataInputStream(fileInputStream);

        //One char one byte, 5 bytes gets us 5 chars. They should be B-M-S-O-E
        byte[] data = new byte[5];
        inputStream.read(data);

        //Used to combine the characters
        String fileHeader = "";

        //Interpret the bytes to chars, then combine them into a string
        int i = 0;
        while (i < (data.length)){
            fileHeader += Character.toString((char)data[i]);
            i++;
        }

        //Check the letters
        if(!fileHeader.equals("BMSOE")){
            throw new CorruptDataException("BMSOE file header not found");
        }

        //Set input byte size to 4. Thats the size of an Integer
        data = new byte[4];

        //Get the width
        inputStream.read(data);
        int width = ByteBuffer.wrap(data).getInt();

        //Get the height
        inputStream.read(data);
        int height = ByteBuffer.wrap(data).getInt();

        //Image to be filled, and returned
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        //X and Y coords of the pixels
        int x = 0;
        int y = 0;
        while(x<width-1||y<height-1){
            //Get next 4 bytes of data, one byte per color
            inputStream.read(data);

            //Create the color by passing each byte-long integer into the args for R,G,B,A
            Color color = new Color(data[0]&0xff, data[1]&0xff, data[2]&0xff, data[3]&0xff);

            //Set the color at the pixel spot
            image.setRGB(x, y, color.getRGB());

            //If reached the end of the row of the pixels
            if(++x >= width){
                //Remember the 16 byte rule. Skip any "leftover" bytes
                inputStream.skipBytes((x*4)%16);
                x = 0;
                y++;
            }
        }
        //CLOSE THE STREAMS
        fileInputStream.close();
        inputStream.close();
        return image;
    }

    /**
     * Writes the image to where file points it
     * @param image Generic image class
     * @param file .msoe file
     */
    private void writeMSOE(BufferedImage image, File file) throws IOException {
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
    private void writeBMSOE(BufferedImage image, File file) throws IOException {
        //Basic input stream of the specified file
        FileOutputStream outputStream = new FileOutputStream(file);
        //Data input stream is easier to use than file input stream, needs an existing input stream to work
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        //Wrtites the string BMSOE to binary, convert to char array first because they are similar
        char[] letters = "BMSOE".toCharArray();
        for (char c: letters){
            dataOutputStream.write((byte)c);
        }

        //Integers are 4 bytes, don't need to do anything fancy for writing the dimensions
        dataOutputStream.writeInt(image.getWidth());
        dataOutputStream.writeInt(image.getHeight());

        //Initialize loop variables. data is reused, x and y are reference points for the pixels, avoids nested loop
        byte[] data = new byte[4];
        int x = 0;
        int y = 0;

        //While there are still pixels to loop through, use this instead of nested for loop
        while(x<image.getWidth()-1||y<image.getHeight()-1){
            //Get current pixel
            Color c = new Color(image.getRGB(x,y));

            //Write red, green, blue, alpha
            data[0] = (byte)c.getRed();
            data[1] = (byte)c.getGreen();
            data[2] = (byte)c.getBlue();
            data[3] = (byte)c.getAlpha();

            //Write current pixel
            for(byte b: data){
                dataOutputStream.write(b);
            }

            //If we reached the end of the row
            if(++x>image.getWidth()-1){
                //For the remaining bytes to be written to keep the 16 byte rule, write a byte
                for(int i = 0; i < (x*4)%16; i++){
                    dataOutputStream.writeByte(0);
                }
                x=0;
                y++;
            }
        }
        dataOutputStream.close();
        outputStream.close();
    }
}
