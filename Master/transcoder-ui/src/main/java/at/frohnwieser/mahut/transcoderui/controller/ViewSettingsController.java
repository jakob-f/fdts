package at.frohnwieser.mahut.transcoderui.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.transcoderui.data.ClientData;
import at.frohnwieser.mahut.transcoderui.util.LocaleUtils;
import at.frohnwieser.mahut.transcoderui.util.SceneUtils;
import at.frohnwieser.mahut.transcoderui.util.SceneUtils.EView;

public class ViewSettingsController implements Initializable {
    @FXML
    TextField usernameTextField;
    @FXML
    PasswordField passwordPasswordField;
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
	passwordPasswordField.setText(ClientData.getInstance().getPassword());
	String sServerURL;
	try {
	    sServerURL = ClientData.getInstance().getServerURL().toString();
	} catch (final MalformedURLException aMalformedURLException) {
	    sServerURL = "";
	    _setStatusText("error.load.serverurl");
	}
	urlTextField.setText(sServerURL);
	languageComboBox.getItems().addAll(LocaleUtils.SUPPORTED_LOCALES);
	languageComboBox.getSelectionModel().select(LocaleUtils.localeToString(ClientData.getInstance().getLocale()));

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
	if (!ClientData.getInstance().setPassword(passwordPasswordField.getText())) {
	    bIsReady = false;
	    passwordPasswordField.getStyleClass().add("text-field-error");
	}
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
	// locale (if changed - reload views)
	final Locale aNewLocale = LocaleUtils.stringtoLocale(languageComboBox.getSelectionModel().getSelectedItem());
	if (!ClientData.getInstance().getLocale().equals(aNewLocale)) {
	    ClientData.getInstance().setLocale(aNewLocale);
	    SceneUtils.getInstance().reload();
	}

	if (bIsReady) {
	    ((ViewMainController) SceneUtils.getInstance().getController(EView.MAIN))._reset();
	    ViewManager.getInstance().setView(EView.MAIN);
	}
    }
}