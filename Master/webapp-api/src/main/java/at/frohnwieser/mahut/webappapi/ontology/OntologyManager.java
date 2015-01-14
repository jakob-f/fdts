package at.frohnwieser.mahut.webappapi.ontology;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;

public class OntologyManager {
    private static OntologyManager m_aInstance = new OntologyManager();

    private OntologyManager() {
    }

    public static OntologyManager getInstance() {
	return m_aInstance;
    }

    @Nonnull
    public Collection<String> getSubclasses(@Nullable final Collection<String> aIndividuals) {
	final Collection<String> aRet = new ArrayList<String>();

	if (CollectionUtils.isNotEmpty(aIndividuals)) {
	    // TODO implement me!
	    aRet.add("one");
	    aRet.add("two");
	    aRet.add("test");
	}

	return aRet;
    }
}
