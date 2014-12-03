package at.ac.tuwien.media.master.transcoderui.controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
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

import at.ac.tuwien.media.master.commons.IOnCompleteCallback;
import at.ac.tuwien.media.master.commons.IdFactory;
import at.ac.tuwien.media.master.commons.TimeStampFactory;
import at.ac.tuwien.media.master.transcoderui.component.TextProgressBar;
import at.ac.tuwien.media.master.transcoderui.controller.ViewManager.EPosition;
import at.ac.tuwien.media.master.transcoderui.data.ClientData;
import at.ac.tuwien.media.master.transcoderui.io.AbstractNotifierThread;
import at.ac.tuwien.media.master.transcoderui.io.FileCopyProgressThread;
import at.ac.tuwien.media.master.transcoderui.io.TranscodeProgressThread;
import at.ac.tuwien.media.master.transcoderui.io.UploadProgressThread;
import at.ac.tuwien.media.master.transcoderui.util.SceneUtils;
import at.ac.tuwien.media.master.transcoderui.util.SceneUtils.EView;
import at.ac.tuwien.media.master.transcoderui.util.Utils;
import at.ac.tuwien.media.master.transcoderui.util.Value;
import at.ac.tuwien.media.master.webapp.FailedLoginException_Exception;
import at.ac.tuwien.media.master.webapp.SetData;
import at.ac.tuwien.media.master.wsclient.WSClient;

public class ViewMainController implements Initializable {
    public enum EFileListType {
	METACONTENTFILES,
	MATERIALS;
    }

    private static final EPosition POSITION_POPUP = EPosition.RIGHT;
    private static final EPosition POSITION_PROGRESSBARS = EPosition.BOTTOM;

    // TOP SECTION
    @FXML
    Text titleText;

    // LEFT COLUMN
    // select materials
    @FXML
    Button materialsSelectButton;
    @FXML
    HBox materialsDropZoneBox;
    @FXML
    Text materialsDropZoneText;
    // meta content
    @FXML
    HBox metaContentBox;
    @FXML
    Button metaContentButton;
    @FXML
    TextField metaContentTextField;
    @FXML
    TextArea metaContentTextArea;
    @FXML
    Button metaContentSelectButton;
    @FXML
    HBox metaContentDropZoneBox;
    @FXML
    Text metaContentDropZoneText;
    // copy path
    @FXML
    CheckBox copyCheckBox;
    @FXML
    TextField copyPathTextField;
    @FXML
    Button copyButton;
    // set
    @FXML
    CheckBox uploadCheckBox;
    @FXML
    ComboBox<String> uploadSetComboBox;
    // start
    @FXML
    Button startButton;

    // RIGHT COLUMN
    @FXML
    Text statusTextMaterials;
    @FXML
    Text statusTextMetaContent;
    @FXML
    Text statusTextCopy;
    @FXML
    Text statusTextUpload;
    @FXML
    Text statusTextStart;

    // BOTTOM STATUS TEXT
    @FXML
    Text statusText;

    private ResourceBundle m_aResourceBundle;
    private EFileListType m_aCurrentFileListType;
    private Collection<AbstractNotifierThread> m_aRunningThreads;

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

    private void _setStatusMarks() {
	_setStatusMark(statusTextMaterials, ClientData.getInstance().hasMaterials());
	_setStatusMark(statusTextMetaContent, ClientData.getInstance().hasMetaContentFiles());
	_setStatusMark(statusTextCopy, ClientData.getInstance().isSelectedAndReadyForCopy());
	_setStatusMark(statusTextUpload, ClientData.getInstance().isSelectedAndReadyForUploadAndHasSet());
	_setStatusMark(statusTextStart, ClientData.getInstance().isRunning());
    }

    private void _updateFields() {
	final boolean bIsRunning = ClientData.getInstance().isRunning();
	materialsSelectButton.setDisable(bIsRunning);
	metaContentSelectButton.setDisable(bIsRunning);
	metaContentTextField.setDisable(bIsRunning);
	metaContentTextArea.setDisable(bIsRunning);
	copyCheckBox.setDisable(bIsRunning || !ClientData.getInstance().isReadyForCopy());
	final boolean bIsSelectedAndReadyForCopy = ClientData.getInstance().isSelectedAndReadyForCopy();
	copyPathTextField.setDisable(bIsRunning || !bIsSelectedAndReadyForCopy);
	copyButton.setDisable(bIsRunning || !bIsSelectedAndReadyForCopy);
	uploadCheckBox.setDisable(bIsRunning || !ClientData.getInstance().isReadyForUpload());
	uploadSetComboBox.setDisable(bIsRunning || !ClientData.getInstance().isSelectedAndReadyForUpload());
	startButton.setDisable(!ClientData.getInstance().isReadyForStart());
	startButton.setText(bIsRunning ? m_aResourceBundle.getString("text.abort") : m_aResourceBundle.getString("text.start"));

	_setStatusMarks();
    }

