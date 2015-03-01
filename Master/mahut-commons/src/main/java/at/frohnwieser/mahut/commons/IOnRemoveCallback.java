package at.frohnwieser.mahut.commons;

import javax.annotation.Nonnegative;

public interface IOnRemoveCallback extends ICallback {
    public void onRemove(@Nonnegative int nIndex);
}
