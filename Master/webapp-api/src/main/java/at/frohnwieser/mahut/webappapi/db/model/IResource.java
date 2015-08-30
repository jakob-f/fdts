package at.frohnwieser.mahut.webappapi.db.model;

import java.io.Serializable;

import at.frohnwieser.mahut.commons.IHasId;
import at.frohnwieser.mahut.commons.IValidate;

public interface IResource extends Serializable, IHasId, IValidate, Comparable<AbstractResource> {

    @Override
    public abstract String getId();

    public abstract long getCreationTimeStamp();

    public abstract String getCreationTimeStampFormatted();

    public abstract String getOwnerId();

    public abstract String getName();

    public abstract String getHash();

    public abstract IResource resetHash();

    public abstract long getModificationTimeStamp();

    public abstract String getModificationTimeStampFormatted();

    public abstract String getMetaContent();

    public abstract String getMetaContentFormatted();

    public abstract long getViewCount();

    public abstract long decreaseViewCount();

    public abstract EState getState();

    public EFileType getFileType();

    public abstract void setName(String sName);

    public abstract void setModificationTimeStamp(long nModificationTimeStamp);

    public abstract void setMetaContent(String sMetaContent);

    public abstract IResource setViewCount(long nViewCount);

    public abstract IResource setState(EState aState);

    @Override
    public abstract boolean isValid();

}