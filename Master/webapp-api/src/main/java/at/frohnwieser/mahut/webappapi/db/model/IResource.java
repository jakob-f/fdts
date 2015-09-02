package at.frohnwieser.mahut.webappapi.db.model;

import java.io.Serializable;

import at.frohnwieser.mahut.commons.IHasId;
import at.frohnwieser.mahut.commons.IValidate;

public interface IResource extends Serializable, IHasId, IValidate, Comparable<AbstractResource> {

    @Override
    public String getId();

    public long getCreationTimeStamp();

    public String getCreationTimeStampFormatted();

    public String getOwnerId();

    public String getName();

    public String getHash();

    public IResource resetHash();

    public long getModificationTimeStamp();

    public String getModificationTimeStampFormatted();

    public String getMetaContent();

    public String getMetaContentFormatted();

    public long getViewCount();

    public long decreaseViewCount();

    public EState getState();

    public EFileType getFileType();

    public void setName(String sName);

    public void setModificationTimeStamp(long nModificationTimeStamp);

    public void setMetaContent(String sMetaContent);

    public IResource setViewCount(long nViewCount);

    public IResource setState(EState aState);

    @Override
    public boolean isValid();
}