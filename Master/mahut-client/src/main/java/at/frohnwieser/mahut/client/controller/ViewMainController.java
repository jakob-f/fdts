package at.frohnwieser.mahut.client.controller;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
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

import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.client.component.TextProgressBar;
import at.frohnwieser.mahut.client.data.AssetDataWrapper;
import at.frohnwieser.mahut.client.data.ClientData;
import at.frohnwieser.mahut.client.io.AbstractNotifierThread;
import at.frohnwieser.mahut.client.io.FileCopyProgressThread;
import at.frohnwieser.mahut.client.io.TranscodeProgressThread;
import at.frohnwieser.mahut.client.io.UploadProgressThread;
import at.frohnwieser.mahut.client.util.NameIDPair;
import at.frohnwieser.mahut.client.util.SceneUtils.EView;
import at.frohnwieser.mahut.client.util.Value;
import at.frohnwieser.mahut.commons.CommonValue;
import at.frohnwieser.mahut.commons.FileUtils;
import at.frohnwieser.mahut.commons.IOnCompleteCallback;
import at.frohnwieser.mahut.commons.IOnCompleteFileCallback;
import at.frohnwieser.mahut.webapp.FailedLoginException_Exception;
import at.frohnwieser.mahut.wsclient.WSClient;

public class ViewMainController implements Initializable {
    // TOP SECTION
    @FXML
    Text titleText;

    // LEFT COLUMN
    // select materials
    @FXML
    Button materialsSelectButton;
    @FXML
    HBox materialsDropZoneHBox;
    // meta content
    @FXML
    HBox metaContentHBox;
    @FXML
    Button metaContentButton;
    @FXML
    Button metaContentSelectButton;
    @FXML
    HBox metaContentDropZoneHBox;
    @FXML
    HBox metaContentTextHBox;
    @FXML
    TextArea metaContentTextArea;
    @FXML
    Text metaContentWordCountText;
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
    ComboBox<NameIDPair<String>> uploadSetComboBox;
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
    VBox bottomVBox;
    @FXML
    VBox progressVBox;

    private ResourceBundle m_aResourceBundle;
    private Filelist m_aMaterialsFileList;
    private Filelist m_aMetaContentFileList;
    private Collection<AbstractNotifierThread> m_aRunningThreads;
    private int m_nOverallProcessCount;

