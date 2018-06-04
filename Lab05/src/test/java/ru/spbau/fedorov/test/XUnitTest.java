package ru.spbau.fedorov.test;

import org.junit.Before;
import org.junit.Test;
import ru.spbau.fedorov.algo.Classifier;
import ru.spbau.fedorov.algo.XUnit;
import ru.spbau.fedorov.test.classes.*;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class XUnitTest {
    private static OutputStream out;
    private static PrintStream output;

    @Before
    public void setUp() {
        out = new ByteArrayOutputStream();
        output = new PrintStream(out);
        System.setOut(output);
    }

    @Test
    public void testEmpty() throws XUnit.InvocationException, Classifier.ClassifierException {
        XUnit.runTests(Empty.class, output);
        assertTrue(out.toString().contains("Tests passed: 0/0"));
        assertTrue(out.toString().contains("Tests total: 0(0 was(were) ignored)"));
    }

    @Test
    public void testValid() throws XUnit.InvocationException, Classifier.ClassifierException {
        XUnit.runTests(Valid.class, output);
        assertTrue(out.toString().contains("Tests passed: 1/1"));
        assertTrue(out.toString().contains("Tests total: 1(0 was(were) ignored)"));
        assertTrue(out.toString().contains("Test test finished with result 'OK'"));
    }

    @Test
    public void testValidTests() throws XUnit.InvocationException, Classifier.ClassifierException {
        XUnit.runTests(ValidCorrectTests.class, output);
        assertTrue(out.toString().contains("Tests passed: 3/3"));
        assertTrue(out.toString().contains("Tests total: 3(0 was(were) ignored)"));
        assertTrue(out.toString().contains("Test test1 finished with result 'OK'"));
        assertTrue(out.toString().contains("Test test2 finished with result 'OK'"));
        assertTrue(out.toString().contains("Test test3 finished with result 'OK'"));
    }

    @Test
    public void testValidIgnored() throws XUnit.InvocationException, Classifier.ClassifierException {
        XUnit.runTests(ValidIgnored.class, output);
        assertTrue(out.toString().contains("Tests passed: 0/0"));
        assertTrue(out.toString().contains("Tests total: 3(3 was(were) ignored)"));
        assertTrue(out.toString().contains("Test test1 was ignored due to reason: " + "Empty test is too bad"));
        assertTrue(out.toString().contains("Test test2 was ignored due to reason: " + "Can't see any difference with the previous one"));
        assertTrue(out.toString().contains("Test test3 was ignored due to reason: " + "I hate exceptions"));
    }

    @Test
    public void testValidAfter() throws XUnit.InvocationException, Classifier.ClassifierException {
        XUnit.runTests(ValidAfter.class, output);
        assertTrue(out.toString().contains("Tests passed: 2/2"));
        assertTrue(out.toString().contains("Tests total: 3(1 was(were) ignored)"));
        assertTrue(out.toString().contains("Test test1 finished with result 'OK'"));
        assertTrue(out.toString().contains("Test test2 finished with result 'OK'"));
        assertTrue(out.toString().contains("Test test3 was ignored due to reason: " + "don't like this test"));
        assertEquals(2, ValidAfter.getCnt1());
        assertEquals(2, ValidAfter.getCnt2());
    }

    @Test
    public void testValidBefore() throws XUnit.InvocationException, Classifier.ClassifierException {
        XUnit.runTests(ValidBefore.class, output);
        assertTrue(out.toString().contains("Tests passed: 2/2"));
        assertTrue(out.toString().contains("Tests total: 3(1 was(were) ignored)"));
        assertTrue(out.toString().contains("Test test1 finished with result 'OK'"));
        assertTrue(out.toString().contains("Test test2 finished with result 'OK'"));
        assertTrue(out.toString().contains("Test test3 was ignored due to reason: " + "don't like this test"));
        assertEquals(2, ValidBefore.getCnt1());
        assertEquals(2, ValidBefore.getCnt2());
    }

    @Test
    public void testValidAfterClass() throws XUnit.InvocationException, Classifier.ClassifierException {
        XUnit.runTests(ValidAfterClass.class, output);
        assertTrue(out.toString().contains("Tests passed: 2/2"));
        assertTrue(out.toString().contains("Tests total: 3(1 was(were) ignored)"));
        assertTrue(out.toString().contains("Test test1 finished with result 'OK'"));
        assertTrue(out.toString().contains("Test test2 finished with result 'OK'"));
        assertTrue(out.toString().contains("Test test3 was ignored due to reason: " + "don't like this test"));
        assertEquals(1, ValidAfterClass.getCnt1());
        assertEquals(1, ValidAfterClass.getCnt2());
    }

    @Test
    public void testValidBeforeClass() throws XUnit.InvocationException, Classifier.ClassifierException {
        XUnit.runTests(ValidBeforeClass.class, output);
        assertTrue(out.toString().contains("Tests passed: 2/2"));
        assertTrue(out.toString().contains("Tests total: 3(1 was(were) ignored)"));
        assertTrue(out.toString().contains("Test test1 finished with result 'OK'"));
        assertTrue(out.toString().contains("Test test2 finished with result 'OK'"));
        assertTrue(out.toString().contains("Test test3 was ignored due to reason: " + "don't like this test"));
        assertEquals(1, ValidBeforeClass.getCnt1());
        assertEquals(1, ValidBeforeClass.getCnt2());
    }

    @Test
    public void testValidExpected() throws XUnit.InvocationException, Classifier.ClassifierException {
        XUnit.runTests(ValidExpected.class, output);
        assertTrue(out.toString().contains("Tests passed: 2/2"));
        assertTrue(out.toString().contains("Tests total: 2(0 was(were) ignored)"));
        assertTrue(out.toString().contains("Test test1 finished with result 'OK'"));
        assertTrue(out.toString().contains("Test test2 finished with result 'OK'"));
    }

    @Test(expected = XUnit.InvocationException.class)
    public void testInvalidNoDefaultConstructor() throws XUnit.InvocationException, Classifier.ClassifierException {
        XUnit.runTests(InvalidNoDefaultConstructor.class, output);
    }

    @Test
    public void testInvalidExceptionThrown() throws XUnit.InvocationException, Classifier.ClassifierException {
        XUnit.runTests(InvalidExceptionThrown.class, output);
        assertTrue(out.toString().contains("Tests passed: 0/1"));
        assertTrue(out.toString().contains("Tests total: 1(0 was(were) ignored)"));
        assertTrue(out.toString().contains("Test test finished with result 'java.lang.IllegalArgumentException " +
                                            "was thrown, while no exception was expected'"));
    }

    @Test
    public void testInvalidExpected() throws XUnit.InvocationException, Classifier.ClassifierException {
        XUnit.runTests(InvalidExpected.class, output);
        assertTrue(out.toString().contains("Tests passed: 0/1"));
        assertTrue(out.toString().contains("Tests total: 1(0 was(were) ignored)"));
        assertTrue(out.toString().contains("Test test finished with result 'java.lang.IllegalArgumentException " +
                "was thrown, while java.lang.RuntimeException was expected'"));
    }

    @Test
    public void testInvalidExpectedButSuccess() throws XUnit.InvocationException, Classifier.ClassifierException {
        XUnit.runTests(InvalidExpectedButSuccess.class, output);
        assertTrue(out.toString().contains("Tests passed: 0/1"));
        assertTrue(out.toString().contains("Tests total: 1(0 was(were) ignored)"));
        assertTrue(out.toString().contains("Test test finished with result 'no exception " +
                "was thrown, while java.lang.RuntimeException was expected'"));
    }

    @Test
    public void testValidWithAllTypes() throws XUnit.InvocationException, Classifier.ClassifierException {
        XUnit.runTests(ValidWithAllTypes.class, output);
        assertTrue(out.toString().contains("Tests passed: 1/1"));
        assertTrue(out.toString().contains("Tests total: 1(0 was(were) ignored)"));
        assertTrue(out.toString().contains("Test test finished with result 'OK'"));
    }

    @Test
    public void testLoader() {
        String source = Paths.get("src", "test", "resources").toString();
        XUnit.main(new String[]{source, "ru.spbau.fedorov.test.classes.ValidCorrectTests"});
        assertTrue(out.toString().contains("Tests passed: 3/3"));
        assertTrue(out.toString().contains("Tests total: 3(0 was(were) ignored)"));
        assertTrue(out.toString().contains("Test test1 finished with result 'OK'"));
        assertTrue(out.toString().contains("Test test2 finished with result 'OK'"));
        assertTrue(out.toString().contains("Test test3 finished with result 'OK'"));
    }
}
