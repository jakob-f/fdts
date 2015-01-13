package at.frohnwieser.mahut.transcoderui.controller;

import java.net.MalformedURLException;
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
import javafx.scene.text.Text;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.transcoderui.config.Configuration;
import at.frohnwieser.mahut.transcoderui.config.Configuration.EField;
import at.frohnwieser.mahut.transcoderui.data.ClientData;
import at.frohnwieser.mahut.transcoderui.util.SceneUtils;
import at.frohnwieser.mahut.transcoderui.util.SceneUtils.EView;
import at.frohnwieser.mahut.transcoderui.util.Utils;

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
    @FXML
    Text statusText;

    private ResourceBundle m_aResourceBundle;

    private void _setStatusText(@Nullable final String sKey) {
	if (StringUtils.isNotEmpty(sKey))
	    statusText.setText(m_aResourceBundle.getString(sKey));
    }

    private void _resetAllFields() {
	usernameTextField.setText(ClientData.getInstance().getUsername());
	final boolean bIsPasswordSave = Configuration.getInstance().getAsBoolean(EField.IS_PASSWORD_SAVE);
	if (bIsPasswordSave)
	    passwordPasswordField.setText(ClientData.getInstance().getPassword());
	else
	    passwordPasswordField.setDisable(true);
	passwordCheckBox.setSelected(bIsPasswordSave);
	String sServerURL;
	try {
	    sServerURL = ClientData.getInstance().getServerURL().toString();
	} catch (final MalformedURLException aMalformedURLException) {
	    sServerURL = "";
	    _setStatusText("error.load.serverurl");
	}
	urlTextField.setText(sServerURL);

	languageComboBox.getItems().addAll(Utils.SUPPORTED_LOCALES);
	languageComboBox.getSelectionModel().select(Utils.localeToString(ClientData.getInstance().getLocale()));

	_setStatusText("text.about");
    }

    @Override
    public void initialize(@Nonnull final URL aLocation, @Nonnull final ResourceBundle aResourceBundle) {
	m_aResourceBundle = aResourceBundle;

	_resetAllFields();
    }

    @FXML
    protected void onClickBack(@Nonnull final ActionEvent aActionEvent) {
	ViewManager.getInstance().setView(EView.MAIN);
    }

    @FXML
    protected void onClickPasswordCheckBox(@Nonnull final ActionEvent aActionEvent) {
	final boolean bIsPasswordSave = passwordCheckBox.isSelected();

	Configuration.getInstance().set(EField.IS_PASSWORD_SAVE, bIsPasswordSave);
	passwordPasswordField.setDisable(!bIsPasswordSave);
    }

    @FXML
    protected void onClickSave(@Nonnull final ActionEvent aActionEvent) {
	_setStatusText("text.about");
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
	if (!ClientData.getInstance().setUsername(usernameTextField.getText())) {
	    bIsReady = false;
	    usernameTextField.getStyleClass().add("text-field-error");
	}
	// password
	if (Configuration.getInstance().getAsBoolean(EField.IS_PASSWORD_SAVE)) {
	    if (!ClientData.getInstance().setPassword(passwordPasswordField.getText())) {
		bIsReady = false;
		passwordPasswordField.getStyleClass().add("text-field-error");
	    }
	} else
	    ClientData.getInstance().setPassword(" ");
	// server url
	try {
	    if (!ClientData.getInstance().setServerURL(urlTextField.getText())) {
		bIsReady = false;
		urlTextField.getStyleClass().add("text-field-error");
	    }
	} catch (final MalformedURLException aMalformedURLException) {
	    bIsReady = false;
	    urlTextField.getStyleClass().add("text-field-error");
	    _setStatusText("error.save.serverurl");
	}
	// locale
	ClientData.getInstance().setLocale(Utils.stringtoLocale(languageComboBox.getSelectionModel().getSelectedItem()));

	if (bIsReady) {
	    ((ViewMainController) SceneUtils.getInstance().getController(EView.MAIN))._reset();
	    ViewManager.getInstance().setView(EView.MAIN);
	}
    }
}