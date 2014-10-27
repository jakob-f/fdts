package at.ac.tuwien.media.master.transcoderui.controller;

import java.io.File;
import java.net.URL;
import java.util.Collection;
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
import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.transcoderui.component.TextProgressBar;
import at.ac.tuwien.media.master.transcoderui.controller.ViewManager.EPosition;
import at.ac.tuwien.media.master.transcoderui.io.AbstractNotifierThread;
import at.ac.tuwien.media.master.transcoderui.io.FileCopyProgressThread;
import at.ac.tuwien.media.master.transcoderui.io.TranscodeProgressThread;
import at.ac.tuwien.media.master.transcoderui.model.TranscoderData;
import at.ac.tuwien.media.master.transcoderui.util.SceneUtils;
import at.ac.tuwien.media.master.transcoderui.util.SceneUtils.EView;
import at.ac.tuwien.media.master.transcoderui.util.Value;

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

    // BOTTOM STATUS TEXT
    @FXML
    Text statusText;

    private ResourceBundle m_aResourceBundle;

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

    private void _setFieldsDisabled() {
	projectComboBox.setDisable(CollectionUtils.isEmpty(TranscoderData.getInstance().getProjectList()));
	copyCheckBox.setDisable(!TranscoderData.getInstance().isReadyForCopy());
	uploadCheckBox.setDisable(!TranscoderData.getInstance().isReadyForUpload());
	startButton.setDisable(!TranscoderData.getInstance().isReadyForStart());
    }

    private void _setUploadFiles(@Nonnull final List<File> aFileList) {
	videoDropZoneBox.getStyleClass().clear();
	videoDropZoneBox.getStyleClass().add("dropzone");

	if (TranscoderData.getInstance().addUploadFileList(aFileList)) {
	    videoDropZoneBox.getStyleClass().add("bd-success");
	    _setStatusMark(statusTextVideo, true);

	    final Collection<File> aUploadFileCollection = TranscoderData.getInstance().getUploadFiles();
	    if (aUploadFileCollection.size() == 1)
		videoDropZoneText.setText(m_aResourceBundle.getString("text.added.video.file"));
	    else
		videoDropZoneText.setText(m_aResourceBundle.getString("text.added.video.files").replace(Value.PLACEHOLDER,
		        String.valueOf(aUploadFileCollection.size())));
	} else {
	    videoDropZoneBox.getStyleClass().add("bd-failure");

	    _setStatusMark(statusTextVideo, false);
	    videoDropZoneText.setText(m_aResourceBundle.getString("text.not.added.video.file"));
	}

	_setFieldsDisabled();
    }

    private void _setMetadataFiles(@Nonnull final List<File> aFileList) {
	metadataDropZoneBox.getStyleClass().clear();
	metadataDropZoneBox.getStyleClass().add("dropzone");

	if (TranscoderData.getInstance().addMetadataFileList(aFileList)) {
	    metadataDropZoneBox.getStyleClass().add("bd-success");

	    if (aFileList.size() == 1)
		metadataDropZoneText.setText(m_aResourceBundle.getString("text.added.metadata.file"));
	    else
		metadataDropZoneText.setText(m_aResourceBundle.getString("text.added.metadata.files").replace(Value.PLACEHOLDER,
		        String.valueOf(aFileList.size())));
	} else
	    metadataDropZoneText.setText(m_aResourceBundle.getString("text.drop.files"));
    }

    private void _setCopyPath(@Nonnull final File aCopyFile) {
	final boolean bIsValidDirectory = TranscoderData.getInstance().setCopyDirectory(aCopyFile);

	if (bIsValidDirectory)
	    copyPathTextField.setText(aCopyFile.getPath());
	else
	    ; // TODO: ERROR

	_setStatusMark(statusTextCopy, bIsValidDirectory);
	_setFieldsDisabled();
    }

    private void _resetAllFields() {
	// title
	String sTitle = TranscoderData.getInstance().getUsername();
	final URL aServerURL = TranscoderData.getInstance().getServerURL();
	if (aServerURL != null)
	    sTitle += " @ " + aServerURL.getHost();

	// right column fields
	titleText.setText(sTitle);
	videoDropZoneText.setText(m_aResourceBundle.getString("text.drop.files"));
	metadataButton.setText(m_aResourceBundle.getString("button.more"));
	metadataDropZoneText.setText(m_aResourceBundle.getString("text.drop.files"));
	_setCopyPath(TranscoderData.getInstance().getCopyDirectory());
	projectComboBox.getItems().addAll(TranscoderData.getInstance().getProjectList());
	projectComboBox.getSelectionModel().select(TranscoderData.getInstance().getSelectedProject());
	copyCheckBox.setSelected(TranscoderData.getInstance().isCopy());
	uploadCheckBox.setSelected(TranscoderData.getInstance().isUpload());

	// left column fields
	_setStatusMark(statusTextVideo, false);
	_setStatusMark(statusTextMetadata, false);
	_setStatusMark(statusTextProject, projectComboBox.getSelectionModel().getSelectedIndex() >= 0);
	_setStatusMark(statusTextStart, false);

	// set fields disabled
	_setFieldsDisabled();
    }

    @Override
    public void initialize(@Nonnull final URL aLocation, @Nonnull final ResourceBundle aResourceBundle) {
	m_aResourceBundle = aResourceBundle;

	_resetAllFields();

	// drop zone callbacks
	videoDropZoneBox.setOnDragOver(aDragEvent -> {
	    final Dragboard aDragboard = aDragEvent.getDragboard();
	    if (aDragboard.hasFiles())
		aDragEvent.acceptTransferModes(TransferMode.COPY);
	    else
		aDragEvent.consume();
	});
	videoDropZoneBox.setOnDragDropped(aDragEvent -> {
	    final Dragboard aDragboard = aDragEvent.getDragboard();
	    if (aDragboard.hasFiles()) {
		aDragEvent.setDropCompleted(true);
		_setUploadFiles(aDragboard.getFiles());
	    }

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
	    if (aDragboard.hasFiles()) {
		aDragEvent.setDropCompleted(true);
		_setMetadataFiles(aDragboard.getFiles());
	    }

	    aDragEvent.consume();
	});
    }

    private void _toggleFileList(@Nonnull final Collection<File> aFiles) {
	if (!ViewManager.getInstance().isPopupShowing(EPosition.RIGHT) && CollectionUtils.isNotEmpty(aFiles)) {
	    final ViewFilelistController aController = (ViewFilelistController) SceneUtils.getInstance().getController(EView.FILELIST);
	    aController.setFiles(aFiles);
	    ViewManager.getInstance().showPopup(EView.FILELIST, EPosition.RIGHT);
	} else
	    ViewManager.getInstance().hidePopup(EPosition.RIGHT);
    }

    private void _toggleMetadataBox(final boolean bShowBox) {
	final double nOffsetHeight = Value.METADATABOX_HEIGHT;
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
	// reset window height...
	if (metadataBox.isVisible())
	    _toggleMetadataBox(false);
	// ... and all hide popups
	ViewManager.getInstance().hideAllPopups();

	// show settings
	ViewManager.getInstance().setView(EView.SETTINGS);
    }

    @FXML
    protected void onSelectVideos(@Nonnull final ActionEvent aActionEvent) {
	// show file chooser
	final FileChooser aMultipleFileChooser = new FileChooser();
	aMultipleFileChooser.setTitle(m_aResourceBundle.getString("text.select.video.files"));
	aMultipleFileChooser.setInitialDirectory(TranscoderData.getInstance().getUploadDirectory());

	_setUploadFiles(aMultipleFileChooser.showOpenMultipleDialog(videoDropZoneBox.getScene().getWindow()));
    }

    @FXML
    protected void onClickVideoDropZone(@Nonnull final MouseEvent aMouseEvent) {
	_toggleFileList(TranscoderData.getInstance().getUploadFiles());
    }

    @FXML
    protected void onClickMetadataBox(@Nonnull final ActionEvent aActionEvent) {
	_toggleMetadataBox(!metadataBox.isVisible());
    }

    @FXML
    protected void onClickMetadataDropZone(@Nonnull final MouseEvent aMouseEvent) {
	_toggleFileList(TranscoderData.getInstance().getMetadataFiles());
    }

    @FXML
    protected void onSelectMetadata(@Nonnull final ActionEvent aActionEvent) {
	final FileChooser aMultipleFileChooser = new FileChooser();
	aMultipleFileChooser.setTitle(m_aResourceBundle.getString("text.select.metadata.files"));
	aMultipleFileChooser.setInitialDirectory(TranscoderData.getInstance().getMetadataDirectory());

	_setMetadataFiles(aMultipleFileChooser.showOpenMultipleDialog(metadataDropZoneBox.getScene().getWindow()));
    }

    @FXML
    protected void onEnterCopyPath(@Nonnull final ActionEvent aActionEvent) {
	final String sCopyFilePath = copyPathTextField.getText();

	_setCopyPath(StringUtils.isNotEmpty(sCopyFilePath) ? new File(sCopyFilePath) : null);
    }

    @FXML
    protected void onSelectCopyPath(@Nonnull final ActionEvent aActionEvent) {
	final DirectoryChooser aDirectoryChooser = new DirectoryChooser();
	aDirectoryChooser.setTitle(m_aResourceBundle.getString("text.select.metadata.files"));
	aDirectoryChooser.setInitialDirectory(TranscoderData.getInstance().getCopyDirectory());

	_setCopyPath(aDirectoryChooser.showDialog(copyPathTextField.getScene().getWindow()));
    }

    @FXML
    protected void onSelectProject(@Nonnull final ActionEvent aActionEvent) {
	TranscoderData.getInstance().setSelectedProject(projectComboBox.getSelectionModel().getSelectedItem());
    }

    @FXML
    protected void onClickCopyCheckBox(@Nonnull final ActionEvent aActionEvent) {
	TranscoderData.getInstance().setIsCopy(copyCheckBox.isSelected());

	_setFieldsDisabled();
    }

    @FXML
    protected void onClickUploadCheckBox(@Nonnull final ActionEvent aActionEvent) {
	TranscoderData.getInstance().setIsUpload(uploadCheckBox.isSelected());

	_setFieldsDisabled();
    }

    @FXML
    protected void onClickStart(@Nonnull final ActionEvent aActionEvent) {
	if (TranscoderData.getInstance().isReadyForStart()) {
	    final Collection<File> aInFiles = TranscoderData.getInstance().getUploadFiles();
	    final File aOutDirectory = TranscoderData.getInstance().getCopyDirectory();

	    // show progress popup
	    ViewManager.getInstance().showPopup(EView.PROGRESSBARS, EPosition.BOTTOM);
	    final VBox aProgressBarVBox = ((ViewProgressBarsController) SceneUtils.getInstance().getController(EView.PROGRESSBARS)).centerVBox;
	    aProgressBarVBox.getChildren().clear();

	    // copy file
	    if (TranscoderData.getInstance().isSelectedAndReadyForCopy()) {
		final TextProgressBar aCopyProgressBar = new TextProgressBar();
		aCopyProgressBar.setCompletedText(m_aResourceBundle.getString("text.copying.done"));
		aCopyProgressBar.setInsertableProgressText(m_aResourceBundle.getString("text.copying"));
		aCopyProgressBar.setSize(410, 19);

		final AbstractNotifierThread aFileCopyThread = new FileCopyProgressThread(aInFiles, aOutDirectory);
		aFileCopyThread.addCallback(aCopyProgressBar);
		aFileCopyThread.start();

		aProgressBarVBox.getChildren().addAll(aCopyProgressBar);
	    }
	    // transcode file
	    if (TranscoderData.getInstance().isSelectedAndReadyForUpload()) {
		final TextProgressBar aTranscodeProgressBar = new TextProgressBar();
		aTranscodeProgressBar.setCompletedText(m_aResourceBundle.getString("text.transcoding.done"));
		aTranscodeProgressBar.setInsertableProgressText(m_aResourceBundle.getString("text.transcoding"));
		aTranscodeProgressBar.setSize(410, 19);

		final AbstractNotifierThread aTranscodeThread = new TranscodeProgressThread(aInFiles, aOutDirectory);
		aTranscodeThread.addCallback(aTranscodeProgressBar);
		aTranscodeThread.start();

		aProgressBarVBox.getChildren().addAll(aTranscodeProgressBar);
	    }

	    _setStatusMark(statusTextStart, true);
	}
    }
}
