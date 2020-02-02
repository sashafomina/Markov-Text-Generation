package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.helpers.Inspection;
import edu.caltech.cs2.helpers.Reflection;
import edu.caltech.cs2.helpers.RuntimeInstrumentation;
import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.textgenerator.NGram;
import org.junit.jupiter.api.*;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("A+")
public class AVLTreeDictionaryTest extends DictionaryTest {

    private static String DICTIONARY_SOURCE =
            "src/edu/caltech/cs2/datastructures/AVLTreeDictionary.java";

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
        Reflection.assertNoPublicFields(AVLTreeDictionary.class);
    }

    protected IDictionary newDictionary() {
        Constructor c = Reflection.getConstructor(AVLTreeDictionary.class);
        return Reflection.newInstance(c);
    }

    protected RuntimeInstrumentation.ComplexityType getAndPutComplexity() {
        return RuntimeInstrumentation.ComplexityType.LOGARITHMIC;
    }

    protected int SINGLE_OP_TIMEOUT_MS() {
        return 60;
    }

    protected int CONTAINS_VALUE_TIMEOUT_MS() {
        return 120;
    }

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
                RuntimeInstrumentation.ComplexityType.LOGARITHMIC, provide, containsKey, 8);
    }

    void testRemove(Map<Iterable<String>, Object> testSet) {

    }

    void containsKeyAddRemove(Map<Iterable<Object>, Object> testSet) {

    }

    void testContainsValueAddRemove(Map<Iterable<Object>, Object> testSet) {
    }

    void testSizeAddRemove(Map<Iterable<Object>, Object> testSet) {
    }

    @Order(1)
    @DisplayName("The AVL tree self-balances")
    @Test
    public void testBalance() {
        AVLTreeDictionary<String, Integer> avl = new AVLTreeDictionary<>();

        // Left rotation
        avl.put("m", 1);
        avl.put("s", 2);
        avl.put("x", 3);
        assertEquals("{s: 2, m: 1, x: 3}", avl.toString());

        // Right rotation
        avl.put("i", 4);
        avl.put("a", 5);
        assertEquals("{s: 2, i: 4, x: 3, a: 5, m: 1}",
                avl.toString());

        // Left-right rotation
        avl.put("p", 6);
        assertEquals("{m: 1, i: 4, s: 2, a: 5, p: 6, x: 3}",
                avl.toString());

        // Right-left rotation
        avl.put("u", 7);
        avl.put("y", 8);
        avl.put("t", 9);
        assertEquals("{m: 1, i: 4, u: 7, a: 5, s: 2, x: 3, p: 6, t: 9, y: 8}",
                avl.toString());

    }
}