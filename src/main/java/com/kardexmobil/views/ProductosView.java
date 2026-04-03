
package com.kardexmobil.views;

import com.gluonhq.charm.glisten.mvc.View;
import java.io.IOException;
import javafx.fxml.FXMLLoader;

public class ProductosView {
   public View getView() {
        try {
            View view = FXMLLoader.load(ProductosView.class.getResource("producto.fxml"));
            return view;
        } catch (IOException e) {
            System.out.println("IOException: " + e);
            return new View();
        }
    }
}