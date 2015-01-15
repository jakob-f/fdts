package at.frohnwieser.mahut.webappapi.ontology;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

public class OntologyManagerTest {

    @Test
    public void testGetSubclasses() {
	final Collection<String> aList = new ArrayList<String>();
	aList.add("Spider");

	OntologyManager.getInstance().getEqualClasses(aList).forEach(System.out::println);
    }
}
