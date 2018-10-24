package de.vanitasvitae.omemoqrgenerator;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;

public class LoginController {

    @FXML
    private JFXButton button_login;

    @FXML
    private JFXTextField text_username;

    @FXML
    private JFXPasswordField text_password;

    public void setLoginCallback(LoginCallback callback) {
        button_login.setOnAction(actionEvent -> {
            String username = text_username.getText();
            String password = text_password.getText();
            callback.login(username, password);
        });
    }

}
