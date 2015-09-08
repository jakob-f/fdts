package at.frohnwieser.mahut.transcoderui.controller;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.ws.WebServiceException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.commons.CommonValue;
import at.frohnwieser.mahut.commons.FileUtils;
import at.frohnwieser.mahut.commons.IOnCompleteCallback;
import at.frohnwieser.mahut.commons.IOnCompleteFileCallback;
import at.frohnwieser.mahut.commons.IdFactory;
import at.frohnwieser.mahut.commons.TimeStampFactory;
import at.frohnwieser.mahut.transcoderui.component.TextProgressBar;
import at.frohnwieser.mahut.transcoderui.controller.ViewManager.EPosition;
import at.frohnwieser.mahut.transcoderui.data.AssetDataWrapper;
import at.frohnwieser.mahut.transcoderui.data.ClientData;
import at.frohnwieser.mahut.transcoderui.io.AbstractNotifierThread;
import at.frohnwieser.mahut.transcoderui.io.FileCopyProgressThread;
import at.frohnwieser.mahut.transcoderui.io.TranscodeProgressThread;
import at.frohnwieser.mahut.transcoderui.io.UploadProgressThread;
import at.frohnwieser.mahut.transcoderui.util.SceneUtils;
import at.frohnwieser.mahut.transcoderui.util.SceneUtils.EView;
import at.frohnwieser.mahut.transcoderui.util.Value;
import at.frohnwieser.mahut.webapp.FailedLoginException_Exception;
import at.frohnwieser.mahut.webapp.SetData;
import at.frohnwieser.mahut.wsclient.WSClient;

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
    HBox materialsDropZoneHBox;
    @FXML
    Text materialsDropZoneText;
    // meta content
    @FXML
    HBox metaContentHBox;
    @FXML
    Button metaContentButton;
    @FXML
    TextField metaContentTextField;
    @FXML
    HBox metaContentTextHBox;
    @FXML
    TextArea metaContentTextArea;
    @FXML
    Text metaContentWordCountText;
    @FXML
    Button metaContentSelectButton;
    @FXML
    HBox metaContentDropZoneHBox;
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
    HBox bottomHBox;

    private ResourceBundle m_aResourceBundle;
    private EFileListType m_aCurrentFileListType;
    private Collection<AbstractNotifierThread> m_aRunningThreads;
    private int m_nOverallProcessCount;

    private void _setStatusText(@Nullable final String sText) {
	bottomHBox.getChildren().clear();
	bottomHBox.getStyleClass().clear();
	bottomHBox.getStyleClass().add("bottomHBox");
	bottomHBox.setOnMouseClicked(null);
	if (StringUtils.isNotEmpty(sText)) {
	    bottomHBox.setAlignment(Pos.CENTER);
	    bottomHBox.getChildren().add(new Text(sText));
	}
    }

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

    private void _update() {
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

	materialsDropZoneHBox.getStyleClass().clear();
	materialsDropZoneHBox.getStyleClass().add("dropzone");

	if (nUploadFilesSize == 0) {
	    materialsDropZoneHBox.getStyleClass().add("bd-normal");
	    materialsDropZoneText.setText(m_aResourceBundle.getString("text.drop.files"));
	} else {
	    materialsDropZoneHBox.getStyleClass().add("bd-success");
	    if (nUploadFilesSize == 1)
		materialsDropZoneText.setText(m_aResourceBundle.getString("text.added.materials.one"));
	    else
		materialsDropZoneText.setText(m_aResourceBundle.getString("text.added.materials").replace(CommonValue.PLACEHOLDER,
		        String.valueOf(nUploadFilesSize)));
	}

	_setStatusMarks();
    }

    private void _updateMetaContentDropZone() {
	final int nMetaContentFilesSize = ClientData.getInstance().getMetaContentFiles().size();

	metaContentDropZoneHBox.getStyleClass().clear();
	metaContentDropZoneHBox.getStyleClass().add("dropzone");

	if (nMetaContentFilesSize == 0) {
	    metaContentDropZoneHBox.getStyleClass().add("bd-normal");
	    metaContentDropZoneText.setText(m_aResourceBundle.getString("text.drop.files"));
	} else {
	    metaContentDropZoneHBox.getStyleClass().add("bd-success");
	    if (nMetaContentFilesSize == 1)
		metaContentDropZoneText.setText(m_aResourceBundle.getString("text.added.metacontent.file"));
	    else
		metaContentDropZoneText.setText(m_aResourceBundle.getString("text.added.metacontent.files").replace(CommonValue.PLACEHOLDER,
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
	_update();

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

    private void _setCopyPath(@Nullable final File aCopyFile) {
	final boolean bIsValidDirectory = ClientData.getInstance().setCopyDirectory(aCopyFile);

	if (bIsValidDirectory)
	    copyPathTextField.setText(aCopyFile.getPath());
	else
	    ; // TODO: ERROR

	_update();
	_setStatusMarks();
    }

    protected void _reset() {
	_setStatusText(m_aResourceBundle.getString("text.about"));
	try {
	    ClientData.getInstance().reloadWSData();
	} catch (final FailedLoginException_Exception aFailedLoginException) {
	    _setStatusText(m_aResourceBundle.getString("error.login.failed"));
	} catch (final MalformedURLException aMalformedURLException) {
	    _setStatusText(m_aResourceBundle.getString("error.save.serverurl"));
	} catch (final WebServiceException aWSException) {
	    _setStatusText(m_aResourceBundle.getString("error.ws.access"));
	} catch (final IllegalStateException aIllegalStateException) {
	    _setStatusText(m_aResourceBundle.getString("error.ws.mssing"));
	}

	// title
	titleText.setText("@" + ClientData.getInstance().getUsername());

	// right column fields
	_updateMaterialsDropZone();
	metaContentButton.setText(m_aResourceBundle.getString("button.more"));
	metaContentTextField.setText("upload " + TimeStampFactory.nowFormatted());
	_updateMetaContentDropZone();
	_setCopyPath(ClientData.getInstance().getCopyDirectory());

	final Collection<String> aSets = ClientData.getInstance().getSetDatas().stream().filter(o -> o != null).map(aSetData -> aSetData.getName())
	        .collect(Collectors.toCollection(TreeSet::new));
	uploadSetComboBox.getItems().clear();
	uploadSetComboBox.getItems().addAll(aSets);
	final SetData aSelectedSet = ClientData.getInstance().getSelectedSetData();
	uploadSetComboBox.getSelectionModel().select(aSelectedSet != null ? aSelectedSet.getName() : "");
	copyCheckBox.setSelected(ClientData.getInstance().isCopy());
	uploadCheckBox.setSelected(ClientData.getInstance().isUpload());

	// set fields disabled
	_update();
	_setStatusMarks();
	_setStatusMark(statusTextStart, false);

	// format meta content text area
	_onKeyTypedMetaContentTextArea(null);
    }

    private void _onKeyTypedMetaContentTextArea(@Nullable final KeyEvent aEvent) {
	String sMetaContentText = metaContentTextArea.getText();
	final int nMetaContentTextLength = sMetaContentText.length();

	if (nMetaContentTextLength >= CommonValue.MAX_LENGTH_METACONTENT)
	    aEvent.consume();
	else if (sMetaContentText.contains("\n") || sMetaContentText.contains("  ")) {
	    final int nCaretPosition = metaContentTextArea.getCaretPosition();
	    sMetaContentText = sMetaContentText.replaceAll("\n", "");
	    sMetaContentText = sMetaContentText.replaceAll("  ", " ");
	    metaContentTextArea.setText(sMetaContentText);
	    metaContentTextArea.positionCaret(nCaretPosition - 1);
	} else {
	    final TextFlow aTextFlow = new TextFlow();
	    if (metaContentTextArea.lookup(".scroll-bar:vertical") != null)
		aTextFlow.setMaxWidth(400);

	    final StringTokenizer aTokenizer = new StringTokenizer(sMetaContentText);

	    while (aTokenizer.hasMoreTokens()) {
		final String sToken = aTokenizer.nextToken();
		final Label aTextLabel = new Label(sToken);
		aTextLabel.setTextFill(Color.TRANSPARENT);

		if (1 < sToken.length() && sToken.length() <= 55
		        && (sToken.startsWith(CommonValue.CHARACTER_AT) || sToken.startsWith(CommonValue.CHARACTER_HASH)))
		    aTextLabel.getStyleClass().add("text-highlight");

		aTextFlow.getChildren().addAll(aTextLabel, new Text(" "));
	    }

	    metaContentTextHBox.getChildren().clear();
	    metaContentTextHBox.getChildren().add(aTextFlow);
	}

	metaContentWordCountText.setText("(" + (CommonValue.MAX_LENGTH_METACONTENT - nMetaContentTextLength) + ")");
    }

    private void _onDragOverDropZone(@Nonnull final DragEvent aDragEvent) {
	final Dragboard aDragboard = aDragEvent.getDragboard();
	if (aDragboard.hasFiles() && !ClientData.getInstance().isRunning())
	    aDragEvent.acceptTransferModes(TransferMode.COPY);
	else
	    aDragEvent.consume();
    }

    @Override
    public void initialize(@Nonnull final URL aLocation, @Nonnull final ResourceBundle aResourceBundle) {
	m_aResourceBundle = aResourceBundle;

	// drop zone callbacks
	materialsDropZoneHBox.setOnDragOver(aDragEvent -> _onDragOverDropZone(aDragEvent));
	materialsDropZoneHBox.setOnDragDropped(aDragEvent -> {
	    final Dragboard aDragboard = aDragEvent.getDragboard();
	    if (aDragboard.hasFiles() && !ClientData.getInstance().isRunning()) {
		aDragEvent.setDropCompleted(true);
		_setUploadFiles(aDragboard.getFiles());
	    }

	    aDragEvent.consume();
	});

	metaContentTextField.addEventFilter(KeyEvent.KEY_TYPED, aEvent -> {
	    if (metaContentTextField.getText().length() >= CommonValue.MAX_LENGTH_NAME)
		aEvent.consume();
	});

	metaContentTextArea.addEventFilter(KeyEvent.KEY_TYPED, aEvent -> _onKeyTypedMetaContentTextArea(aEvent));

	metaContentDropZoneHBox.setOnDragOver(aDragEvent -> _onDragOverDropZone(aDragEvent));
	metaContentDropZoneHBox.setOnDragDropped(aDragEvent -> {
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
	aController.addOnRemoveCallback(nIndex -> _update());
	aController.setInsertableCountText(m_aResourceBundle.getString("text.total.file.count"));

	m_aRunningThreads = new ArrayList<AbstractNotifierThread>();

	_reset();
    }

    private void _toggleMetaContentBox(final boolean bShowBox) {
	final Stage aPrimaryStage = (Stage) metaContentHBox.getScene().getWindow();
	final double nCollapsibleHBoxHeight = bShowBox ? Value.METACONTENTBOX_HEIGHT : 0;

	metaContentButton.setText(bShowBox ? m_aResourceBundle.getString("button.less") : m_aResourceBundle.getString("button.more"));
	metaContentHBox.setMaxHeight(nCollapsibleHBoxHeight);
	metaContentHBox.setMinHeight(nCollapsibleHBoxHeight);
	metaContentHBox.setVisible(bShowBox);
	aPrimaryStage.setMaxHeight(Value.WINDOW_HEIGHT_DEFAULT + nCollapsibleHBoxHeight);
	aPrimaryStage.setMinHeight(Value.WINDOW_HEIGHT_DEFAULT + nCollapsibleHBoxHeight);
	statusTextMetaContent.getParent().setStyle("-fx-padding: 50 0 " + nCollapsibleHBoxHeight + " 0");
    }

    @FXML
    protected void onClickSettings(@Nullable final ActionEvent aActionEvent) {
	// reset window height...
	if (metaContentHBox.isVisible())
	    _toggleMetaContentBox(false);
	// ... and all hide popups
	ViewManager.getInstance().hideAllPopups();

	// show settings
	ViewManager.getInstance().setView(EView.SETTINGS);
    }

    @FXML
    protected void onSelectMaterials(@Nullable final ActionEvent aActionEvent) {
	if (!ClientData.getInstance().isRunning()) {
	    // show file chooser
	    final FileChooser aMultipleFileChooser = new FileChooser();
	    aMultipleFileChooser.setTitle(m_aResourceBundle.getString("text.select.materials"));
	    aMultipleFileChooser.setInitialDirectory(ClientData.getInstance().getMaterialsDirectory());

	    _setUploadFiles(aMultipleFileChooser.showOpenMultipleDialog(materialsDropZoneHBox.getScene().getWindow()));
	}
    }

    @FXML
    protected void onClickMaterialsDropZone(@Nullable final MouseEvent aMouseEvent) {
	if (ClientData.getInstance().getMaterials().isEmpty())
	    onSelectMaterials(null);
	else {
	    m_aCurrentFileListType = EFileListType.MATERIALS;
	    _toggleFileList();
	}
    }

    @FXML
    protected void onClickMetaContentBox(@Nullable final ActionEvent aActionEvent) {
	_toggleMetaContentBox(!metaContentHBox.isVisible());
    }

    @FXML
    protected void onClickMetaContentDropZone(@Nullable final MouseEvent aMouseEvent) {
	if (ClientData.getInstance().getMetaContentFiles().isEmpty())
	    onSelectMetaContent(null);
	else {
	    m_aCurrentFileListType = EFileListType.METACONTENTFILES;
	    _toggleFileList();
	}
    }

    @FXML
    protected void onSelectMetaContent(@Nullable final ActionEvent aActionEvent) {
	if (!ClientData.getInstance().isRunning()) {
	    final FileChooser aMultipleFileChooser = new FileChooser();
	    aMultipleFileChooser.setTitle(m_aResourceBundle.getString("text.select.metacontent.files"));
	    aMultipleFileChooser.setInitialDirectory(ClientData.getInstance().getMetaContentFilesDirectory());

	    _setMetaContentFiles(aMultipleFileChooser.showOpenMultipleDialog(metaContentDropZoneHBox.getScene().getWindow()));
	}
    }

    @FXML
    protected void onEnterCopyPath(@Nullable final ActionEvent aActionEvent) {
	final String sCopyFilePath = copyPathTextField.getText();

	_setCopyPath(StringUtils.isNotEmpty(sCopyFilePath) ? new File(sCopyFilePath) : null);
    }

    @FXML
    protected void onSelectCopyPath(@Nullable final ActionEvent aActionEvent) {
	final DirectoryChooser aDirectoryChooser = new DirectoryChooser();
	aDirectoryChooser.setTitle(m_aResourceBundle.getString("text.select.metacontent.files"));
	aDirectoryChooser.setInitialDirectory(ClientData.getInstance().getCopyDirectory());

	_setCopyPath(aDirectoryChooser.showDialog(copyPathTextField.getScene().getWindow()));
    }

    @FXML
    protected void onSelectSet(@Nullable final ActionEvent aActionEvent) {
	final String sSelectedSet = uploadSetComboBox.getSelectionModel().getSelectedItem();
	if (StringUtils.isNotEmpty(sSelectedSet)) {
	    final String sSelectedSetId = sSelectedSet.replaceAll(".*\\[|\\].*", "");

	    if (StringUtils.isNotEmpty(sSelectedSetId))
		ClientData.getInstance().setSelectedSet(sSelectedSetId);

	    _update();
	}
    }

    @FXML
    protected void onClickCopyCheckBox(@Nullable final ActionEvent aActionEvent) {
	ClientData.getInstance().setIsCopy(copyCheckBox.isSelected());

	_update();
    }

    @FXML
    protected void onClickUploadCheckBox(@Nullable final ActionEvent aActionEvent) {
	ClientData.getInstance().setIsUpload(uploadCheckBox.isSelected());

	_update();
    }

    private void _onCompleteThreads() {
	// clean up
	new Thread() {
	    @Override
	    public void run() {
		try {
		    // Utils.cleanDirectory(Value.FILEPATH_TMP);
		    // wait for three seconds
		    TimeUnit.SECONDS.sleep(3);
		} catch (final InterruptedException aInterruptedException) {
		    Thread.currentThread().interrupt();
		} finally {
		    // update status text and hide popup
		    ClientData.getInstance().setRunning(false);
		    Platform.runLater(() -> {
			_setStatusText(m_aResourceBundle.getString("text.about"));
			_update();
			ViewManager.getInstance().hidePopup(POSITION_PROGRESSBARS);
		    });
		}
	    };
	}.start();
    }

    private void _terminateAllThreads() {
	m_aRunningThreads.forEach(aThread -> aThread.terminate());

	_onCompleteThreads();
    }

    @FXML
    protected void onClickStart(@Nullable final ActionEvent aActionEvent) {
	if (ClientData.getInstance().isRunning())
	    _terminateAllThreads();
	else if (ClientData.getInstance().isReadyForStart()) {
	    // set up threads
	    if (!m_aRunningThreads.isEmpty())
		m_aRunningThreads.clear();

	    final int nOverallFileCount = ClientData.getInstance().getOverallFileCount();
	    // show overall progress in status bar
	    final TextProgressBar aBar = new TextProgressBar();
	    {
		m_nOverallProcessCount = 1;

		aBar.setCompletedText(m_aResourceBundle.getString("text.progress.overall.done"));
		aBar.setInsertableProgressText(m_aResourceBundle.getString("text.progress.overall"));
		aBar.setProgress(0);
		aBar.setSize(365, 19);
		aBar.setText(String.valueOf(m_nOverallProcessCount), String.valueOf(nOverallFileCount));

		final Button aButton = new Button();
		final Label aLabel = new Label(">");
		aLabel.setRotate(90);
		aButton.setGraphic(new Group(aLabel));
		aButton.setOnMouseClicked(aAction -> {
		    if (ViewManager.getInstance().isPopupShowing(POSITION_PROGRESSBARS)) {
			aLabel.setRotate(90);
			ViewManager.getInstance().hidePopup(POSITION_PROGRESSBARS);
		    } else {
			aLabel.setRotate(-90);
			ViewManager.getInstance().showPopup(EView.PROGRESSBARS, POSITION_PROGRESSBARS);
		    }
		});
		aButton.setPadding(new Insets(2, 5, 2, 5));

		final VBox aVBox = new VBox();
		aVBox.setMinWidth(430);
		aVBox.getChildren().add(aBar);
		bottomHBox.getChildren().clear();
		bottomHBox.getChildren().add(aVBox);
		bottomHBox.getChildren().add(aButton);
	    }

	    // add on complete callbacks
	    final IOnCompleteFileCallback aOnCompleteFileCallback = () -> {
		aBar.setProgress((double) m_nOverallProcessCount / nOverallFileCount);
		aBar.setText(String.valueOf(m_nOverallProcessCount), String.valueOf(nOverallFileCount));
		m_nOverallProcessCount++;
	    };
	    final IOnCompleteCallback aOnCompleteCallback = () -> {
		aBar.onComplete();
		_onCompleteThreads();
	    };

	    // get all materials to process
	    final Collection<File> aInFiles = ClientData.getInstance().getMaterials();
	    // show progress popup
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
		if (!ClientData.getInstance().isSelectedAndReadyForUpload()) {
		    aFileCopyThread.addCallback(aOnCompleteFileCallback);
		    aFileCopyThread.addCallback(aOnCompleteCallback);
		}
		aFileCopyThread.start();

		aController.add(aCopyProgressBar);
		m_aRunningThreads.add(aFileCopyThread);
	    }
	    // transcode file
	    if (ClientData.getInstance().isSelectedAndReadyForUploadAndHasSet()) {
		// create new set
		try {
		    final SetData aSetData = new SetData();
		    aSetData.setId(IdFactory.getInstance().getStringId());
		    aSetData.setName(metaContentTextField.getText());
		    aSetData.setMetaContent(metaContentTextArea.getText());

		    if (WSClient.getInstance().createSet(ClientData.getInstance().getSelectedSetData().getId(), aSetData)) {
			// blocking queue
			final BlockingQueue<Object> aBlockingQueue = new LinkedBlockingDeque<Object>();

			// transcode thread
			final TextProgressBar aTranscodeProgressBar = new TextProgressBar();
			aTranscodeProgressBar.setCompletedText(m_aResourceBundle.getString("text.progress.transcoding.done"));
			aTranscodeProgressBar.setInsertableProgressText(m_aResourceBundle.getString("text.progress.transcoding"));
			aTranscodeProgressBar.setSize(410, 19);

			final AbstractNotifierThread aTranscodeThread = new TranscodeProgressThread(aInFiles, FileUtils.getDirectorySave(Value.FILEPATH_TMP));
			aTranscodeThread.addCallback(aTranscodeProgressBar);
			aTranscodeThread.setQueue(aBlockingQueue);
			aTranscodeThread.start();

			// upload thread
			final TextProgressBar aUploadProgressBar = new TextProgressBar();
			aUploadProgressBar.setCompletedText(m_aResourceBundle.getString("text.progress.uploading.done"));
			aUploadProgressBar.setInsertableProgressText(m_aResourceBundle.getString("text.progress.uploading"));
			aUploadProgressBar.setSize(410, 19);

			final AbstractNotifierThread aUploadThread = new UploadProgressThread(aSetData.getId());
			aUploadThread.addCallback(aUploadProgressBar);
			aUploadThread.addCallback(aOnCompleteFileCallback);
			aUploadThread.addCallback(aOnCompleteCallback);
			aUploadThread.setQueue(aBlockingQueue);
			aUploadThread.start();

			// add bars to view
			aController.add(aTranscodeProgressBar);
			aController.add(aUploadProgressBar);
			m_aRunningThreads.add(aTranscodeThread);
			m_aRunningThreads.add(aUploadThread);

			// upload meta content files
			ClientData.getInstance().getMetaContentFiles().forEach(aFile -> {
			    try {
				aBlockingQueue.put(new AssetDataWrapper(aFile, null, true));
			    } catch (final Exception e) {
				_setStatusText(m_aResourceBundle.getString("error.internal"));

				return;
			    }
			});
		    } else {
			_setStatusText(m_aResourceBundle.getString("error.create.set"));

			return;
		    }
		} catch (final FailedLoginException_Exception aFailedLoginException) {
		    _setStatusText(m_aResourceBundle.getString("error.login.failed"));

		    return;
		}
	    }

	    ClientData.getInstance().setRunning(true);
	}

	_update();
    }
}
