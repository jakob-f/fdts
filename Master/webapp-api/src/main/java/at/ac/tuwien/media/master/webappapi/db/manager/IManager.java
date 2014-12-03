package at.ac.tuwien.media.master.webappapi.db.manager;

import java.util.Collection;

import javax.annotation.Nullable;

public interface IManager<E> {

    public Collection<E> all();

    public boolean contains(@Nullable final E aEntry);

    public boolean delete(@Nullable final E aEntry);

    public boolean save(@Nullable final E aEntry);
}
