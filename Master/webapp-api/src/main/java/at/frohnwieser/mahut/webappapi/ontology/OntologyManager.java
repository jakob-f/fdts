package at.frohnwieser.mahut.webappapi.ontology;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

public class OntologyManager {
    private final static String CLASS_PREFIX = "#";
    private final static OntologyManager m_aInstance = new OntologyManager();

    private OWLDataFactory m_aFactory;
    private OWLOntology m_aOntology;
    private PrefixManager m_aPM;

    private OntologyManager() {
	try {
	    final OWLOntologyManager aManager = OWLManager.createOWLOntologyManager();
	    m_aFactory = aManager.getOWLDataFactory();
	    m_aOntology = aManager.loadOntologyFromOntologyDocument(new File("src/test/resources/animals.owl"));
	    // TODO
	    final IRI documentIRI = aManager.getOntologyDocumentIRI(m_aOntology);
	    m_aPM = new DefaultPrefixManager("http://nlp.shef.ac.uk/abraxas/ontologies/animals.owl");
	} catch (final OWLOntologyCreationException e) {
	    e.printStackTrace();
	}
    }

    public static OntologyManager getInstance() {
	return m_aInstance;
    }

    @Nonnull
    public Collection<String> getEqualClasses(@Nullable final String sClass) {
	final Collection<String> aRet = new ArrayList<String>();

	// TODO make case insensitive
	if (StringUtils.isNotEmpty(sClass)) {
	    final OWLClass aFound = m_aFactory.getOWLClass(CLASS_PREFIX + sClass, m_aPM);
	    final Collection<OWLClassExpression> aSuperClasses = EntitySearcher.getSuperClasses(aFound, m_aOntology);
	    if (CollectionUtils.isNotEmpty(aSuperClasses))
		for (final OWLClassExpression aSuperClass : aSuperClasses) {
		    final Collection<OWLClassExpression> aSubClasses = EntitySearcher.getSubClasses(aSuperClass.asOWLClass(), m_aOntology);
		    aSubClasses.stream().forEach(aSubClass -> aRet.add(aSubClass.asOWLClass().getIRI().getShortForm()));
		}
	}

	return aRet;
    }

    @Nonnull
    public Collection<String> getEqualClasses(@Nullable final Collection<String> aClasses) {
	final Collection<String> aRet = new ArrayList<String>();

	if (CollectionUtils.isNotEmpty(aClasses))
	    aClasses.stream().forEach(sClass -> aRet.addAll(getEqualClasses(sClass)));

	return aRet;
    }
}
