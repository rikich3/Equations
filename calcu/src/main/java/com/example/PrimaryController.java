package com.example;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
//import javafx.fxml.FXML;
import javafx.scene.text.Text;

//glass.setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 10;");

public class PrimaryController {
    @FXML StackPane frstPane;
    public static void allg(){
        //frstPane.
    }
    //frstPane.setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 10;");
    private String Listened;
    @FXML private TextField UserInput;
    @FXML Text warnings;
    @FXML
    private void switchToSecondary() throws IOException {
        if(Equation.onNewEcu(UserInput.getText()))
            App.setRoot("secondary");
        else{
            warnings.setText(Equation.getWarning());
        }
    }

    //mycode
    /*
    private void showsWindow() throws IOException {
        
    }

    private void SomethingCool() throws IOException {

    }
    */
}
