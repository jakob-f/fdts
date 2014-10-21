package at.ac.tuwien.media.master.transcoderui.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.transcoderui.config.Configuration;
import at.ac.tuwien.media.master.transcoderui.config.ConfigurationValue;
import at.ac.tuwien.media.master.transcoderui.util.SceneLoader;
import at.ac.tuwien.media.master.transcoderui.util.Value;
import at.ac.tuwien.media.master.wsclient.WSClient;

public class ViewSettingsController implements Initializable {
    private final static String LOCALE_NAME_ENGLISH = "English";
    private final static String LOCALE_NAME_GERMAN = "Deutsch";

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

    @Nonnull
    private static String _localeToString(@Nonnull final Locale aLocale) {
	String sLocale = LOCALE_NAME_ENGLISH;

	if (aLocale.equals(Locale.GERMAN))
	    sLocale = LOCALE_NAME_GERMAN;

	return sLocale;
    }

    @Nonnull
    private static Locale _stringtoLocale(@Nonnull final String sLocale) {
	Locale aLocale = Locale.ENGLISH;

	if (sLocale != null)
	    switch (sLocale) {
	    case LOCALE_NAME_GERMAN:
		aLocale = Locale.GERMAN;
		break;

	    case LOCALE_NAME_ENGLISH:
	    default:
		aLocale = Locale.ENGLISH;
	    }

	return aLocale;
    }

    private void _resetAllFields() {
	usernameTextField.setText(Configuration.getAsString(ConfigurationValue.USERNAME));
	passwordPasswordField.setText(Configuration.getAsString(ConfigurationValue.PASSWORD));
	passwordCheckBox.setSelected(Configuration.getAsBoolean(ConfigurationValue.IS_PASSWORD_SAVE));
	urlTextField.setText(Configuration.getAsString(ConfigurationValue.SERVER_URL));
	languageComboBox.getItems().addAll(LOCALE_NAME_ENGLISH, LOCALE_NAME_GERMAN);
	languageComboBox.getSelectionModel().select(_localeToString(new Locale(Configuration.getAsString(ConfigurationValue.LANGUAGE))));
    }

    @Override
    public void initialize(@Nonnull final URL location, @Nonnull final ResourceBundle aResourceBundle) {
	_resetAllFields();
    }

    private void _displayMainView() {
	final Scene aScene = SceneLoader.load(getClass().getClassLoader().getResource(Value.FXML_MAIN));

	if (aScene != null)
	    ((Stage) usernameTextField.getScene().getWindow()).setScene(aScene);
    }

    @FXML
    protected void onClickBack(@Nonnull final ActionEvent aActionEvent) {
	_displayMainView();
    }

    @FXML
    protected void onClickPasswordCheckBox(@Nonnull final ActionEvent aActionEvent) {
	Configuration.set(ConfigurationValue.IS_PASSWORD_SAVE, passwordCheckBox.isSelected());
    }

    @FXML
    protected void onClickSave(@Nonnull final ActionEvent aActionEvent) {
	boolean bIsReady = true;

	// username
	final String sUsername = usernameTextField.getText();
	if (StringUtils.isNotEmpty(sUsername)) {
	    Configuration.set(ConfigurationValue.USERNAME, sUsername);
	    WSClient.setUsername(sUsername);
	    usernameTextField.setId("textfield");
	} else {
	    bIsReady = false;
	    usernameTextField.setId("textfield-error");
	}

	// password
	if (Configuration.getAsBoolean(ConfigurationValue.IS_PASSWORD_SAVE)) {
	    final String sPassword = passwordPasswordField.getText();

	    if (StringUtils.isNotEmpty(sPassword)) {
		Configuration.set(ConfigurationValue.PASSWORD, sPassword);
		WSClient.setPassword(sPassword);
		passwordPasswordField.setId("textfield");
	    } else {
		bIsReady = false;
		passwordPasswordField.setId("textfield-error");
	    }
	} else {
	    Configuration.set(ConfigurationValue.PASSWORD, "");
	    passwordPasswordField.setId("textfield");
	}

	// server url
	final String sServerURL = urlTextField.getText();
	if (StringUtils.isNotEmpty(sServerURL)) {
	    // check if url is valid
	    try {
		final URL aWSURL = new URL(sServerURL);

		Configuration.set(ConfigurationValue.SERVER_URL, sServerURL);
		WSClient.setWSURL(aWSURL);
		urlTextField.setId("textfield");
	    } catch (final MalformedURLException e) {
		bIsReady = false;
		urlTextField.setId("textfield-error");
	    }
	} else {
	    bIsReady = false;
	    urlTextField.setId("textfield-error");
	}

	final String sSelectedLanguage = languageComboBox.getSelectionModel().getSelectedItem();
	if (sSelectedLanguage != null) {
	    final Locale aLocale = _stringtoLocale(sSelectedLanguage);
	    Configuration.set(ConfigurationValue.LANGUAGE, aLocale.getLanguage());
	}

	if (bIsReady)
	    _displayMainView();
    }
}