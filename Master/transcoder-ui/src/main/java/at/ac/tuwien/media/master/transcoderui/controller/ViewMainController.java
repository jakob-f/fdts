package at.ac.tuwien.media.master.transcoderui.controller;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.annotation.Nonnull;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.ffmpegwrapper.FFMPEGWrapper;
import at.ac.tuwien.media.master.ffmpegwrapper.util.FFMPEGUtils;
import at.ac.tuwien.media.master.transcoderui.component.TextProgressBar;
import at.ac.tuwien.media.master.transcoderui.config.Configuration;
import at.ac.tuwien.media.master.transcoderui.config.ConfigurationValue;
import at.ac.tuwien.media.master.transcoderui.controller.ViewManager.EPosition;
import at.ac.tuwien.media.master.transcoderui.controller.ViewManager.EView;
import at.ac.tuwien.media.master.transcoderui.io.AbstractNotifierThread;
import at.ac.tuwien.media.master.transcoderui.io.FileCopyProgressThread;
import at.ac.tuwien.media.master.transcoderui.io.TranscodeProgressThread;
import at.ac.tuwien.media.master.transcoderui.util.Value;
import at.ac.tuwien.media.master.webapp.FailedLoginException_Exception;
import at.ac.tuwien.media.master.wsclient.WSClient;

public class ViewMainController implements Initializable {
    // TOP SECTION
    @FXML
    Text titleText;

    // LEFT COLUMN
    // select video
    @FXML
    HBox videoDropZoneBox;
    @FXML
    Text videoDropZoneText;
    // metadata
    @FXML
    HBox metadataBox;
    @FXML
    Button metadataButton;
    @FXML
    HBox metadataDropZoneBox;
    @FXML
    Text metadataDropZoneText;
    // copy path
    @FXML
    TextField copyPathTextField;
    // project
    @FXML
    ComboBox<String> projectComboBox;
    // check boxes & start
    @FXML
    CheckBox copyCheckBox;
    @FXML
    CheckBox uploadCheckBox;
    @FXML
    Button startButton;

    // RIGHT COLUMN
    @FXML
    Text statusTextVideo;
    @FXML
    Text statusTextMetadata;
    @FXML
    Text statusTextCopy;
    @FXML
    Text statusTextProject;
    @FXML
    Text statusTextStart;

    // PROGRESS BARS
    @FXML
    VBox progressVBox;

    private ResourceBundle m_aResourceBundle;
    private File m_aVideoFile;
    private List<File> m_aMetadataFileList;

    private void _setStatusMark(@Nonnull final Text aStatusText, final boolean bIsSuccess) {
	aStatusText.setFont(new Font(29));

	aStatusText.getStyleClass().clear();
	if (bIsSuccess) {
	    aStatusText.getStyleClass().add("text-success");
	    aStatusText.setText(m_aResourceBundle.getString("symbol.checkmark"));
	} else {
	    aStatusText.getStyleClass().add("text-grey");
	    aStatusText.setText(m_aResourceBundle.getString("symbol.bullet"));
	}
    }

    private void _updateStartButton() {
	startButton.setDisable(m_aVideoFile == null || (!copyCheckBox.isSelected() && !uploadCheckBox.isSelected()));
    }

    private void _setVideoFile(@Nonnull final File... aFiles) {
	videoDropZoneBox.getStyleClass().clear();
	if (aFiles.length == 1 && aFiles[0] != null) {
	    m_aVideoFile = aFiles[0];
	    final String sInFileType = FilenameUtils.getExtension(m_aVideoFile.getName());

	    // save last shown folder
	    Configuration.set(ConfigurationValue.FILEPATH_VIDEO, m_aVideoFile.getParent());
	    _resetAllFields();

	    if (FFMPEGUtils.isFormatSupportedForDecoding(sInFileType)) {
		videoDropZoneBox.getStyleClass().addAll("dropzone", "bd-success");
		videoDropZoneText.setText(StringUtils.abbreviateMiddle(m_aVideoFile.getAbsolutePath(), "...", 50));

		_setStatusMark(statusTextVideo, true);

		return;
	    } else
		videoDropZoneText.setText(m_aResourceBundle.getString("text.file.format.not.supported").replace(Value.PLACEHOLDER, sInFileType));
	} else
	    videoDropZoneText.setText(m_aResourceBundle.getString("text.files.not.accepted"));

	m_aVideoFile = null;
	videoDropZoneBox.getStyleClass().addAll("dropzone", "bd-failure");

	_setStatusMark(statusTextVideo, false);
	_updateStartButton();
    }

