package com.klugesoftware.farmainvoice.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FarmaInvoiceMain extends Application {

    private Logger logger = LogManager.getLogger(FarmaInvoiceMain.class.getName());

    @Override
    public void start(Stage primaryStage)  {
        try {
            BorderPane root = (BorderPane) FXMLLoader.load(getClass().getResource("/com/klugesoftware/farmainvoice/view/ElencoHeaderFatture.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setTitle("Farma Invoice");
            primaryStage.setScene(scene);
            primaryStage.show();
        }catch(Exception ex){
            logger.error(ex);
        }
    }

    @Override
    public void stop(){
        System.exit(1);
    }

    public static void main(String[] args){ Application.launch(args);}
}