    private void _updateMaterialsDropZone() {
	final int nUploadFilesSize = ClientData.getInstance().getMaterials().size();

	materialsDropZoneBox.getStyleClass().clear();
	materialsDropZoneBox.getStyleClass().add("dropzone");

	if (nUploadFilesSize == 0) {
	    materialsDropZoneBox.getStyleClass().add("bd-normal");
	    materialsDropZoneText.setText(m_aResourceBundle.getString("text.drop.files"));
	} else {
	    materialsDropZoneBox.getStyleClass().add("bd-success");
	    if (nUploadFilesSize == 1)
		materialsDropZoneText.setText(m_aResourceBundle.getString("text.added.materials.one"));
	    else
		materialsDropZoneText.setText(m_aResourceBundle.getString("text.added.materials").replace(Value.PLACEHOLDER, String.valueOf(nUploadFilesSize)));
	}

	_setStatusMarks();
    }

    private void _updateMetaContentDropZone() {
	final int nMetaContentFilesSize = ClientData.getInstance().getMetaContentFiles().size();

	metaContentDropZoneBox.getStyleClass().clear();
	metaContentDropZoneBox.getStyleClass().add("dropzone");

	if (nMetaContentFilesSize == 0) {
	    metaContentDropZoneBox.getStyleClass().add("bd-normal");
	    metaContentDropZoneText.setText(m_aResourceBundle.getString("text.drop.files"));
	} else {
	    metaContentDropZoneBox.getStyleClass().add("bd-success");
	    if (nMetaContentFilesSize == 1)
		metaContentDropZoneText.setText(m_aResourceBundle.getString("text.added.metacontent.file"));
	    else
		metaContentDropZoneText.setText(m_aResourceBundle.getString("text.added.metacontent.files").replace(Value.PLACEHOLDER,
		        String.valueOf(nMetaContentFilesSize)));
	}

	_setStatusMarks();
    }