    private void _setStatusText(@Nullable final String sText) {
	bottomVBox.getChildren().clear();
	bottomVBox.setOnMouseClicked(null);
	if (StringUtils.isNotEmpty(sText)) {
	    bottomVBox.setAlignment(Pos.CENTER);
	    bottomVBox.getChildren().add(new Text(sText));
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
	final ClientData aClientData = ClientData.getInstance();
	_setStatusMark(statusTextMaterials, aClientData.hasMaterials());
	_setStatusMark(statusTextMetaContent, aClientData.hasMetaContentFiles());
	_setStatusMark(statusTextCopy, aClientData.isSelectedAndReadyForCopy());
	_setStatusMark(statusTextUpload, aClientData.isSelectedAndReadyForUploadAndHasSet());
	_setStatusMark(statusTextStart, aClientData.isRunning());
    }

    private void _update() {
	final ClientData aClientData = ClientData.getInstance();
	final boolean bIsRunning = aClientData.isRunning();
	materialsSelectButton.setDisable(bIsRunning);
	metaContentSelectButton.setDisable(bIsRunning);
	metaContentTextArea.setDisable(bIsRunning);
	copyCheckBox.setDisable(bIsRunning || !aClientData.isReadyForCopy());
	final boolean bIsSelectedAndReadyForCopy = aClientData.isSelectedAndReadyForCopy();
	copyPathTextField.setDisable(bIsRunning || !bIsSelectedAndReadyForCopy);
	copyButton.setDisable(bIsRunning || !bIsSelectedAndReadyForCopy);
	uploadCheckBox.setDisable(bIsRunning || !aClientData.isReadyForUpload());
	uploadSetComboBox.setDisable(bIsRunning || !aClientData.isSelectedAndReadyForUpload());
	startButton.setDisable(!aClientData.isReadyForStart());
	startButton.setText(bIsRunning ? m_aResourceBundle.getString("text.abort") : m_aResourceBundle.getString("text.start"));

	_setStatusMarks();
    }

    private void _updateMaterialsDropZone() {
	final Collection<File> aMaterials = ClientData.getInstance().getMaterials();
	final int nUploadFilesSize = aMaterials.size();

	materialsDropZoneHBox.getStyleClass().clear();
	materialsDropZoneHBox.getChildren().clear();
	materialsDropZoneHBox.getStyleClass().add("dropzone");

	if (nUploadFilesSize == 0) {
	    materialsDropZoneHBox.getStyleClass().add("bd-normal");
	    materialsDropZoneHBox.getChildren().add(new Text(m_aResourceBundle.getString("text.drop.files")));
	} else {
	    materialsDropZoneHBox.getStyleClass().add("bd-success");
	    m_aMaterialsFileList.setFiles(aMaterials);
	}

	_setStatusMarks();
    }

    private void _updateMetaContentDropZone() {
	final Collection<File> aMetaContentFiles = ClientData.getInstance().getMetaContentFiles();
	final int nMetaContentFilesSize = aMetaContentFiles.size();

	metaContentDropZoneHBox.getStyleClass().clear();
	metaContentDropZoneHBox.getChildren().clear();
	metaContentDropZoneHBox.getStyleClass().add("dropzone");

	if (nMetaContentFilesSize == 0) {
	    metaContentDropZoneHBox.getStyleClass().add("bd-normal");
	    metaContentDropZoneHBox.getChildren().add(new Text(m_aResourceBundle.getString("text.drop.files")));
	} else {
	    metaContentDropZoneHBox.getStyleClass().add("bd-success");
	    m_aMetaContentFileList.setFiles(aMetaContentFiles);
	}

	_setStatusMarks();
    }

    private void _setUploadFiles(@Nonnull final List<File> aFileList) {
	ClientData.getInstance().addMaterials(aFileList);
	_updateMaterialsDropZone();
	_update();
    }

    private void _setMetaContentFiles(@Nonnull final List<File> aFileList) {
	ClientData.getInstance().addMetaContentFiles(aFileList);
	_updateMetaContentDropZone();
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
	final ClientData aClientData = ClientData.getInstance();
	_setStatusText(m_aResourceBundle.getString("text.about"));
	try {
	    aClientData.reloadWSData();
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
	titleText.setText("@" + aClientData.getUsername());

	// right column fields
	_updateMaterialsDropZone();
	metaContentButton.setText(m_aResourceBundle.getString("button.more"));
	_updateMetaContentDropZone();
	_setCopyPath(aClientData.getCopyDirectory());

	uploadSetComboBox.getItems().clear();
	uploadSetComboBox.getItems().addAll(aClientData.getSetNameIDPairs());
	uploadSetComboBox.getSelectionModel().select(aClientData.getSelectedSetNameIDPair());
	copyCheckBox.setSelected(aClientData.isCopy());
	uploadCheckBox.setSelected(aClientData.isUpload());

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

	// set up File lists
	m_aMaterialsFileList = new Filelist(materialsDropZoneHBox, m_aResourceBundle.getString("text.total.file.count"),
	        m_aResourceBundle.getString("text.clear.list"));
	m_aMaterialsFileList.addOnRemoveCallback(nIndex -> _updateMaterialsDropZone());
	m_aMaterialsFileList.addOnRemoveCallback(nIndex -> _update());
	m_aMetaContentFileList = new Filelist(metaContentDropZoneHBox, m_aResourceBundle.getString("text.total.file.count"),
	        m_aResourceBundle.getString("text.clear.list"));
	m_aMetaContentFileList.addOnRemoveCallback(nIndex -> _updateMetaContentDropZone());
	m_aMetaContentFileList.addOnRemoveCallback(nIndex -> _update());

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
    }

    @FXML
    protected void onClickMetaContentBox(@Nullable final ActionEvent aActionEvent) {
	_toggleMetaContentBox(!metaContentHBox.isVisible());
    }

    @FXML
    protected void onClickMetaContentDropZone(@Nullable final MouseEvent aMouseEvent) {
	if (ClientData.getInstance().getMetaContentFiles().isEmpty())
	    onSelectMetaContent(null);
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
	final NameIDPair<String> aSelectedNameIDPair = uploadSetComboBox.getSelectionModel().getSelectedItem();
	if (aSelectedNameIDPair != null) {
	    ClientData.getInstance().setSelectedSet(aSelectedNameIDPair.getId());
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
			progressVBox.getChildren().clear();
			// TODO set height
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
	final ClientData aClientData = ClientData.getInstance();
	if (aClientData.isRunning())
	    _terminateAllThreads();
	else if (aClientData.isReadyForStart()) {
	    // set up threads
	    if (!m_aRunningThreads.isEmpty())
		m_aRunningThreads.clear();

	    final int nOverallFileCount = aClientData.getOverallFileCount();
	    // show overall progress in status bar
	    final TextProgressBar aBar = new TextProgressBar();
	    {
		m_nOverallProcessCount = 1;

		aBar.setCompletedText(m_aResourceBundle.getString("text.progress.overall.done"));
		aBar.setInsertableProgressText(m_aResourceBundle.getString("text.progress.overall"));
		aBar.setProgress(0);
		aBar.setSize(365, 19);
		aBar.setText(String.valueOf(m_nOverallProcessCount), String.valueOf(nOverallFileCount));

		final VBox aVBox = new VBox();
		aVBox.setMinWidth(430);
		aVBox.getChildren().add(aBar);
		bottomVBox.getChildren().clear();
		bottomVBox.getChildren().add(aVBox);
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
	    final Collection<File> aInFiles = aClientData.getMaterials();
	    // show progress popup
	    progressVBox.getChildren().clear();

	    // copy file
	    if (aClientData.isSelectedAndReadyForCopy()) {
		// copy thread
		final TextProgressBar aCopyProgressBar = new TextProgressBar();
		aCopyProgressBar.setCompletedText(m_aResourceBundle.getString("text.progress.copying.done"));
		aCopyProgressBar.setInsertableProgressText(m_aResourceBundle.getString("text.progress.copying"));
		aCopyProgressBar.setSize(410, 19);

		final AbstractNotifierThread aFileCopyThread = new FileCopyProgressThread(aInFiles, aClientData.getCopyDirectory());
		aFileCopyThread.addCallback(aCopyProgressBar);
		if (!aClientData.isSelectedAndReadyForUpload()) {
		    aFileCopyThread.addCallback(aOnCompleteFileCallback);
		    aFileCopyThread.addCallback(aOnCompleteCallback);
		}
		aFileCopyThread.start();

		progressVBox.getChildren().add(aCopyProgressBar);
		m_aRunningThreads.add(aFileCopyThread);
	    }
	    // transcode file
	    if (aClientData.isSelectedAndReadyForUploadAndHasSet())
		if (WSClient.getInstance().isCreated()) {
		    // create new set
		    final String sParentSetId = aClientData.getSelectedSetNameIDPair().getId();
		    final String sMetaContent = metaContentTextArea.getText();

		    // blocking queue
		    final BlockingQueue<Object> aBlockingQueue = new LinkedBlockingDeque<Object>();

		    // transcode thread
		    final TextProgressBar aTranscodeProgressBar = new TextProgressBar();
		    aTranscodeProgressBar.setCompletedText(m_aResourceBundle.getString("text.progress.transcoding.done"));
		    aTranscodeProgressBar.setInsertableProgressText(m_aResourceBundle.getString("text.progress.transcoding"));
		    aTranscodeProgressBar.setSize(410, 19);

		    final AbstractNotifierThread aTranscodeThread = new TranscodeProgressThread(aInFiles, FileUtils.getDirectorySave(Value.FILEPATH_TMP),
			    sMetaContent);
		    aTranscodeThread.addCallback(aTranscodeProgressBar);
		    aTranscodeThread.setQueue(aBlockingQueue);
		    aTranscodeThread.start();

		    // upload thread
		    final TextProgressBar aUploadProgressBar = new TextProgressBar();
		    aUploadProgressBar.setCompletedText(m_aResourceBundle.getString("text.progress.uploading.done"));
		    aUploadProgressBar.setInsertableProgressText(m_aResourceBundle.getString("text.progress.uploading"));
		    aUploadProgressBar.setSize(410, 19);

		    final AbstractNotifierThread aUploadThread = new UploadProgressThread(sParentSetId);
		    aUploadThread.addCallback(aUploadProgressBar);
		    aUploadThread.addCallback(aOnCompleteFileCallback);
		    aUploadThread.addCallback(aOnCompleteCallback);
		    aUploadThread.setQueue(aBlockingQueue);
		    aUploadThread.start();

		    // add bars to view
		    progressVBox.getChildren().add(aTranscodeProgressBar);
		    progressVBox.getChildren().add(aUploadProgressBar);
		    m_aRunningThreads.add(aTranscodeThread);
		    m_aRunningThreads.add(aUploadThread);

		    // upload meta content files
		    aClientData.getMetaContentFiles().forEach(aFile -> {
			try {
			    aBlockingQueue.put(new AssetDataWrapper(aFile, null, true));
			} catch (final Exception e) {
			    _setStatusText(m_aResourceBundle.getString("error.internal"));

			    return;
			}
		    });
		} else {
		    _setStatusText(m_aResourceBundle.getString("error.login.failed"));
		    return;
		}

	    aClientData.setRunning(true);
	}

	_update();
    }
}
