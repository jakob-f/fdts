package at.ac.tuwien.media.master.webappapi.db.model;

import java.util.Arrays;

import javax.annotation.Nonnull;

public enum EFileType {
    IMAGE("([^\\s]+(\\.(?i)(bmp|gif|jpg|png))$)"),
    PDF("([^\\s]+(\\.(?i)(pdf))$)"),
    VIDEO("([^\\s]+(\\.(?i)(mp4|ogv|webm))$)"),
    OTHER("([^\\s]+(\\.(?i)([a-x]{3}))$)");

    private final String f_sRegexFileExtension;

    private EFileType(@Nonnull final String sRegexFileExtension) {
	f_sRegexFileExtension = sRegexFileExtension;
    }

    @Nonnull
    public static EFileType getFileTypeFromName(@Nonnull final String sFileName) {
	return Arrays.stream(EFileType.values()).filter(aFileType -> sFileName.matches(aFileType.f_sRegexFileExtension)).findFirst().orElse(EFileType.OTHER);
    }
}