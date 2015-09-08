package at.frohnwieser.mahut.mahutclient.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

import javax.annotation.Nonnull;

public class ViewProgressBarsController {
    @FXML
    VBox centerVBox;

    @FXML
    protected void onClickClose(@Nonnull final ActionEvent aActionEvent) {
	((Popup) centerVBox.getScene().getWindow()).hide();
    }

    public void add(@Nonnull final Node aNode) {
	centerVBox.getChildren().add(aNode);
    }

    public void clear() {
	centerVBox.getChildren().clear();
    }
}
