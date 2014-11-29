package at.ac.tuwien.media.master.webappapi.db.model;

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
	for (final EFileType aFileType : EFileType.values())
	    if (sFileName.matches(aFileType.f_sRegexFileExtension))
		return aFileType;

	return EFileType.OTHER;
    }
}