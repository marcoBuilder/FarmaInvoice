package com.klugesoftware.farmainvoice.main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InviaFattureMain extends Application {

    private Logger logger = LogManager.getLogger(InviaFattureMain.class.getName());

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        try {
            VBox root = (VBox) FXMLLoader.load(getClass().getResource("/com/klugesoftware/farmainvoice/view/InviaFatture.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setTitle("Farma Invoice");
            primaryStage.setScene(scene);
            primaryStage.setOnCloseRequest(event-> {
                Platform.exit();});
            primaryStage.show();
        }catch(Exception ex){
            logger.error(ex);
        }


    }


    @Override
    public void stop(){
        System.exit(0);
    }

}
