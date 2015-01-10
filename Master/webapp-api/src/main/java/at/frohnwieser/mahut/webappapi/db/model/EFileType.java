package at.frohnwieser.mahut.webappapi.db.model;

import java.util.Arrays;

import javax.annotation.Nonnull;

public enum EFileType {
    DOCUMENT("(.+\\.(?i)(doc|docx|ods|odt|ppt|pptx|xls|xslx)$)"),
    IMAGE("(.+\\.(?i)(bmp|gif|jpg|png)$)"),
    PDF("(.+\\.(?i)(pdf)$)"),
    VIDEO("(.+\\.(?i)(mp4|ogg|webm)$)");

    private final String f_sRegexFileExtension;

    private EFileType(@Nonnull final String sRegexFileExtension) {
	f_sRegexFileExtension = sRegexFileExtension;
    }

    @Nonnull
    public static EFileType getFileTypeFromName(@Nonnull final String sFileName) {
	return Arrays.stream(EFileType.values()).filter(aFileType -> sFileName.matches(aFileType.f_sRegexFileExtension)).findFirst().orElse(EFileType.DOCUMENT);
    }
}