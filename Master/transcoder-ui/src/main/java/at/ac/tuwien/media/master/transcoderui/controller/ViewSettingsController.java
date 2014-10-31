package at.ac.tuwien.media.master.transcoderui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javax.annotation.Nonnull;

import at.ac.tuwien.media.master.transcoderui.config.Configuration;
import at.ac.tuwien.media.master.transcoderui.config.Configuration.EField;
import at.ac.tuwien.media.master.transcoderui.model.TranscoderData;
import at.ac.tuwien.media.master.transcoderui.util.SceneUtils.EView;
import at.ac.tuwien.media.master.transcoderui.util.Utils;

public class ViewSettingsController implements Initializable {
    @FXML
    TextField usernameTextField;
    @FXML
    PasswordField passwordPasswordField;
    @FXML
    CheckBox passwordCheckBox;
    @FXML
    TextField urlTextField;
    @FXML
    ComboBox<String> languageComboBox;
    @FXML
    Button saveButton;

    private void _resetAllFields() {
	usernameTextField.setText(TranscoderData.getInstance().getUsername());
	final boolean bIsPasswordSave = Configuration.getAsBoolean(EField.IS_PASSWORD_SAVE);
	if (bIsPasswordSave)
	    passwordPasswordField.setText(TranscoderData.getInstance().getPassword());
	else
	    passwordPasswordField.setDisable(true);
	passwordCheckBox.setSelected(bIsPasswordSave);
	urlTextField.setText(TranscoderData.getInstance().getServerURL().toString());
	languageComboBox.getItems().addAll(Utils.SUPPORTED_LOCALES);
	languageComboBox.getSelectionModel().select(Utils.localeToString(TranscoderData.getInstance().getLocale()));
    }

    @Override
    public void initialize(@Nonnull final URL aLocation, @Nonnull final ResourceBundle aResourceBundle) {
	_resetAllFields();
    }

    @FXML
    protected void onClickBack(@Nonnull final ActionEvent aActionEvent) {
	ViewManager.getInstance().setView(EView.MAIN);
    }

    @FXML
    protected void onClickPasswordCheckBox(@Nonnull final ActionEvent aActionEvent) {
	final boolean bIsPasswordSave = passwordCheckBox.isSelected();

	Configuration.set(EField.IS_PASSWORD_SAVE, bIsPasswordSave);
	passwordPasswordField.setDisable(!bIsPasswordSave);
    }

    @FXML
    protected void onClickSave(@Nonnull final ActionEvent aActionEvent) {
	boolean bIsReady = true;

	// clear all textfields
	usernameTextField.getStyleClass().clear();
	passwordPasswordField.getStyleClass().clear();
	urlTextField.getStyleClass().clear();
	// set default styles
	usernameTextField.getStyleClass().addAll("text-input", "text-field");
	passwordPasswordField.getStyleClass().addAll("text-input", "text-field");
	urlTextField.getStyleClass().addAll("text-input", "text-field");

	// username
	if (!TranscoderData.getInstance().setUsername(usernameTextField.getText())) {
	    bIsReady = false;
	    usernameTextField.getStyleClass().add("text-field-error");
	}
	// password
	if (Configuration.getAsBoolean(EField.IS_PASSWORD_SAVE)) {
	    if (!TranscoderData.getInstance().setPassword(passwordPasswordField.getText())) {
		bIsReady = false;
		passwordPasswordField.getStyleClass().add("text-field-error");
	    }
	} else
	    TranscoderData.getInstance().setPassword(" ");
	// server url
	if (!TranscoderData.getInstance().setServerURL(urlTextField.getText())) {
	    bIsReady = false;
	    urlTextField.getStyleClass().add("text-field-error");
	}
	// locale
	TranscoderData.getInstance().setLocale(Utils.stringtoLocale(languageComboBox.getSelectionModel().getSelectedItem()));

	if (bIsReady)
	    ViewManager.getInstance().setView(EView.MAIN);
    }
}