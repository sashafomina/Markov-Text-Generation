package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.helpers.Inspection;
import edu.caltech.cs2.helpers.Reflection;
import edu.caltech.cs2.helpers.RuntimeInstrumentation;
import edu.caltech.cs2.interfaces.IDictionary;
import org.junit.jupiter.api.*;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.Supplier;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("B")
public class ChainingHashDictionaryTest extends DictionaryTest {
    private static String DICTIONARY_SOURCE =
            "src/edu/caltech/cs2/datastructures/ChainingHashDictionary.java";

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
        Reflection.assertNoPublicFields(ChainingHashDictionary.class);
    }

    protected IDictionary newDictionary() {
        Constructor c = Reflection.getConstructor(ChainingHashDictionary.class, Supplier.class);
        Supplier<IDictionary> sup = MoveToFrontDictionary::new;
        return Reflection.newInstance(c, sup);
    }

    protected RuntimeInstrumentation.ComplexityType getAndPutComplexity() {
        return RuntimeInstrumentation.ComplexityType.CONSTANT;
    }
}