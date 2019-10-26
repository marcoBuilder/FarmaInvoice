package com.klugesoftware.farmainvoice.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import java.util.function.Function;

public class ActionButtonTableCell<S> extends TableCell<S, Button> {

    private final Button actionButton;
    private final ImageView iconButton;

    public ActionButtonTableCell(String label, Function< S, S> function) {
        this.getStyleClass().add("action-button-table-cell");
        iconButton = new ImageView("/com/klugesoftware/farmainvoice/img/icons8-pdf.png");
        this.actionButton = new Button(label);
        this.actionButton.setGraphic(iconButton);
        this.actionButton.setOnAction((ActionEvent e) -> {
            function.apply(getCurrentItem());
        });
        //this.actionButton.setMaxWidth(Double.MAX_VALUE);

    }

    public S getCurrentItem() {
        return (S) getTableView().getItems().get(getIndex());
    }

    public static <S> Callback<TableColumn<S, Button>, TableCell<S, Button>> forTableColumn(String label, Function< S, S> function) {
        return param -> new ActionButtonTableCell<>(label, function);
    }

    @Override
    public void updateItem(Button item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(actionButton);
        }
    }
}

