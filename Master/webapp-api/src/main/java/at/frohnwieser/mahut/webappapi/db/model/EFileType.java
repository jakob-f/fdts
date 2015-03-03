package at.frohnwieser.mahut.webappapi.db.model;

import java.util.Arrays;

import javax.annotation.Nonnull;

import org.apache.commons.io.FilenameUtils;

public enum EFileType {
    DOCUMENT("|doc|docx|ods|odt|ppt|pptx|xls|xslx|"),
    IMAGE("|bmp|gif|jpg|png|"),
    PDF("|pdf|"),
    VIDEO("|mp4|ogg|webm|");

    private final String f_sRegexFileExtension;

    private EFileType(@Nonnull final String sRegexFileExtension) {
	f_sRegexFileExtension = sRegexFileExtension;
    }

    @Nonnull
    public static EFileType getFileTypeFromName(@Nonnull final String sFileName) {
	final String sFileExtension = "|" + FilenameUtils.getExtension(sFileName.toLowerCase()) + "|";

	// faster than pattern matcher
	return Arrays.stream(EFileType.values()).filter(aFileType -> aFileType.f_sRegexFileExtension.contains(sFileExtension)).findFirst()
	        .orElse(EFileType.DOCUMENT);
    }
}