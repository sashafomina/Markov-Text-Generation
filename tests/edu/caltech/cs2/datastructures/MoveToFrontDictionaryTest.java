package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.helpers.Inspection;
import edu.caltech.cs2.helpers.Reflection;
import edu.caltech.cs2.helpers.RuntimeInstrumentation;
import edu.caltech.cs2.interfaces.IDictionary;
import org.junit.jupiter.api.*;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("B")
public class MoveToFrontDictionaryTest extends DictionaryTest {
    private static String DICTIONARY_SOURCE =
            "src/edu/caltech/cs2/datastructures/MoveToFrontDictionary.java";

    @Order(0)
    @DisplayName("Does not use or import disallowed classes")
    @Test
    public void testForInvalidClasses() {
        List<String> regexps = List.of("java.util.^(?!Iterator)(?!Supplier)(?!Stream)", "java.lang.reflect", "java.io");
        Inspection.assertNoImportsOf(DICTIONARY_SOURCE, regexps);
        Inspection.assertNoUsageOf(DICTIONARY_SOURCE, regexps);
    }

    @Order(0)
    @DisplayName("There are no public fields")
    @Test
    public void testNoPublicFields() {
        Reflection.assertNoPublicFields(MoveToFrontDictionary.class);
    }

    protected IDictionary newDictionary() {
        Constructor c = Reflection.getConstructor(MoveToFrontDictionary.class);
        return Reflection.newInstance(c);
    }

    protected RuntimeInstrumentation.ComplexityType getAndPutComplexity() {
        return RuntimeInstrumentation.ComplexityType.LINEAR;
    }

    protected int SINGLE_OP_TIMEOUT_MS() {
        return 100;
    }

    protected int CONTAINS_VALUE_TIMEOUT_MS() {
        return 100;
    }

    void testContainsKey() {
    }

    void testGetComplexity() {
    }

    void testPutComplexity() {
    }
}