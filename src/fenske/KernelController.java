package fenske;

import com.sun.javaws.exceptions.InvalidArgumentException;
import com.sun.org.apache.xpath.internal.operations.Div;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import jdk.nashorn.internal.runtime.ECMAException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.DataOutputStream;

public class KernelController {

    BufferedImage image;
    Controller controller;

    public void setController(Controller controller){
        this.controller = controller;
    }

    @FXML
    TextField   A, B, C,
                D, E, F,
                G, H, I, Divisor;

    float[] kernel = new float[9];

    @FXML
    private void blur(){
        A.setText("0"); B.setText("1"); C.setText("0");
        D.setText("1"); E.setText("5"); F.setText("1");
        G.setText("0"); H.setText("1"); I.setText("0");
        Divisor.setText("");
    }

    @FXML
    private void sharpen(){
        A.setText("0"); B.setText("-1"); C.setText("0");
        D.setText("-1"); E.setText("5"); F.setText("-1");
        G.setText("0"); H.setText("-1"); I.setText("0");
        Divisor.setText("");
    }

    @FXML
    private void edge(){
        A.setText("0"); B.setText("-1"); C.setText("0");
        D.setText("-1"); E.setText("4"); F.setText("-1");
        G.setText("0"); H.setText("-1"); I.setText("0");
        Divisor.setText("");
    }

    @FXML
    private void Apply(){
        //Lots of things can go wrong here, so be careful
        try{
            //Default value in case something with the kernel isn't right
            float divisor = 1;

            //Check for text in the divisor
            if(Divisor.getText()!=null && !Divisor.getText().equals("")){
                //Get divisor value
                divisor = Float.parseFloat(Divisor.getText());
                if(Float.parseFloat(Divisor.getText())<=0) {
                    //Divisor must be positive
                    throw new NumberFormatException("Divisor Must Be Positive");
                }
            }

            //Do kernel calculations
            kernel[0] = Float.parseFloat(A.getText())/divisor;
            kernel[1] = Float.parseFloat(B.getText())/divisor;
            kernel[2] = Float.parseFloat(C.getText())/divisor;
            kernel[3] = Float.parseFloat(D.getText())/divisor;
            kernel[4] = Float.parseFloat(E.getText())/divisor;
            kernel[5] = Float.parseFloat(F.getText())/divisor;
            kernel[6] = Float.parseFloat(G.getText())/divisor;
            kernel[7] = Float.parseFloat(H.getText())/divisor;
            kernel[8] = Float.parseFloat(I.getText())/divisor;

            float sum = 0;
            for(float value: kernel){
                sum += value;
            }

            if(sum<=0){
                throw new NumberFormatException("The sum of the kernel components must be positive.");
            }

            for(int i = 0; i < kernel.length; i++){
                kernel[i]/=sum;
            }

            image = controller.getImage();
            if(image!=null){
                BufferedImageOp op = new ConvolveOp(new Kernel(3, 3, kernel));
                BufferedImage result = op.filter(image, null);
                controller.setImage(result);
            }

        }catch (NumberFormatException e){
            //Handle any exceptions, log them as well
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Kernel data is invalid. " + e.getLocalizedMessage());
            alert.showAndWait();
            ErrorLogger.Log(e.getMessage());
        }
    }

}
