package com.example;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
//import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class PrimaryController {
    private String Listened;
    @FXML private TextField UserInput;
    
    @FXML
    private void switchToSecondary() throws IOException {
        if(Equation.onNewEcu(UserInput.getText()))
            App.setRoot("secondary");
    }

    //mycode
    /*
    private void showsWindow() throws IOException {
        
    }

    private void SomethingCool() throws IOException {

    }
    */
}