    private boolean _updateFileListFiles() {
	Collection<File> aFiles = null;
	String sTitle = null;

	switch (m_aCurrentFileListType) {
	case METACONTENTFILES:
	    aFiles = ClientData.getInstance().getMetaContentFiles();
	    sTitle = m_aResourceBundle.getString("text.title.filelist.metacontent");

	    break;
	case MATERIALS:
	    aFiles = ClientData.getInstance().getMaterials();
	    sTitle = m_aResourceBundle.getString("text.title.filelist.materials");

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
	ClientData.getInstance().addMaterials(aFileList);

	_updateMaterialsDropZone();
	_updateFields();

	if (ViewManager.getInstance().isPopupShowing(POSITION_POPUP))
	    _updateFileListFiles();
    }

    private void _setMetaContentFiles(@Nonnull final List<File> aFileList) {
	ClientData.getInstance().addMetaContentFiles(aFileList);
	_updateMetaContentDropZone();

	if (ViewManager.getInstance().isPopupShowing(POSITION_POPUP))
	    _updateFileListFiles();

	_setStatusMarks();
    }

    private void _setCopyPath(@Nonnull final File aCopyFile) {
	final boolean bIsValidDirectory = ClientData.getInstance().setCopyDirectory(aCopyFile);

	if (bIsValidDirectory)
	    copyPathTextField.setText(aCopyFile.getPath());
	else
	    ; // TODO: ERROR

	_updateFields();
	_setStatusMarks();
    }

    private static String _toSring(@Nonnull final SetData aSetData) {
	if (aSetData != null)
	    return aSetData.getName() + " [" + String.valueOf(aSetData.getId()) + "]";

	return "";
    }

    private void _resetAllFields() {
	// title
	titleText.setText("@" + ClientData.getInstance().getUsername());

	// right column fields
	materialsDropZoneText.setText(m_aResourceBundle.getString("text.drop.files"));
	metaContentButton.setText(m_aResourceBundle.getString("button.more"));
	metaContentTextField.setText("upload " + TimeStampFactory.getAsString());
	metaContentTextArea.setText("@" + ClientData.getInstance().getUsername());
	metaContentDropZoneText.setText(m_aResourceBundle.getString("text.drop.files"));
	_setCopyPath(ClientData.getInstance().getCopyDirectory());

	final Collection<String> aSets = ClientData.getInstance().getSetDatas().stream().map(aSetData -> _toSring(aSetData))
	        .collect(Collectors.toCollection(TreeSet::new));
	uploadSetComboBox.getItems().addAll(aSets);
	uploadSetComboBox.getSelectionModel().select(_toSring(ClientData.getInstance().getSelectedSetData()));
	copyCheckBox.setSelected(ClientData.getInstance().isCopy());
	uploadCheckBox.setSelected(ClientData.getInstance().isUpload());

	// set fields disabled
	_updateFields();
	_setStatusMarks();
	_setStatusMark(statusTextStart, false);

    }

    @Override
    public void initialize(@Nonnull final URL aLocation, @Nonnull final ResourceBundle aResourceBundle) {
	m_aResourceBundle = aResourceBundle;

	// drop zone callbacks
	materialsDropZoneBox.setOnDragOver(aDragEvent -> {
	    final Dragboard aDragboard = aDragEvent.getDragboard();
	    if (aDragboard.hasFiles() && !ClientData.getInstance().isRunning())
		aDragEvent.acceptTransferModes(TransferMode.COPY);
	    else
		aDragEvent.consume();
	});
	materialsDropZoneBox.setOnDragDropped(aDragEvent -> {
	    final Dragboard aDragboard = aDragEvent.getDragboard();
	    if (aDragboard.hasFiles() && !ClientData.getInstance().isRunning()) {
		aDragEvent.setDropCompleted(true);
		_setUploadFiles(aDragboard.getFiles());
	    }

	    aDragEvent.consume();
	});

	metaContentDropZoneBox.setOnDragOver(aDragEvent -> {
	    final Dragboard aDragboard = aDragEvent.getDragboard();
	    if (aDragboard.hasFiles() && !ClientData.getInstance().isRunning())
		aDragEvent.acceptTransferModes(TransferMode.COPY);
	    else
		aDragEvent.consume();
	});
	metaContentDropZoneBox.setOnDragDropped(aDragEvent -> {
	    final Dragboard aDragboard = aDragEvent.getDragboard();
	    if (aDragboard.hasFiles() && !ClientData.getInstance().isRunning()) {
		aDragEvent.setDropCompleted(true);
		_setMetaContentFiles(aDragboard.getFiles());
	    }

	    aDragEvent.consume();
	});

	// set up File list
	final ViewFilelistController aController = (ViewFilelistController) SceneUtils.getInstance().getController(EView.FILELIST);
	aController.addOnRemoveCallback(nIndex -> _updateMaterialsDropZone());
	aController.addOnRemoveCallback(nIndex -> _updateMetaContentDropZone());
	aController.addOnRemoveCallback(nIndex -> _updateFields());
	aController.setInsertableCountText(m_aResourceBundle.getString("text.total.file.count"));

	m_aRunningThreads = new ArrayList<AbstractNotifierThread>();

	_resetAllFields();
    }

    private void _toggleMetaContentBox(final boolean bShowBox) {
	final Stage aPrimaryStage = (Stage) metaContentBox.getScene().getWindow();
	final double nCollapsibleHBoxHeight = bShowBox ? Value.METACONTENTBOX_HEIGHT : 0;

	metaContentButton.setText(bShowBox ? m_aResourceBundle.getString("button.less") : m_aResourceBundle.getString("button.more"));
	metaContentBox.setMaxHeight(nCollapsibleHBoxHeight);
	metaContentBox.setMinHeight(nCollapsibleHBoxHeight);
	metaContentBox.setVisible(bShowBox);
	aPrimaryStage.setMaxHeight(Value.WINDOW_HEIGHT_DEFAULT + nCollapsibleHBoxHeight);
	aPrimaryStage.setMinHeight(Value.WINDOW_HEIGHT_DEFAULT + nCollapsibleHBoxHeight);
	statusTextMetaContent.getParent().setStyle("-fx-padding: 50 0 " + nCollapsibleHBoxHeight + " 0");
    }

    @FXML
    protected void onClickSettings(@Nonnull final ActionEvent aActionEvent) {
	// reset window height...
	if (metaContentBox.isVisible())
	    _toggleMetaContentBox(false);
	// ... and all hide popups
	ViewManager.getInstance().hideAllPopups();

	// show settings
	ViewManager.getInstance().setView(EView.SETTINGS);
    }

    @FXML
    protected void onSelectMaterials(@Nonnull final ActionEvent aActionEvent) {
	if (!ClientData.getInstance().isRunning()) {
	    // show file chooser
	    final FileChooser aMultipleFileChooser = new FileChooser();
	    aMultipleFileChooser.setTitle(m_aResourceBundle.getString("text.select.materials"));
	    aMultipleFileChooser.setInitialDirectory(ClientData.getInstance().getMaterialsDirectory());

	    _setUploadFiles(aMultipleFileChooser.showOpenMultipleDialog(materialsDropZoneBox.getScene().getWindow()));
	}
    }

    @FXML
    protected void onClickMaterialsDropZone(@Nonnull final MouseEvent aMouseEvent) {
	if (ClientData.getInstance().getMaterials().isEmpty())
	    onSelectMaterials(null);
	else {
	    m_aCurrentFileListType = EFileListType.MATERIALS;
	    _toggleFileList();
	}
    }

    @FXML
    protected void onClickMetaContentBox(@Nonnull final ActionEvent aActionEvent) {
	_toggleMetaContentBox(!metaContentBox.isVisible());
    }

    @FXML
    protected void onClickMetaContentDropZone(@Nonnull final MouseEvent aMouseEvent) {
	if (ClientData.getInstance().getMetaContentFiles().isEmpty())
	    onSelectMetaContent(null);
	else {
	    m_aCurrentFileListType = EFileListType.METACONTENTFILES;
	    _toggleFileList();
	}
    }

    @FXML
    protected void onSelectMetaContent(@Nonnull final ActionEvent aActionEvent) {
	if (!ClientData.getInstance().isRunning()) {
	    final FileChooser aMultipleFileChooser = new FileChooser();
	    aMultipleFileChooser.setTitle(m_aResourceBundle.getString("text.select.metacontent.files"));
	    aMultipleFileChooser.setInitialDirectory(ClientData.getInstance().getMetaContentFilesDirectory());

	    _setMetaContentFiles(aMultipleFileChooser.showOpenMultipleDialog(metaContentDropZoneBox.getScene().getWindow()));
	}
    }

    @FXML
    protected void onEnterCopyPath(@Nonnull final ActionEvent aActionEvent) {
	final String sCopyFilePath = copyPathTextField.getText();

	_setCopyPath(StringUtils.isNotEmpty(sCopyFilePath) ? new File(sCopyFilePath) : null);
    }

    @FXML
    protected void onSelectCopyPath(@Nonnull final ActionEvent aActionEvent) {
	final DirectoryChooser aDirectoryChooser = new DirectoryChooser();
	aDirectoryChooser.setTitle(m_aResourceBundle.getString("text.select.metacontent.files"));
	aDirectoryChooser.setInitialDirectory(ClientData.getInstance().getCopyDirectory());

	_setCopyPath(aDirectoryChooser.showDialog(copyPathTextField.getScene().getWindow()));
    }

    @FXML
    protected void onSelectSet(@Nonnull final ActionEvent aActionEvent) {
	final String sSelectedSet = uploadSetComboBox.getSelectionModel().getSelectedItem();
	final String sSelectedSetId = sSelectedSet.replaceAll(".*\\[|\\].*", "");

	if (StringUtils.isNotEmpty(sSelectedSetId))
	    ClientData.getInstance().setSelectedSet(Long.parseLong(sSelectedSetId));

	_updateFields();
    }

    @FXML
    protected void onClickCopyCheckBox(@Nonnull final ActionEvent aActionEvent) {
	ClientData.getInstance().setIsCopy(copyCheckBox.isSelected());

	_updateFields();
    }

    @FXML
    protected void onClickUploadCheckBox(@Nonnull final ActionEvent aActionEvent) {
	ClientData.getInstance().setIsUpload(uploadCheckBox.isSelected());

	_updateFields();
    }

    private void _onCompleteThreads() {
	// clean up
	new Thread() {
	    @Override
	    public void run() {
		try {
		    Utils.cleanDirectory(Value.FILEPATH_TMP);
		    TimeUnit.SECONDS.sleep(3);
		} catch (final InterruptedException aInterruptedException) {
		    Thread.currentThread().interrupt();
		} finally {
		    Platform.runLater(() -> ViewManager.getInstance().hidePopup(POSITION_PROGRESSBARS));
		}
	    };
	}.start();

	ClientData.getInstance().setRunning(false);
	_updateFields();
    }

    private void _terminateAllThreads() {
	m_aRunningThreads.forEach(aThread -> aThread.terminate());

	_onCompleteThreads();
    }

    @FXML
    protected void onClickStart(@Nonnull final ActionEvent aActionEvent) {
	if (ClientData.getInstance().isRunning())
	    _terminateAllThreads();
	else if (ClientData.getInstance().isReadyForStart()) {
	    // set up threads
	    if (!m_aRunningThreads.isEmpty())
		m_aRunningThreads.clear();
	    // add on complete callback
	    final IOnCompleteCallback aOnCompleteCallback = () -> Platform.runLater(() -> _onCompleteThreads());

	    // get all materials to process
	    final Collection<File> aInFiles = ClientData.getInstance().getMaterials();
	    // show progress popup
	    ViewManager.getInstance().showPopup(EView.PROGRESSBARS, POSITION_PROGRESSBARS);
	    final ViewProgressBarsController aController = (ViewProgressBarsController) SceneUtils.getInstance().getController(EView.PROGRESSBARS);
	    aController.clear();

	    // copy file
	    if (ClientData.getInstance().isSelectedAndReadyForCopy()) {
		// copy thread
		final TextProgressBar aCopyProgressBar = new TextProgressBar();
		aCopyProgressBar.setCompletedText(m_aResourceBundle.getString("text.progress.copying.done"));
		aCopyProgressBar.setInsertableProgressText(m_aResourceBundle.getString("text.progress.copying"));
		aCopyProgressBar.setSize(410, 19);

		final AbstractNotifierThread aFileCopyThread = new FileCopyProgressThread(aInFiles, ClientData.getInstance().getCopyDirectory());
		aFileCopyThread.addCallback(aCopyProgressBar);
		if (!ClientData.getInstance().isSelectedAndReadyForUpload())
		    aFileCopyThread.addCallback(aOnCompleteCallback);
		aFileCopyThread.start();

		aController.add(aCopyProgressBar);
		m_aRunningThreads.add(aFileCopyThread);
	    }
	    // transcode file
	    if (ClientData.getInstance().isSelectedAndReadyForUploadAndHasSet()) {
		// create new set
		try {
		    final SetData aSetData = new SetData();
		    aSetData.setId(IdFactory.getInstance().getId());
		    aSetData.setName(metaContentTextField.getText());
		    aSetData.setMetaContent(metaContentTextArea.getText());
		    // timestamp will be overwritten by the server

		    if (WSClient.getInstance().createSet(ClientData.getInstance().getSelectedSetData().getId(), aSetData)) {
			// blocking queue
			final BlockingQueue<Object> aBlockingQueue = new LinkedBlockingDeque<Object>();

			// transcode thread
			final TextProgressBar aTranscodeProgressBar = new TextProgressBar();
			aTranscodeProgressBar.setCompletedText(m_aResourceBundle.getString("text.progress.transcoding.done"));
			aTranscodeProgressBar.setInsertableProgressText(m_aResourceBundle.getString("text.progress.transcoding"));
			aTranscodeProgressBar.setSize(410, 19);

			final AbstractNotifierThread aTranscodeThread = new TranscodeProgressThread(aInFiles, Utils.getDirectorySave(Value.FILEPATH_TMP));
			aTranscodeThread.addCallback(aTranscodeProgressBar);
			aTranscodeThread.setQueue(aBlockingQueue);
			aTranscodeThread.start();

			// upload thread
			final TextProgressBar aUploadProgressBar = new TextProgressBar();
			aUploadProgressBar.setCompletedText(m_aResourceBundle.getString("text.progress.uploading.done"));
			aUploadProgressBar.setInsertableProgressText(m_aResourceBundle.getString("text.progress.uploading"));
			aUploadProgressBar.setSize(410, 19);

			final AbstractNotifierThread aUploadThread = new UploadProgressThread(ClientData.getInstance().getMetaContentFiles());
			aUploadThread.addCallback(aUploadProgressBar);
			aUploadThread.addCallback(aOnCompleteCallback);
			aUploadThread.setQueue(aBlockingQueue);
			aUploadThread.start();

			aController.add(aTranscodeProgressBar);
			aController.add(aUploadProgressBar);
			m_aRunningThreads.add(aTranscodeThread);
			m_aRunningThreads.add(aUploadThread);
		    } else
			// TODO ERROR
			;
		} catch (final FailedLoginException_Exception e) {
		    // TODO ERROR
		}
	    }

	    ClientData.getInstance().setRunning(true);
	}

	_updateFields();
    }
}
