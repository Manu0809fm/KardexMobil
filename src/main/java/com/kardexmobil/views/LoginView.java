
package com.kardexmobil.views;

import com.gluonhq.charm.glisten.mvc.View;
import java.io.IOException;
import javafx.fxml.FXMLLoader;

public class LoginView {
    public View getView() {
        try {
            View view = FXMLLoader.load(LoginView.class.getResource("login.fxml"));
            return view;
        } catch (IOException e) {
            System.out.println("IOException: " + e);
            return new View();
        }
    }
}