    private void _setMetadataFiles(@Nonnull final List<File> aFileList) {
	m_aMetadataFileList = aFileList;
	metadataDropZoneBox.getStyleClass().clear();
	metadataDropZoneBox.getStyleClass().add("dropzone");

	if (m_aMetadataFileList != null && !m_aMetadataFileList.isEmpty()) {
	    metadataDropZoneBox.getStyleClass().add("bd-success");
	    if (m_aMetadataFileList.size() == 1)
		metadataDropZoneText.setText(m_aResourceBundle.getString("text.added.metadata.file"));
	    else
		metadataDropZoneText.setText(m_aResourceBundle.getString("text.added.metadata.files").replace(Value.PLACEHOLDER,
		        String.valueOf(m_aMetadataFileList.size())));

	    // save last shown folder
	    Configuration.set(ConfigurationValue.FILEPATH_METADATA, m_aMetadataFileList.get(0).getParent());
	} else
	    metadataDropZoneText.setText(m_aResourceBundle.getString("text.drop.files"));
    }

    private void _setCopyPath(@Nonnull final File aCopyFile) {
	boolean bIsValidDirectory = aCopyFile.isDirectory();

	if (bIsValidDirectory) {
	    final String sCopyFilePath = aCopyFile.getAbsolutePath();

	    copyPathTextField.setText(sCopyFilePath);
	    Configuration.set(ConfigurationValue.FILEPATH_COPY, sCopyFilePath);

	    bIsValidDirectory = !sCopyFilePath.isEmpty();
	}

	copyCheckBox.setDisable(!bIsValidDirectory);
	_setStatusMark(statusTextCopy, bIsValidDirectory);
    }

    private void _resetAllFields() {
	final String sUsername = Configuration.getAsString(ConfigurationValue.USERNAME);
	final String sPassword = Configuration.getAsString(ConfigurationValue.PASSWORD);
	final String sCopyFilePath = Configuration.getAsString(ConfigurationValue.FILEPATH_COPY);
	final String sServerURL = Configuration.getAsString(ConfigurationValue.SERVER_URL);
	URL aWSURL = null;
	String sHostname = "";
	if (StringUtils.isNotEmpty(sServerURL))
	    try {
		aWSURL = new URL(sServerURL);
		sHostname = " @ " + aWSURL.getHost();
	    } catch (final MalformedURLException aMalformedURLException) {
	    }

	titleText.setText(sUsername + sHostname);
	videoDropZoneText.setText(m_aResourceBundle.getString("text.drop.file"));
	metadataButton.setText(m_aResourceBundle.getString("button.more"));
	metadataDropZoneText.setText(m_aResourceBundle.getString("text.drop.files"));

	if (StringUtils.isNotEmpty(sCopyFilePath))
	    _setCopyPath(new File(sCopyFilePath));

	copyCheckBox.setSelected(Configuration.getAsBoolean(ConfigurationValue.IS_SELECTED_COPY));
	uploadCheckBox.setSelected(Configuration.getAsBoolean(ConfigurationValue.IS_SELECTED_UPLOAD));
	_updateStartButton();

	_setStatusMark(statusTextVideo, false);
	_setStatusMark(statusTextMetadata, false);
	_setStatusMark(statusTextStart, false);

	videoDropZoneBox.setOnDragOver(aDragEvent -> {
	    final Dragboard aDragboard = aDragEvent.getDragboard();
	    if (aDragboard.hasFiles())
		aDragEvent.acceptTransferModes(TransferMode.COPY);
	    else
		aDragEvent.consume();
	});
	videoDropZoneBox.setOnDragDropped(aDragEvent -> {
	    final Dragboard aDragboard = aDragEvent.getDragboard();
	    final boolean bSuccess = aDragboard.hasFiles();
	    if (bSuccess) {
		final List<File> aFileList = aDragboard.getFiles();
		_setVideoFile(aFileList.toArray(new File[aFileList.size()]));
	    }

	    aDragEvent.setDropCompleted(bSuccess);
	    aDragEvent.consume();
	});

	metadataDropZoneBox.setOnDragOver(aDragEvent -> {
	    final Dragboard aDragboard = aDragEvent.getDragboard();
	    if (aDragboard.hasFiles())
		aDragEvent.acceptTransferModes(TransferMode.COPY);
	    else
		aDragEvent.consume();
	});
	metadataDropZoneBox.setOnDragDropped(aDragEvent -> {
	    final Dragboard aDragboard = aDragEvent.getDragboard();
	    final boolean bSuccess = aDragboard.hasFiles();
	    if (bSuccess)
		_setMetadataFiles(aDragboard.getFiles());

	    aDragEvent.setDropCompleted(bSuccess);
	    aDragEvent.consume();
	});

	// WS Client
	if (StringUtils.isNotEmpty(sUsername))
	    WSClient.setUsername(sUsername);
	if (StringUtils.isNotEmpty(sPassword))
	    WSClient.setPassword(sPassword);
	if (aWSURL != null)
	    WSClient.setWSURL(aWSURL);

	boolean bHasProject = false;
	if (WSClient.isReady()) {
	    WSClient.createEndpoint();

	    // set Project
	    List<String> aProjectList = null;
	    try {
		aProjectList = WSClient.getProjects();
	    } catch (final FailedLoginException_Exception aFailedLoginException) {
		aFailedLoginException.printStackTrace();
	    }
	    if (CollectionUtils.isNotEmpty(aProjectList)) {
		projectComboBox.getItems().addAll(aProjectList);

		String sProject = null;
		if (aProjectList.size() == 1) {
		    sProject = aProjectList.get(0);
		    Configuration.set(ConfigurationValue.SELECTED_PROJECT, sProject);
		} else {
		    sProject = Configuration.getAsString(ConfigurationValue.SELECTED_PROJECT);
		    if (StringUtils.isEmpty(sProject) || !aProjectList.contains(sProject))
			sProject = null;
		}

		bHasProject = StringUtils.isNotEmpty(sProject);
		if (bHasProject)
		    projectComboBox.getSelectionModel().select(sProject);
	    }
	}

	_setStatusMark(statusTextProject, bHasProject);
    }

