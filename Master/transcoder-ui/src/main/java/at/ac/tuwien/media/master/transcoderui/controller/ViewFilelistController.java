package at.ac.tuwien.media.master.transcoderui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

import javax.annotation.Nonnull;

import at.ac.tuwien.media.master.transcoderui.component.RemoveableCellListView;

public class ViewFilelistController implements Initializable {
    @FXML
    VBox centerVBox;

    @Override
    public void initialize(@Nonnull final URL aLocation, @Nonnull final ResourceBundle aResourceBundle) {
	final ObservableList<String> items = FXCollections.observableArrayList("Item 1", "Item 2", "Item 2", "Item 3", "Item 4", "Item 4");
	centerVBox.getChildren().addAll(new RemoveableCellListView(items));
    }

    @FXML
    protected void onClickClose(@Nonnull final ActionEvent aActionEvent) {
	((Popup) centerVBox.getScene().getWindow()).hide();
    }
}
