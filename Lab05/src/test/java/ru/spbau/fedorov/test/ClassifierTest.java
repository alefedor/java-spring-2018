package ru.spbau.fedorov.test;

import org.junit.Test;
import ru.spbau.fedorov.algo.Classifier;
import ru.spbau.fedorov.test.classes.*;

import static org.junit.Assert.assertTrue;

public class ClassifierTest {
    @Test
    public void testEmptyClass() throws Classifier.ClassifierException {
        Classifier classifier = new Classifier(Empty.class);
        assertTrue(classifier.getTests().isEmpty());
        assertTrue(classifier.getBefore().isEmpty());
        assertTrue(classifier.getAfter().isEmpty());
        assertTrue(classifier.getBeforeClass().isEmpty());
        assertTrue(classifier.getAfterClass().isEmpty());
    }

    @Test
    public void testValidClass() throws Classifier.ClassifierException {
        Classifier classifier = new Classifier(Valid.class);
        assertTrue(classifier.getTests().size() == 1);
        assertTrue(classifier.getBefore().isEmpty());
        assertTrue(classifier.getAfter().isEmpty());
        assertTrue(classifier.getBeforeClass().isEmpty());
        assertTrue(classifier.getAfterClass().isEmpty());
        assertTrue(classifier.getTests().get(0).getName().equals("test"));
    }

    @Test
    public void testValidWithAllTypes() throws Classifier.ClassifierException {
        Classifier classifier = new Classifier(ValidWithAllTypes.class);
        assertTrue(classifier.getTests().size() == 1);
        assertTrue(classifier.getTests().get(0).getName().equals("test"));
        assertTrue(classifier.getBefore().size() == 1);
        assertTrue(classifier.getBefore().get(0).getName().equals("before"));
        assertTrue(classifier.getAfter().size() == 1);
        assertTrue(classifier.getAfter().get(0).getName().equals("after"));
        assertTrue(classifier.getBeforeClass().size() == 1);
        assertTrue(classifier.getBeforeClass().get(0).getName().equals("beforeClass"));
        assertTrue(classifier.getAfterClass().size() == 1);
        assertTrue(classifier.getAfterClass().get(0).getName().equals("afterClass"));
    }

    @Test(expected = Classifier.ClassifierException.class)
    public void testInvalidAnnotations() throws Classifier.ClassifierException {
        Classifier classifier = new Classifier(InvalidAnnotations.class);
    }

    @Test(expected = Classifier.ClassifierException.class)
    public void testInvalidHasArguments() throws Classifier.ClassifierException {
        Classifier classifier = new Classifier(InvalidHasArguments.class);
    }

    @Test(expected = Classifier.ClassifierException.class)
    public void testInvalidHasReturnType() throws Classifier.ClassifierException {
        Classifier classifier = new Classifier(InvalidHasReturnType.class);
    }
}
