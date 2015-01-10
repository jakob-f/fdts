package at.frohnwieser.mahut.commons;

import javax.annotation.Nonnegative;

public interface IOnRemoveCallback {
    public void onRemove(@Nonnegative int nIndex);
}
