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
    public enum EFileListType {
	METADATA,
	VIDEOS;
    }

    private static final EPosition POSITION_POPUP = EPosition.RIGHT;
    private static final EPosition POSITION_PROGRESSBARS = EPosition.BOTTOM;

    // TOP SECTION
    @FXML
    Text titleText;

    // LEFT COLUMN
    // select video
    @FXML
    // TODO rename materials
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
    CheckBox copyCheckBox;
    @FXML
    TextField copyPathTextField;
    @FXML
    Button copyButton;
    // project
    @FXML
    CheckBox uploadCheckBox;
    @FXML
    ComboBox<String> projectComboBox;
    // start
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
    private EFileListType m_aCurrentFileListType;

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
	copyPathTextField.setDisable(!(TranscoderData.getInstance().isReadyForCopy() && copyCheckBox.isSelected()));
	copyButton.setDisable(!(TranscoderData.getInstance().isReadyForCopy() && copyCheckBox.isSelected()));
	uploadCheckBox.setDisable(!TranscoderData.getInstance().isReadyForUpload());
	projectComboBox.setDisable(!(TranscoderData.getInstance().isReadyForUpload() && uploadCheckBox.isSelected()));
	startButton.setDisable(!TranscoderData.getInstance().isReadyForStart());
    }

    private void _updateVideoDropZone() {
	final int nUploadFilesSize = TranscoderData.getInstance().getVideoFiles().size();

	videoDropZoneBox.getStyleClass().clear();
	videoDropZoneBox.getStyleClass().add("dropzone");

	if (nUploadFilesSize == 0) {
	    videoDropZoneBox.getStyleClass().add("bd-normal");
	    videoDropZoneText.setText(m_aResourceBundle.getString("text.drop.files"));
	    _setStatusMark(statusTextVideo, false);
	} else {
	    videoDropZoneBox.getStyleClass().add("bd-success");
	    if (nUploadFilesSize == 1)
		videoDropZoneText.setText(m_aResourceBundle.getString("text.added.video.file"));
	    else
		videoDropZoneText.setText(m_aResourceBundle.getString("text.added.video.files").replace(Value.PLACEHOLDER, String.valueOf(nUploadFilesSize)));
	    _setStatusMark(statusTextVideo, true);
	}
    }

    private void _updateMetadataDropZone() {
	final int nMetadataFilesSize = TranscoderData.getInstance().getMetadataFiles().size();

	metadataDropZoneBox.getStyleClass().clear();
	metadataDropZoneBox.getStyleClass().add("dropzone");

	if (nMetadataFilesSize == 0) {
	    metadataDropZoneBox.getStyleClass().add("bd-normal");
	    metadataDropZoneText.setText(m_aResourceBundle.getString("text.drop.files"));
	    _setStatusMark(statusTextMetadata, false);
	} else {
	    metadataDropZoneBox.getStyleClass().add("bd-success");
	    if (nMetadataFilesSize == 1)
		metadataDropZoneText.setText(m_aResourceBundle.getString("text.added.metadata.file"));
	    else
		metadataDropZoneText.setText(m_aResourceBundle.getString("text.added.metadata.files").replace(Value.PLACEHOLDER,
		        String.valueOf(nMetadataFilesSize)));
	    _setStatusMark(statusTextMetadata, true);
	}
    }

    private boolean _updateFileListFiles() {
	Collection<File> aFiles = null;
	String sTitle = null;

	switch (m_aCurrentFileListType) {
	case METADATA:
	    aFiles = TranscoderData.getInstance().getMetadataFiles();
	    sTitle = m_aResourceBundle.getString("text.title.filelist.metadata");

	    break;
	case VIDEOS:
	    aFiles = TranscoderData.getInstance().getVideoFiles();
	    sTitle = m_aResourceBundle.getString("text.title.filelist.video");

	    break;
	default:
	}

	if (CollectionUtils.isNotEmpty(aFiles)) {
	    final ViewFilelistController aController = (ViewFilelistController) SceneUtils.getInstance().getController(EView.FILELIST);
	    aController.setFiles(aFiles);
	    aController.setTitleText(sTitle);

	    return true;
	}

	return false;
    }

    private void _toggleFileList() {
	if (!ViewManager.getInstance().isPopupShowing(POSITION_POPUP)) {
	    if (_updateFileListFiles())
		ViewManager.getInstance().showPopup(EView.FILELIST, POSITION_POPUP);
	} else
	    ViewManager.getInstance().hidePopup(POSITION_POPUP);
    }

    private void _setUploadFiles(@Nonnull final List<File> aFileList) {
	TranscoderData.getInstance().addUploadFileList(aFileList);

	_updateVideoDropZone();
	_setFieldsDisabled();

	if (ViewManager.getInstance().isPopupShowing(POSITION_POPUP))
	    _updateFileListFiles();
    }

    private void _setMetadataFiles(@Nonnull final List<File> aFileList) {
	TranscoderData.getInstance().addMetadataFileList(aFileList);
	_updateMetadataDropZone();

	if (ViewManager.getInstance().isPopupShowing(POSITION_POPUP))
	    _updateFileListFiles();

	_setStatusMark(statusTextMetadata, true);
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

	// set up File list
	final ViewFilelistController aController = (ViewFilelistController) SceneUtils.getInstance().getController(EView.FILELIST);
	aController.addOnRemoveCallback(nIndex -> _updateVideoDropZone());
	aController.addOnRemoveCallback(nIndex -> _updateMetadataDropZone());
	aController.addOnRemoveCallback(nIndex -> _setFieldsDisabled());
	aController.setInsertableCountText(m_aResourceBundle.getString("text.total.file.count"));
    }

    private void _toggleMetadataBox(final boolean bShowBox) {
	final Stage aPrimaryStage = (Stage) metadataBox.getScene().getWindow();
	final double nCollapsibleHBoxHeight = bShowBox ? Value.METADATABOX_HEIGHT : 0;

	metadataButton.setText(bShowBox ? m_aResourceBundle.getString("button.less") : m_aResourceBundle.getString("button.more"));
	metadataBox.setMaxHeight(nCollapsibleHBoxHeight);
	metadataBox.setMinHeight(nCollapsibleHBoxHeight);
	metadataBox.setVisible(bShowBox);
	aPrimaryStage.setMaxHeight(Value.WINDOW_HEIGHT_DEFAULT + nCollapsibleHBoxHeight);
	aPrimaryStage.setMinHeight(Value.WINDOW_HEIGHT_DEFAULT + nCollapsibleHBoxHeight);
	statusTextMetadata.getParent().setStyle("-fx-padding: 50 0 " + nCollapsibleHBoxHeight + " 0");
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
	if (TranscoderData.getInstance().getVideoFiles().isEmpty())
	    onSelectVideos(null);
	else {
	    m_aCurrentFileListType = EFileListType.VIDEOS;
	    _toggleFileList();
	}
    }

    @FXML
    protected void onClickMetadataBox(@Nonnull final ActionEvent aActionEvent) {
	_toggleMetadataBox(!metadataBox.isVisible());
    }

    @FXML
    protected void onClickMetadataDropZone(@Nonnull final MouseEvent aMouseEvent) {
	if (TranscoderData.getInstance().getMetadataFiles().isEmpty())
	    onSelectMetadata(null);
	else {
	    m_aCurrentFileListType = EFileListType.METADATA;
	    _toggleFileList();
	}
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
	    final Collection<File> aInFiles = TranscoderData.getInstance().getVideoFiles();
	    final File aOutDirectory = TranscoderData.getInstance().getCopyDirectory();

	    // show progress popup
	    ViewManager.getInstance().showPopup(EView.PROGRESSBARS, POSITION_PROGRESSBARS);
	    final ViewProgressBarsController aController = (ViewProgressBarsController) SceneUtils.getInstance().getController(EView.PROGRESSBARS);
	    aController.clear();

	    // copy file
	    if (TranscoderData.getInstance().isSelectedAndReadyForCopy()) {
		final TextProgressBar aCopyProgressBar = new TextProgressBar();
		aCopyProgressBar.setCompletedText(m_aResourceBundle.getString("text.copying.done"));
		aCopyProgressBar.setInsertableProgressText(m_aResourceBundle.getString("text.copying"));
		aCopyProgressBar.setSize(410, 19);

		final AbstractNotifierThread aFileCopyThread = new FileCopyProgressThread(aInFiles, aOutDirectory);
		aFileCopyThread.addCallback(aCopyProgressBar);
		aFileCopyThread.start();

		aController.add(aCopyProgressBar);
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

		aController.add(aTranscodeProgressBar);
	    }

	    _setStatusMark(statusTextStart, true);
	}
    }
}
