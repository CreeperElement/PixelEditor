package fenske;

import javafx.embed.swing.SwingFXUtils;
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
                G, H, I;

    float[] kernel = new float[9];

    @FXML
    private void blur(){
        A.setText("0"); B.setText("1"); C.setText("0");
        D.setText("1"); E.setText("5"); F.setText("1");
        G.setText("0"); H.setText("1"); I.setText("0");
    }

    @FXML
    private void sharpen(){
        A.setText("0"); B.setText("-1"); C.setText("0");
        D.setText("-1"); E.setText("5"); F.setText("-1");
        G.setText("0"); H.setText("-1"); I.setText("0");
    }

    @FXML
    private void edge(){
        A.setText("0"); B.setText("-1"); C.setText("0");
        D.setText("-1"); E.setText("4"); F.setText("-1");
        G.setText("0"); H.setText("-1"); I.setText("0");
    }

    @FXML
    private void Apply(){
        try{
            kernel[0] = Float.parseFloat(A.getText());
            kernel[1] = Float.parseFloat(B.getText());
            kernel[2] = Float.parseFloat(C.getText());
            kernel[3] = Float.parseFloat(D.getText());
            kernel[4] = Float.parseFloat(E.getText());
            kernel[5] = Float.parseFloat(F.getText());
            kernel[6] = Float.parseFloat(G.getText());
            kernel[7] = Float.parseFloat(H.getText());
            kernel[8] = Float.parseFloat(I.getText());

            float sum = 0;
            for(float value: kernel){
                sum += value;
            }

            for(int i = 0; i < kernel.length; i++){
                kernel[i]/=sum;
            }

        }catch (NumberFormatException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Kernel data is invalid.");
            alert.showAndWait();
            ErrorLogger.Log(e.getMessage());
        }

        image = SwingFXUtils.fromFXImage(controller.getImage(), null);

        BufferedImageOp op = new ConvolveOp(new Kernel(3, 3, kernel));
        BufferedImage result = op.filter(image, null);
        controller.setImage(SwingFXUtils.toFXImage(result, null));
    }

}