    @Override
    public void initialize(@Nonnull final URL location, @Nonnull final ResourceBundle aResourceBundle) {
	m_aResourceBundle = aResourceBundle;

	_resetAllFields();
    }

    private void _toggleMetadataBox(final boolean bShowBox) {
	final int nOffsetHeight = 200;
	final Stage aPrimaryStage = (Stage) metadataBox.getScene().getWindow();

	final double nCollapsibleHBoxHeight = bShowBox ? nOffsetHeight : 0;

	metadataButton.setText(bShowBox ? m_aResourceBundle.getString("button.less") : m_aResourceBundle.getString("button.more"));
	metadataBox.setMaxHeight(nCollapsibleHBoxHeight);
	metadataBox.setMinHeight(nCollapsibleHBoxHeight);
	metadataBox.setVisible(bShowBox);
	aPrimaryStage.setMaxHeight(Value.WINDOW_HEIGHT_DEFAULT + nCollapsibleHBoxHeight);
	aPrimaryStage.setMinHeight(Value.WINDOW_HEIGHT_DEFAULT + nCollapsibleHBoxHeight);
	statusTextMetadata.getParent().setStyle("-fx-padding: 50 0 " + (bShowBox ? nOffsetHeight : 0) + " 0");
    }

    @FXML
    protected void onClickSettings(@Nonnull final ActionEvent aActionEvent) {
	// reset window height and all hide popups
	if (metadataBox.isVisible())
	    _toggleMetadataBox(false);

	ViewManager.closePopup(EPosition.BOTTOM);
	ViewManager.closePopup(EPosition.LEFT);
	ViewManager.closePopup(EPosition.RIGHT);
	ViewManager.closePopup(EPosition.TOP);

	// show settings
	ViewManager.setView(EView.SETTINGS);
    }

    @Nonnull
    private File _getInitialDirectorySave(@Nonnull final String sFilePath) {
	final File aInitialDirectory = new File(sFilePath);

	return aInitialDirectory.exists() && aInitialDirectory.isDirectory() ? aInitialDirectory : new File(Value.DEFAULT_FILEPATH);
    }

    @FXML
    protected void onSelectVideos(@Nonnull final ActionEvent aActionEvent) {
	// show file chooser
	final FileChooser aFileChooser = new FileChooser();
	aFileChooser.setTitle(m_aResourceBundle.getString("text.select.video.file"));
	aFileChooser.setInitialDirectory(_getInitialDirectorySave(Configuration.getAsString(ConfigurationValue.FILEPATH_VIDEO)));
	final File aVideoFile = aFileChooser.showOpenDialog(videoDropZoneBox.getScene().getWindow());

	_setVideoFile(aVideoFile);
    }

    @FXML
    protected void onClickVideoDropZone(@Nonnull final MouseEvent aMouseEvent) {
	if (!ViewManager.isPopupShowing(EPosition.RIGHT))
	    ViewManager.showPopup(EView.FILELIST, EPosition.RIGHT);
	else
	    ViewManager.closePopup(EPosition.RIGHT);

	ViewManager.showPopup(EView.PROGRESSBARS, EPosition.BOTTOM);
    }

    @FXML
    protected void onClickMetadataBox(@Nonnull final ActionEvent aActionEvent) {
	_toggleMetadataBox(!metadataBox.isVisible());
    }

