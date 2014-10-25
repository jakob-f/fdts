package at.ac.tuwien.media.master.transcoderui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
}
