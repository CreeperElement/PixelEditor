package fenske;

import com.sun.javafx.iio.ImageStorage;
import fenske.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import javafx.embed.swt.SWTFXUtils;
import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.image.*;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import javax.swing.plaf.synth.ColorType;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.font.ImageGraphicAttribute;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.Buffer;

public class Controller {

    /**Current file being displayed and worked on.*/
    private File selectedImageFile;
    private BufferedImage selectedImage;

    public static final FileChooser.ExtensionFilter[] FILE_FILTERS = new FileChooser.ExtensionFilter[]{
            new FileChooser.ExtensionFilter("All Images","*.gif", "*.jpg", "*.msoe", "*.png"),
            new FileChooser.ExtensionFilter("GIF","*.gif"),
            new FileChooser.ExtensionFilter("JPEG","*.jpg"),
            new FileChooser.ExtensionFilter("MSOE","*.msoe"),
            new FileChooser.ExtensionFilter("PNG","*.png")
    };

    @FXML
    ImageView imageViewer;

    @FXML
    public void save(){
        if(selectedImageFile ==null||selectedImage==null){
            saveAs();
        }else{
            ImageIO imageIO = new ImageIO();
            try {
                imageIO.write(selectedImage, selectedImageFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void save(File outputFile){
        ImageIO imageWrite = new ImageIO();
        try {
            imageWrite.write(selectedImage, outputFile);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Error Encountered While Trying To Save: "+ selectedImageFile.getAbsolutePath());
            ErrorLogger.Log(e.getMessage());
        }
    }

    @FXML
    public void saveAs(){
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(FILE_FILTERS);
        File writeFile = chooser.showSaveDialog(null);

        if(writeFile==null)
            return;

        save(writeFile);
    }

    @FXML
    public void open(){
        //File chooser dialog
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(FILE_FILTERS);
        File imageFile = chooser.showOpenDialog(null);
        if(imageFile!=null){
            selectedImageFile = imageFile;
        }
        reload();
    }

    @FXML
    private void reload() {
        //User may cancel, or select an illegal file type
        if(selectedImageFile!=null && selectedImageFile.exists()){
            try{
                setImage(ImageIO.read(selectedImageFile));
            }catch (IOException e){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Error Encountered While Trying To Load: "+ selectedImageFile.getAbsolutePath());
                ErrorLogger.Log(e.getMessage());
            }
        }
    }

    Transformation grayscale = (x,y,color)->{
        int red = (int)(color.getRed()*.2126);
        int green = (int)(color.getGreen()*.7152);
        int blue = (int)(color.getBlue()*.0722);
        int finalColor = red+green+blue;
        return new Color(finalColor, finalColor, finalColor);
    };

    Transformation negative = (x,y,color)->{
        return new Color(255-color.getRed(), 255-color.getGreen(), 255-color.getBlue());
    };

    private BufferedImage transform(Transformation transformation, BufferedImage bufferedImage){
        for(int i = 0; i < bufferedImage.getWidth(); i++){
            for (int k = 0; k < bufferedImage.getHeight(); k++){
                bufferedImage.setRGB(i, k , transformation.transform(i, k, new Color(bufferedImage.getRGB(i,k))).getRGB());
            }
        }
        return bufferedImage;
    }

    @FXML
    public void makeGrayscale(){
        setImage(transform(grayscale, selectedImage));
    }

    @FXML
    public void makeNegative(){
        setImage(transform(negative,selectedImage));
    }

    private void setImage(BufferedImage image){
        selectedImage = image;
        imageViewer.setImage(SwingFXUtils.toFXImage(selectedImage, null));
    }

    @FXML
    public void clearLog(){
        ErrorLogger.clearLog();
    }
}