    @FXML
    protected void onSelectMetadata(@Nonnull final ActionEvent aActionEvent) {
	// show multiple file chooser
	final FileChooser aFileChooser = new FileChooser();
	aFileChooser.setTitle(m_aResourceBundle.getString("text.select.metadata.files"));
	aFileChooser.setInitialDirectory(_getInitialDirectorySave(Configuration.getAsString(ConfigurationValue.FILEPATH_METADATA)));
	_setMetadataFiles(aFileChooser.showOpenMultipleDialog(metadataDropZoneBox.getScene().getWindow()));
    }

    @FXML
    protected void onEnterCopyPath(@Nonnull final ActionEvent aActionEvent) {
	final String sCopyFilePath = copyPathTextField.getText();

	if (sCopyFilePath != null) {
	    _setCopyPath(new File(copyPathTextField.getText()));
	}
    }

    @FXML
    protected void onSelectCopyPath(@Nonnull final ActionEvent aActionEvent) {
	final DirectoryChooser aDirectoryChooser = new DirectoryChooser();
	aDirectoryChooser.setTitle(m_aResourceBundle.getString("text.select.metadata.files"));
	aDirectoryChooser.setInitialDirectory(_getInitialDirectorySave(Configuration.getAsString(ConfigurationValue.FILEPATH_COPY)));
	final File aInFile = aDirectoryChooser.showDialog(copyPathTextField.getScene().getWindow());

	if (aInFile != null)
	    _setCopyPath(aInFile);
    }

    @FXML
    protected void onSelectProject(@Nonnull final ActionEvent aActionEvent) {
	Configuration.set(ConfigurationValue.SELECTED_PROJECT, projectComboBox.getSelectionModel().getSelectedItem());
    }

    @FXML
    protected void onClickCopyCheckBox(@Nonnull final ActionEvent aActionEvent) {
	Configuration.set(ConfigurationValue.IS_SELECTED_COPY, copyCheckBox.isSelected());

	_updateStartButton();
    }

    @FXML
    protected void onClickUploadCheckBox(@Nonnull final ActionEvent aActionEvent) {
	Configuration.set(ConfigurationValue.IS_SELECTED_UPLOAD, uploadCheckBox.isSelected());

	_updateStartButton();
    }

    @FXML
    protected void onClickStart(@Nonnull final ActionEvent aActionEvent) {
	ViewManager.showPopup(EView.PROGRESSBARS, EPosition.BOTTOM);
	progressVBox.getChildren().clear();

	// copy file
	if (Configuration.getAsBoolean(ConfigurationValue.IS_SELECTED_COPY)) {
	    if (m_aVideoFile.isFile()) {
		final File aOutDirectory = new File(Configuration.getAsString(ConfigurationValue.FILEPATH_COPY));

		if (aOutDirectory.isDirectory()) {
		    final File aOutFile = new File(aOutDirectory.getAbsolutePath() + File.separator + m_aVideoFile.getName());
		    final TextProgressBar aCopyProgressBar = new TextProgressBar();
		    aCopyProgressBar.setCompletedText(m_aResourceBundle.getString("text.copying.done"));
		    aCopyProgressBar.setInsertableProgressText(m_aResourceBundle.getString("text.copying"));
		    aCopyProgressBar.setSize(410, 19);

		    final AbstractNotifierThread aFileCopyThread = new FileCopyProgressThread(m_aVideoFile, aOutFile);
		    aFileCopyThread.addCallback(aCopyProgressBar);
		    aFileCopyThread.start();
		    _setStatusMark(statusTextStart, true);

		    progressVBox.getChildren().addAll(aCopyProgressBar);
		}
	    }
	}
	// transcode file
	if (Configuration.getAsBoolean(ConfigurationValue.IS_SELECTED_UPLOAD)) {
	    final Process aTranscodeProcess = FFMPEGWrapper
		    .transcode(m_aVideoFile, new File("./" + FilenameUtils.getBaseName(m_aVideoFile.getName()) + ".avi"));

	    if (aTranscodeProcess != null) {
		final TextProgressBar aTranscodeProgressBar = new TextProgressBar();
		aTranscodeProgressBar.setCompletedText(m_aResourceBundle.getString("text.transcoding.done"));
		aTranscodeProgressBar.setInsertableProgressText(m_aResourceBundle.getString("text.transcoding"));
		aTranscodeProgressBar.setSize(410, 19);

		final AbstractNotifierThread aTranscodeThread = new TranscodeProgressThread(aTranscodeProcess);
		aTranscodeThread.addCallback(aTranscodeProgressBar);
		aTranscodeThread.start();
		_setStatusMark(statusTextStart, true);

		progressVBox.getChildren().addAll(aTranscodeProgressBar);
	    }
	}
    }
}
