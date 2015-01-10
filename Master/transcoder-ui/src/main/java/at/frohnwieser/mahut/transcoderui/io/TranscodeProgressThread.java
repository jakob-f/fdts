package at.frohnwieser.mahut.transcoderui.io;

import java.io.File;
import java.util.Collection;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import net.minidev.json.JSONStyle;

import org.apache.commons.lang3.time.DurationFormatUtils;

import at.frohnwieser.mahut.ffmpegwrapper.FFMPEGWrapper;
import at.frohnwieser.mahut.ffmpegwrapper.FFMPEGWrapper.EFormat;
import at.frohnwieser.mahut.ffmpegwrapper.FFMPEGWrapper.EQuality;
import at.frohnwieser.mahut.transcoderui.data.AssetDataWrapper;

public class TranscodeProgressThread extends AbstractNotifierThread {
    private final static EFormat TRANSCODE_FORMAT = EFormat.OGG;

    public TranscodeProgressThread(@Nonnull final Collection<File> aInFiles, @Nonnull final File aOutDirectory) {
	super(aInFiles, aOutDirectory);
    }

    @Override
    protected void _process(@Nonnull final File aInFile, @Nonnull final File aOutDirectory) {
	Scanner aScanner = null;

	try {
	    if (!m_bTerminate) {
		final Process aFFMPEGProcess = FFMPEGWrapper.transcode(aInFile, aOutDirectory, TRANSCODE_FORMAT, EQuality.P720);
		aScanner = new Scanner(aFFMPEGProcess.getInputStream());

		// parse estimated duration
		final String sDuration = aScanner.findWithinHorizon(Pattern.compile("(?<=Duration: )[^,]*"), 0);
		// TODO handle this better
		if (sDuration == null)
		    throw new RuntimeException("could not parse duration");
		final String[] sDurationHMS = sDuration.split(":");
		final double nEstimatedSeconds = Integer.parseInt(sDurationHMS[0]) * 3600 + Integer.parseInt(sDurationHMS[1]) * 60
		        + Double.parseDouble(sDurationHMS[2]);

		// parse time
		String sTime;
		String[] sTimeHMS;
		double nTimeLeft;
		double nProgress;
		while (null != (sTime = aScanner.findWithinHorizon(Pattern.compile("(?<=time=)[\\d:.]*"), 0)) && !m_bTerminate) {
		    sTimeHMS = sTime.split(":");

		    // calc progress in percent
		    nTimeLeft = Integer.parseInt(sTimeHMS[0]) * 3600 + Integer.parseInt(sTimeHMS[1]) * 60 + Double.parseDouble(sTimeHMS[2]);
		    nProgress = nTimeLeft / nEstimatedSeconds;

		    // set values
		    _setCallbackValues(nProgress, aInFile.getName(),
			    DurationFormatUtils.formatDuration((long) ((nEstimatedSeconds - nTimeLeft) * 1000), "HH:mm:ss"));
		}

		if (!m_bTerminate) {
		    // write to queue
		    final File aTranscodedFile = FFMPEGWrapper.getOutputFile(aInFile, aOutDirectory, TRANSCODE_FORMAT);
		    final String sMetaContent = FFMPEGWrapper.metadata(aInFile).toJSONString(JSONStyle.MAX_COMPRESS);

		    if (aTranscodedFile.isFile()) {
			_putInQueue(new AssetDataWrapper(aTranscodedFile, sMetaContent, false));
		    } else
			throw new RuntimeException("Cannot find file '" + aTranscodedFile.getAbsolutePath() + "'");

		    // set values
		    _setCallbackValues(1, aInFile.getName(), "0");
		    // notify listener
		    _notifyOnComplete(this);
		} else {
		    if (aFFMPEGProcess.isAlive())
			aFFMPEGProcess.destroyForcibly();
		}
	    }
	} catch (final Exception aException) {
	    throw new RuntimeException(aException);
	} finally {
	    if (aScanner != null)
		aScanner.close();
	}
    }

    @Override
    public void run() {
	super.run();

	// notify queue on finished
	try {
	    _putInQueue("xxx");
	} catch (final InterruptedException aIE) {
	    aIE.printStackTrace();
	}
    }
}
