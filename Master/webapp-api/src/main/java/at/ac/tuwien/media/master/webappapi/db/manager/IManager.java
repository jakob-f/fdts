package at.ac.tuwien.media.master.webappapi.db.manager;

import java.util.Collection;

import javax.annotation.Nullable;

public interface IManager<E> {

    public Collection<E> all();

    public Collection<E> delete(@Nullable final E aEntry);

    public Collection<E> save(@Nullable final E aEntry);
}
