package com.klugesoftware.farmainvoice.controller;

import com.klugesoftware.farmainvoice.task.SincronizzaEdInviaFattureTask;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InviaFattureController implements Initializable {

    @FXML private ProgressIndicator progressIndicator;
    @FXML private ListView<String> listView;
    @FXML private CheckBox chkFatturePassive;
    @FXML private CheckBox chkFattureAttive;
    @FXML private CheckBox chkSyncFattureAttive;

    @FXML private Button btnInviaFatture;
    private final String PROPERTIES_FILE_NAME = "./resources/conf/config.properties";
    private Properties properties;
    private ObservableList<String> elencoMessaggi = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        listView.setItems(elencoMessaggi);
        try {
            properties = new Properties();
            properties.load(new FileInputStream(PROPERTIES_FILE_NAME));
            if(properties.getProperty("autoExecute").equals("true"))
                btnInviaFatture.fire();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }


    @FXML
    private void btnInviaFattureClicked(ActionEvent event){

        ExecutorService executor = Executors.newFixedThreadPool(1);
        SincronizzaEdInviaFattureTask task = new SincronizzaEdInviaFattureTask(chkFattureAttive.isSelected(),chkFatturePassive.isSelected(),chkSyncFattureAttive.isSelected());
        task.messageProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null){
                elencoMessaggi.add(task.getMessage());
                listView.scrollTo(listView.getItems().size());
            }
        });

        progressIndicator.setProgress(0);
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

        task.setOnSucceeded((succededEvent)->{
            progressIndicator.setProgress(1);
            if(properties.getProperty("autoExecute").equals("true"))
                Platform.exit();
        });

        executor.execute(task);
    }
}
