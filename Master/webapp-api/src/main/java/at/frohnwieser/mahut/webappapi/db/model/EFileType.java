package at.frohnwieser.mahut.webappapi.db.model;

import java.util.Arrays;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.apache.commons.io.FilenameUtils;

public enum EFileType {
    DOCUMENT("(doc|docx|ods|odt|ppt|pptx|xls|xslx)"),
    IMAGE("(bmp|gif|jpg|png)"),
    PDF("(pdf)"),
    VIDEO("(mp4|ogg|webm)");

    private final Pattern f_aPattern;

    private EFileType(@Nonnull final String sRegexFileExtension) {
	f_aPattern = Pattern.compile(sRegexFileExtension);
    }

    @Nonnull
    public static EFileType getFileTypeFromName(@Nonnull final String sFileName) {
	final String sFileExtension = FilenameUtils.getExtension(sFileName);

	return Arrays.stream(EFileType.values()).filter(aFileType -> aFileType.f_aPattern.matcher(sFileExtension).matches()).findFirst()
	        .orElse(EFileType.DOCUMENT);
    }
}