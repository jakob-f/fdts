package at.frohnwieser.mahut.webappapi.ontology;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import at.frohnwieser.mahut.webappapi.config.Configuration;
import at.frohnwieser.mahut.webappapi.config.Configuration.EField;
import at.frohnwieser.mahut.webappapi.fs.manager.FSManager;

public class OntologyManager {
    private final static String CLASS_PREFIX = "#";
    private final static OntologyManager m_aInstance = new OntologyManager();

    private OWLDataFactory m_aFactory;
    private OWLOntology m_aOntology;
    private PrefixManager m_aPM;

    private OntologyManager() {
	try {
	    final File aOntologyDirectory = FSManager.createOnotologyFolder();
	    final OWLOntologyManager aManager = OWLManager.createOWLOntologyManager();
	    m_aFactory = aManager.getOWLDataFactory();
	    m_aOntology = aManager.loadOntologyFromOntologyDocument(new File(aOntologyDirectory.getAbsolutePath() + File.separator
		    + Configuration.getInstance().getAsString(EField.ONTOLOGY_NAME)));
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
	if (StringUtils.isNotEmpty(sClass)) {
	    // TODO make case insensitive
	    final String sClassCasesenstive = WordUtils.capitalizeFully(sClass);
	    final OWLClass aFound = m_aFactory.getOWLClass(CLASS_PREFIX + sClassCasesenstive, m_aPM);
	    // TODO check if class exists
	    return EntitySearcher.getSuperClasses(aFound, m_aOntology).stream()
		    .flatMap(aSuperClass -> EntitySearcher.getSubClasses(aSuperClass.asOWLClass(), m_aOntology).stream())
		    .map(aSubClass -> aSubClass.asOWLClass().getIRI().getShortForm()).filter(aOther -> !aOther.equals(sClassCasesenstive))
		    .collect(Collectors.toCollection(ArrayList::new));
	}

	return new ArrayList<String>();
    }

    @Nonnull
    public Collection<String> getEqualClasses(@Nullable final Collection<String> aClasses) {
	if (CollectionUtils.isNotEmpty(aClasses))
	    return aClasses.stream().flatMap(sClass -> getEqualClasses(sClass).stream()).collect(Collectors.toCollection(ArrayList::new));

	return new ArrayList<String>();
    }
}
