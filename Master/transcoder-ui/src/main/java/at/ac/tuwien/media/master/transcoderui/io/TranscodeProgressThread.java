package at.ac.tuwien.media.master.transcoderui.io;

import java.util.Scanner;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.time.DurationFormatUtils;

import at.ac.tuwien.media.master.commons.ISetProgress;
import at.ac.tuwien.media.master.commons.ISetText;
import at.ac.tuwien.media.master.commons.IOnCompleteNotifyListener;

public class TranscodeProgressThread extends AbstractNotifierThread {
    private final Process m_aProcess;

    public TranscodeProgressThread(@Nonnull final Process aProcess, @Nullable final ISetProgress aProgressIndicator, @Nullable final ISetText aProgressText,
	    @Nullable final IOnCompleteNotifyListener aCompleteListener) {
	super(aProgressIndicator, aProgressText, aCompleteListener);

	if (aProcess == null)
	    throw new NullPointerException("process");

	m_aProcess = aProcess;
    }

    @Override
    public void run() {
	Scanner aScanner = null;

	try {
	    aScanner = new Scanner(m_aProcess.getInputStream());

	    // parse estimated duration
	    final String sDuration = aScanner.findWithinHorizon(Pattern.compile("(?<=Duration: )[^,]*"), 0);
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
	    while (null != (sTime = aScanner.findWithinHorizon(Pattern.compile("(?<=time=)[\\d:.]*"), 0))) {
		sTimeHMS = sTime.split(":");

		// calc progress in percent
		nTimeLeft = Integer.parseInt(sTimeHMS[0]) * 3600 + Integer.parseInt(sTimeHMS[1]) * 60 + Double.parseDouble(sTimeHMS[2]);
		nProgress = nTimeLeft / nEstimatedSeconds;

		// set values
		_setCallbackValues(nProgress, DurationFormatUtils.formatDuration((long) ((nEstimatedSeconds - nTimeLeft) * 1000), "HH:mm:ss,SSS"));
	    }
	} catch (final Exception aException) {
	    aException.printStackTrace();
	} finally {
	    if (aScanner != null)
		aScanner.close();

	    // notify listener
	    _notifyListener(this);
	}
    }
}
