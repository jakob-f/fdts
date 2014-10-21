package at.ac.tuwien.media.master.commons;

import javax.annotation.Nonnull;

public interface IOnCompleteNotifyListener {
    void onThreadComplete(@Nonnull final Thread thread);
}
