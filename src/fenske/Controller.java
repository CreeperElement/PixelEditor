/*
 * SE 1021
 * Winter 2018
 * Lab Eight Image Viewer
 * Seth Fenske
 * Created 2/7/18
 */
package fenske;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Array;

public class Controller {

    public void initialize() throws IOException {
        filterWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader();
        final Parent fxmlRoot =
                fxmlLoader.load(
                        new FileInputStream(new File("kernel.fxml")));
        filterWindow.setScene(new Scene(fxmlRoot));
        filterWindow.setTitle("Filter Window");
        KernelController kernelController = fxmlLoader.getController();
        kernelController.setController(this);

        imageViewer.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClick);
    }

    /**Current file being displayed and worked on.*/
    private File selectedImageFile;
    /**Current loaded image*/
    private BufferedImage selectedImage;

    /**Used to keep track of selected meme status.*/
    private boolean stickerSelected = false;

    Transformation red = (x, y, color)->{
        return  new Color(color.getRed(), 0, 0, color.getAlpha());
    };

    Transformation grayscale = (x,y,color)->{
        int red = (int)(color.getRed()*.2126);
        int green = (int)(color.getGreen()*.7152);
        int blue = (int)(color.getBlue()*.0722);
        //int alpha = (int)(color.getAlpha())
        int finalColor = red+green+blue;
        System.out.println(color.getAlpha());
        return new Color(finalColor, finalColor, finalColor, color.getAlpha());
    };

    /**Used in file chooser to filter out unwanted image formats.*/
    private static final FileChooser.ExtensionFilter[] FILE_FILTERS = new FileChooser.ExtensionFilter[]{
            new FileChooser.ExtensionFilter("All Images","*.gif", "*.jpg", "*.bmsoe", "*.msoe", "*.png"),
            new FileChooser.ExtensionFilter("GIF","*.gif"),
            new FileChooser.ExtensionFilter("JPEG","*.jpg"),
            new FileChooser.ExtensionFilter("BMSOE","*.bmsoe"),
            new FileChooser.ExtensionFilter("MSOE","*.msoe"),
            new FileChooser.ExtensionFilter("PNG","*.png")
    };

    @FXML
    //GUI component used to display image
    private ImageView imageViewer;

    @FXML
    /**
     * Writes the current selected image, otherwise it opens save as dialog.
     */
    private void save(){
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

    /**
     * Takes currently selected image and writes it to file.
     * @param outputFile File locatio to write to.
     */
    private void save(File outputFile){
        ImageIO imageWrite = new ImageIO();
        try {
            imageWrite.write(selectedImage, outputFile);
        } catch (IOException e) {
            displayError("Error Encountered While Trying To Save: "+ selectedImageFile.getAbsolutePath(), e.getMessage());
        }
    }

    @FXML
    /**
     * Opens file save dialog and used ImageIO to save to that file
     */
    private void saveAs(){
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(FILE_FILTERS);
        File writeFile = chooser.showSaveDialog(null);

        if(writeFile==null)
            return;

        save(writeFile);
    }

    @FXML
    /**
     * Opens open file dialog. If user cancels nothing happens
     */
    private void open(){
        //File chooser dialog
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(FILE_FILTERS);
        File imageFile = chooser.showOpenDialog(null);
        if(imageFile!=null){
            selectedImageFile = imageFile;
            reload();
        }
    }

    @FXML
    /**
     * Goes back to location of currently selected image file and loads it again.
     * If the image has been edited between refreshes, it will reflect said chanes.
     */
    private void reload() {
        //User may cancel, or select an illegal file type
        if(selectedImageFile!=null && selectedImageFile.exists()){
            try{
                ImageIO imageIO = new ImageIO();
                setImage(imageIO.read(selectedImageFile));
            }catch (IOException e){
                displayError("Error Encountered While Trying To Load: "+ selectedImageFile.getAbsolutePath(), e.getMessage());
            } catch (CorruptDataException e) {
                displayError("Error Encountered While Trying To Load: "+ selectedImageFile.getAbsolutePath() +"\n File is corrupt", e.getMessage());
            }catch (NumberFormatException e){
                displayError("Error Encountered While Trying To Load: "+ selectedImageFile.getAbsolutePath() +"\n File is corrupt", e.getMessage());
            }catch (ArrayIndexOutOfBoundsException e){
                displayError("Error Encountered While Trying To Load: " + selectedImageFile.getAbsolutePath() + "\n File is corrupt", e.getMessage());
            }
        }
    }

    private void displayError(String contentText, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(contentText);
        alert.showAndWait();
        ErrorLogger.Log(message);
    }

    /**
     * Applies transformation to the BufferedImage
     * @param transformation Color scheme transformation
     * @param bufferedImage Image that you want to change
     * @return A transformed image
     */
    private BufferedImage transform(Transformation transformation, BufferedImage bufferedImage){
        for(int i = 0; i < bufferedImage.getWidth(); i++){
            for (int k = 0; k < bufferedImage.getHeight(); k++){
                bufferedImage.setRGB(i, k , transformation.transform(i, k, new Color(bufferedImage.getRGB(i,k))).getRGB());
            }
        }
        return bufferedImage;
    }

    @FXML
    /**
     * Turns the selected image to grayscale, mutates original BufferedImage
     */
    private void makeGrayscale(){
        setImage(transform(grayscale, selectedImage));
    }

    @FXML
    /**
     * Turns selected image negative, does mutate BufferedImage
     */
    private void makeNegative(){
        Transformation negative = (x,y,color)->{
            return new Color(255-color.getRed(), 255-color.getGreen(), 255-color.getBlue());
        };
        setImage(transform(negative,selectedImage));
    }

    @FXML
    /**
     * Removes the green and blue channels of the image
     */
    private void makeRed(){
        setImage(transform(red, selectedImage));
    }

    @FXML
    private void makeRedGray(){
        Transformation transformation;
        for(int y = 0; y < selectedImage.getHeight(); y++){
            if(y%2 == 0){
                transformation = red;
            }else{
                transformation = grayscale;
            }
            for(int x = 0; x < selectedImage.getWidth(); x++){
                selectedImage.setRGB(x, y, transformation.transform(x, y, new Color(selectedImage.getRGB(x,y))).getRGB());
            }
        }
        setImage(selectedImage);
    }

    Stage filterWindow;

    @FXML
    private void showFilter() throws IOException {
        filterWindow.show();
    }

    BufferedImage image;

    @FXML
    EventHandler mouseClick  = event -> {
        if(event.getEventType() == MouseEvent.MOUSE_CLICKED && stickerSelected){
            double pixelDensityX = selectedImage.getWidth()/imageViewer.getFitWidth();
            double pixelDensityY = selectedImage.getHeight()/imageViewer.getFitHeight();

            int x = (int)(((MouseEvent)event).getX()*pixelDensityX);
            int y = (int)(((MouseEvent)event).getY()*pixelDensityY);

            int i = 0;
            int k = 0;

            while(i < image.getWidth() || k < image.getHeight()){
                if(i < image.getWidth() && x+i < selectedImage.getWidth() && k < image.getHeight() && y+k < selectedImage.getHeight()){
                    selectedImage.setRGB(x+i, y+k, image.getRGB(i, k));
                }
                if(++i > selectedImage.getWidth()){
                    i = 0;
                    k++;
                }
            }
            setImage(selectedImage);
        }
    };

    @FXML
    CheckBox checkBox;

    @FXML
    private void selectSticker(){
        stickerSelected = checkBox.isSelected();

        if(stickerSelected) {
            //File chooser dialog
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().addAll(FILE_FILTERS);
            File imageFile = chooser.showOpenDialog(null);

            ImageIO imageIO = new ImageIO();
            try {
                image = imageIO.read(imageFile);
            } catch (IOException e) {
                displayError("Error Encountered While Trying To Load: " + selectedImageFile.getAbsolutePath() + "\n File is corrupt", e.getMessage());
                ErrorLogger.Log(e.getMessage());
            } catch (CorruptDataException e) {
                displayError("Error Encountered While Trying To Load: " + selectedImageFile.getAbsolutePath() + "\n File is corrupt", e.getMessage());
                ErrorLogger.Log(e.getMessage());
            }
        }
    }

    /**
     * Helper method which allows for easy setting of the image. Removes confusing BufferedImage and fx image
     * ambiguity
     * @param image BufferedImage to be shown
     */
    public void setImage(BufferedImage image){
        selectedImage = image;
        imageViewer.setImage(SwingFXUtils.toFXImage(selectedImage, null));
    }

    public BufferedImage getImage() {
        return selectedImage;
    }

    @FXML
    /**
     * Clears the error log log.txt
     */
    private void clearLog(){
        ErrorLogger.clearLog();
    }
}
