package at.frohnwieser.mahut.webappapi.ontology;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nonnull;

public class OntologyManager {
    private static OntologyManager m_aInstance = new OntologyManager();

    private OntologyManager() {
    }

    public static OntologyManager getInstance() {
	return m_aInstance;
    }

    public Collection<String> getSubclasses(@Nonnull final String... sIndividuals) {
	// TODO implement me!
	final Collection<String> aIndividuals = new ArrayList<String>();

	aIndividuals.add("one");
	aIndividuals.add("two");
	aIndividuals.add("test");

	return aIndividuals;
    }
}
