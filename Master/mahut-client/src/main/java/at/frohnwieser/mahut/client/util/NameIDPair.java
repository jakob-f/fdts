package at.frohnwieser.mahut.client.util;

import javax.annotation.Nonnull;

public class NameIDPair<E> {
    private final E m_aId;
    private final String m_sDisplayName;

    public NameIDPair(@Nonnull final E aId, @Nonnull final String sDisplayName) {
	m_aId = aId;
	m_sDisplayName = sDisplayName;
    }

    @Nonnull
    public E getId() {
	return m_aId;
    }

    @Nonnull
    public String getDisplayName() {
	return m_sDisplayName;
    }

    @Override
    public String toString() {
	return getDisplayName();
    }
}
