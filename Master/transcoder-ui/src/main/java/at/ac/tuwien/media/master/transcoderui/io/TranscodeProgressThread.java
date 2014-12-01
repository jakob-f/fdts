package at.ac.tuwien.media.master.transcoderui.io;

import java.io.File;
import java.util.Collection;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.time.DurationFormatUtils;

import at.ac.tuwien.media.master.ffmpegwrapper.FFMPEGWrapper;
import at.ac.tuwien.media.master.ffmpegwrapper.FFMPEGWrapper.EFormat;
import at.ac.tuwien.media.master.ffmpegwrapper.FFMPEGWrapper.EQuality;
import at.ac.tuwien.media.master.transcoderui.util.Value;

public class TranscodeProgressThread extends AbstractNotifierThread {

    public TranscodeProgressThread(@Nonnull final Collection<File> aInFiles, @Nonnull final File aOutDirectory) {
	super(aInFiles, aOutDirectory);
    }

    @Override
    protected void _process(@Nonnull final File aInFile, @Nonnull final File aOutDirectory) {
	Scanner aScanner = null;

	try {
	    if (!m_bTerminate) {
		final Process aFFMPEGProcess = FFMPEGWrapper.transcode(aInFile, new File(Value.FILEPATH_TMP), EFormat.OGG, EQuality.P720);
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
		    _putInQueue("finshed " + aInFile.getName());

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
	    aException.printStackTrace();
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
