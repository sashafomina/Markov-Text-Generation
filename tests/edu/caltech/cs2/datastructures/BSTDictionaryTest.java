package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.helpers.Inspection;
import edu.caltech.cs2.helpers.Reflection;
import edu.caltech.cs2.helpers.RuntimeInstrumentation;
import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.textgenerator.NGram;
import org.junit.jupiter.api.*;

import java.lang.reflect.Constructor;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("A")
public class BSTDictionaryTest extends DictionaryTest {

    private static String DICTIONARY_SOURCE =
            "src/edu/caltech/cs2/datastructures/BSTDictionary.java";

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
        Reflection.assertNoPublicFields(BSTDictionary.class);
    }

    protected IDictionary newDictionary() {
        Constructor c = Reflection.getConstructor(BSTDictionary.class);
        return Reflection.newInstance(c);
    }

    protected RuntimeInstrumentation.ComplexityType getAndPutComplexity() {
        return RuntimeInstrumentation.ComplexityType.LOGARITHMIC;
    }

    protected int SINGLE_OP_TIMEOUT_MS() {
        return 50;
    }

    protected int CONTAINS_VALUE_TIMEOUT_MS() {
        return 100;
    }

    /*
    @Override
    @Order(2)
    @DisplayName("Test containsKey() -- complexity (worst case)")
    @Test
    void testContainsKey() {
        Function<Integer, IDictionary<Iterable<String>, Object>> provide = (Integer numElements) -> {
            IDictionary<Iterable<String>, Object> t = newDictionary();
            String[] array = new String[numElements];
            for (int i = 0; i < numElements; i++) {
                array[i] = "" + i;
            }
            Arrays.sort(array, (x, y) -> -x.compareTo(y));
            for (int i = 1; i < numElements - 4; i++) {
                t.put(new NGram(new String[]{array[i]}), 0);
            }
            return t;
        };

        Consumer<IDictionary<Iterable<String>, Object>> containsKey = (IDictionary<Iterable<String>, Object> t) -> {
            t.containsKey(new NGram(new String[]{"0"}));
        };
        RuntimeInstrumentation.assertAtMost("containsKey",
                RuntimeInstrumentation.ComplexityType.LINEAR, provide, containsKey, 5);
    }
    */
}