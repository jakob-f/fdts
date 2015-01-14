package at.frohnwieser.mahut.webappapi.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;

import at.frohnwieser.mahut.ffmpegwrapper.FFMPEGWrapper;
import at.frohnwieser.mahut.ffmpegwrapper.FFMPEGWrapper.EQuality;
import at.frohnwieser.mahut.webappapi.db.model.EFileType;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

public final class ThumbnailGenerator {
    final static int THUMBNAIL_WIDTH = 360;

    private static boolean _scaledImage(final Image aInputImage, @Nonnull final File aOutFile) throws IOException {
	if (aInputImage != null) {
	    final int nImageHeight = aInputImage.getHeight(null);
	    final int nImageWidth = aInputImage.getWidth(null);
	    final int nThumbnailHeight = (int) (nImageHeight / ((float) nImageWidth / (float) THUMBNAIL_WIDTH));
	    final BufferedImage aResizedImage = new BufferedImage(THUMBNAIL_WIDTH, nThumbnailHeight, BufferedImage.TYPE_INT_RGB);
	    final Graphics2D aGraphics2d = aResizedImage.createGraphics();

	    aGraphics2d.drawImage(aInputImage, 0, 0, THUMBNAIL_WIDTH, nThumbnailHeight, null);
	    aGraphics2d.dispose();
	    aGraphics2d.setComposite(AlphaComposite.Src);
	    aGraphics2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    aGraphics2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	    aGraphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	    return ImageIO.write(aResizedImage, Value.FILETYPE_THUMBNAIL, aOutFile);
	}

	return false;
    }

    private static boolean _fromImage(@Nonnull final File aInFile, @Nonnull final File aOutDirectory) throws IOException {
	return _scaledImage(ImageIO.read(aInFile), FFMPEGWrapper.getOutputFile(aInFile, aOutDirectory, Value.FILETYPE_THUMBNAIL));
    }

    private static boolean _fromPDF(@Nonnull final File aInFile, @Nonnull final File aOutDirectory) {
	RandomAccessFile aRAFile = null;
	FileChannel aFileChannel = null;

	try {
	    aRAFile = new RandomAccessFile(aInFile, "r");
	    aFileChannel = aRAFile.getChannel();
	    final ByteBuffer aBuffer = aFileChannel.map(FileChannel.MapMode.READ_ONLY, 0, aFileChannel.size());
	    final PDFPage aPage = new PDFFile(aBuffer).getPage(0);
	    final Rectangle aRectangle = new Rectangle(0, 0, (int) aPage.getBBox().getWidth(), (int) aPage.getBBox().getHeight());
	    final Image aInputImage = aPage.getImage(aRectangle.width, aRectangle.height, aRectangle, null, true, true);

	    return _scaledImage(aInputImage, FFMPEGWrapper.getOutputFile(aInFile, aOutDirectory, Value.FILETYPE_THUMBNAIL));
	} catch (final Exception aException) {
	    throw new RuntimeException(aException);
	} finally {
	    try {
		if (aRAFile != null)
		    aRAFile.close();
	    } catch (final IOException e) {
	    }
	    try {
		if (aFileChannel != null)
		    aFileChannel.close();
	    } catch (final IOException e) {
	    }
	}
    }

    private static boolean _fromVideo(@Nonnull final File aInFile, @Nonnull final File aOutDirectory) throws IOException {
	return FFMPEGWrapper.thumbnail(aInFile, aOutDirectory, Value.FILETYPE_THUMBNAIL, EQuality.P360, "00:00:00.010");
    }

    public static boolean create(@Nullable final File aInFile, @Nullable final File aSetDirectory) {
	try {
	    final File aOutDirectory = new File(aSetDirectory.getAbsolutePath() + File.separator + Value.SET_FOLDER_THUMBNAILS);

	    if (aOutDirectory.isDirectory()) {
		final EFileType aFileType = EFileType.getFileTypeFromName(aInFile.getName());

		if (aFileType == EFileType.DOCUMENT)
		    return true;
		if (aFileType == EFileType.IMAGE)
		    return _fromImage(aInFile, aOutDirectory);
		if (aFileType == EFileType.PDF)
		    return _fromPDF(aInFile, aOutDirectory);
		if (aFileType == EFileType.VIDEO)
		    return _fromVideo(aInFile, aOutDirectory);
	    }
	} catch (final Exception aException) {
	    aException.printStackTrace();
	}

	return false;
    }
}
