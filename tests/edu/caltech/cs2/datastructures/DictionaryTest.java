package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.helpers.Inspection;
import edu.caltech.cs2.helpers.RuntimeInstrumentation;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.textgenerator.NGram;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public abstract class DictionaryTest {
    private static String STRING_SOURCE = "src/edu/caltech/cs2/datastructures/TrieMap.java";

    protected abstract IDictionary newDictionary();
    /*{
        Constructor c = Reflection.getConstructor(ChainingHashDictionary.class, Supplier.class);
        Supplier<IDictionary> sup = MoveToFrontDictionary::new;
        return Reflection.newInstance(c, sup);
    }*/

    protected IDictionary<Iterable<String>, Object> dictionaryFromMap(Map<Iterable<String>, Object> testSet) {
        IDictionary<Iterable<String>, Object> t = newDictionary();

        for (Map.Entry<Iterable<String>, Object> e: testSet.entrySet()) {
            t.put(e.getKey(), e.getValue());
        }

        return t;
    }

    protected int SINGLE_OP_TIMEOUT_MS() {
        return 25;
    }

    protected int CONTAINS_VALUE_TIMEOUT_MS() {
        return 60;
    }

    protected abstract RuntimeInstrumentation.ComplexityType getAndPutComplexity();/* {
        return RuntimeInstrumentation.ComplexityType.CONSTANT;
    }
    */

    private static Map<Iterable<String>, Object> getBooleanTestPoints(int size) {
        Random rand = new Random(2019010310);
        Map<Iterable<String>, Object> testOne = new HashMap<>();

        for (int i = 0; i < size; i++) {
            List<Object> k = new ArrayList<>();

            for (int j = 0; j < 1 + rand.nextInt(20); j++) {
                k.add(rand.nextBoolean());
            }

            IDeque<String> d = new ArrayDeque<>();
            for (int j = 0; j < k.size(); j++) {
                d.addBack("" + k.get(j));
            }
            assertEquals(k.toString(), d.toString(), "Your ArrayDeque does not match the reference ArrayDeque.");

            /*
            if (rand.nextBoolean() && rand.nextBoolean()) {
                testOne.put(k, null);
                continue;
            }
            */

            List<Integer> v = new ArrayList<>();

            for (int j = 0; j < 1 + rand.nextInt(10); j++) {
                v.add(rand.nextInt());
            }

            testOne.put(new NGram(d), v);
        }

        return testOne;
    }

    private static Stream<Arguments> testSetData() {
        Random rand = new Random(2019010310);
        Map<Iterable<String>, Object> testOne = getBooleanTestPoints(5000);
        Map<NGram, Integer> testTwo = new HashMap<>();

        for (int i = 0; i < 5000; i++) {
            List<Integer> k = new ArrayList<>();

            for (int j = 0; j < 1 + rand.nextInt(20); j++) {
                k.add(rand.nextInt(5));
            }

            IDeque<String> d = new ArrayDeque<>();
            for (int j = 0; j < k.size(); j++) {
                d.addBack("" + k.get(j));
            }
            assertEquals(k.toString(), d.toString(), "Your ArrayDeque does not match the reference ArrayDeque.");

            /*
            if (rand.nextBoolean()) {
                testTwo.put(k, null);
                continue;
            }
            */

            testTwo.put(new NGram(d), rand.nextInt());
        }


        Arguments[] args = {Arguments.of(testOne), Arguments.of(testTwo)};
        return Stream.of(args);
    }

    @Order(0)
    @DisplayName("Test put(K key, V value) and get(K key)")
    @ParameterizedTest(name = "Testing Insertion and Access on {0}.")
    @MethodSource("testSetData")
    void testInsertionAndAccess(Map<Iterable<String>, Object> testSet) {
        IDictionary<Iterable<String>, Object> t = dictionaryFromMap(testSet);

        for (Map.Entry<Iterable<String>, Object> e: testSet.entrySet()) {
            assertTimeout(Duration.ofMillis(SINGLE_OP_TIMEOUT_MS()), () -> {
                assertEquals(t.get(e.getKey()), e.getValue());
            });
        }
    }

    @Order(1)
    @DisplayName("Test remove(K key)")
    @ParameterizedTest(name = "Test remove on {0}.")
    @MethodSource("testSetData")
    void testRemove(Map<Iterable<String>, Object> testSet) {
        Random rand = new Random(2019010310);

        IDictionary<Iterable<String>, Object> t = dictionaryFromMap(testSet);

        // Test remove elements that are in the map
        List<Iterable<String>> keys = new ArrayList<>();
        Set<Iterable<String>> keySet = testSet.keySet();
        List<Iterable<String>> removedArr = new ArrayList<>();

        keys.addAll(keySet);

        int runs = (int) (keys.size() * 0.25);

        for (int i = 0; i < runs; i++) {
            int inx = rand.nextInt(keys.size());
            Iterable<String> removed = keys.remove(inx);

            assertTimeout(Duration.ofMillis(3 * SINGLE_OP_TIMEOUT_MS()), () -> {
                Object v = t.remove(removed);

                assertEquals(testSet.get(removed), v,
                        "Removing a key that exists does not return the right value.");

                assertEquals(null, t.get(removed), "Removing a key does not delete the pair.");
            });


            removedArr.add(removed);
        }

        // Test remove elements not in the map
        for (Iterable<String> k : removedArr) {
            assertTimeout(Duration.ofMillis(SINGLE_OP_TIMEOUT_MS()), () -> {
                assertEquals(null, t.remove(k),
                        "Removing a key that does not exist does not return null.");
            });
        }

        for (Iterable<String> k : keys) {
            assertEquals(testSet.get(k), t.get(k), "Element not removed is changed.");
        }

    }

    private void containsKeyHelper(Map<Iterable<Object>, Object> testSet, boolean testRemove) {
        IDictionary<NGram, Object> t = newDictionary();
        List<NGram> keys = new ArrayList<>();

        Random rand = new Random(2019101010);

        Set<NGram> disallowKey = new HashSet<>();

        // If this test does not test removal, then we need to disallow some keys from being
        // added to test containsKey returning false.
        for (Map.Entry<Iterable<Object>, Object> e : testSet.entrySet()) {
            if (rand.nextDouble() > 0.3) {
                continue;
            }

            NGram kList = iterableToNGram(e.getKey());
            disallowKey.add(kList);
        }

        for (Map.Entry<Iterable<Object>, Object> e : testSet.entrySet()) {
            // If we are testing remove, these keys will be removed, instead of not added.
            if (disallowKey.contains(e.getKey()) && !testRemove) {
                continue;
            }

            t.put(iterableToNGram(e.getKey()), e.getValue());
            keys.add(iterableToNGram(e.getKey()));
        }

        for (NGram k : keys) {
            assertTimeout(Duration.ofMillis(SINGLE_OP_TIMEOUT_MS()), () -> {
                assertTrue(t.containsKey(k), "Running containsKey on added key returns false.");
            });
        }

        if (!testRemove) {
            for (NGram k : disallowKey) {
                assertTimeout(Duration.ofMillis(SINGLE_OP_TIMEOUT_MS()), () -> {
                    assertFalse(t.containsKey(k),
                            "Running containsKey on key that hasn't been added returns true.");
                });
            }
            return;
        }

        for (NGram k : disallowKey) {
            t.remove(k);
            assertTimeout(Duration.ofMillis(SINGLE_OP_TIMEOUT_MS()), () -> {
                assertFalse(t.containsKey(k),
                        "Key that has been removed still returns true from containsKey.");
            });
        }
    }

    @Order(1)
    @DisplayName("Test containsKey(K key) -- add")
    @ParameterizedTest(name = "Test containsKey (add) on {0}.")
    @MethodSource("testSetData")
    void containsKeyAdd(Map<Iterable<Object>, Object> testSet) {
        containsKeyHelper(testSet, false);
    }

    @Order(1)
    @DisplayName("Test containsKey(K key) -- add and remove")
    @ParameterizedTest(name = "Test containsKey (add & remove) on {0}.")
    @MethodSource("testSetData")
    void containsKeyAddRemove(Map<Iterable<Object>, Object> testSet) {
        containsKeyHelper(testSet, true);
    }

    private void testContainsValueHelper(Map<Iterable<Object>, Object> testSet, boolean testRemove) {
        IDictionary<Iterable<Object>, Object> t = newDictionary();
        final int num_tests = 200;

        Random rand = new Random(2019101010);

        Set<Object> disallowVal = new HashSet<>();
        List<Object> values = new ArrayList<>();
        List<Iterable<Object>> disallowKeys = new ArrayList<>();

        // If this test does not test removal, then we need to disallow some values from being
        // added to test containsValue returning false.
        for (Map.Entry<Iterable<Object>, Object> e : testSet.entrySet()) {
            if (rand.nextDouble() > 0.3) {
                continue;
            }

            Object val = e.getValue();
            disallowVal.add(val);
        }

        for (Map.Entry<Iterable<Object>, Object> e : testSet.entrySet()) {
            // If we are testing remove, these keys will be removed, instead of not added.
            if (disallowVal.contains(e.getValue())) {
                disallowKeys.add(e.getKey());

                if (!testRemove) {
                    continue;
                }
            }

            t.put(e.getKey(), e.getValue());
            values.add(e.getValue());
        }

        int tests = 0;

        for (Object v : values) {
            if (tests > num_tests) break;

            assertTimeout(Duration.ofMillis(CONTAINS_VALUE_TIMEOUT_MS()), () -> {
                assertTrue(t.containsValue(v), "Running containsValue on added value returns false.");
            });

            tests++;
        }


        if (!testRemove) {
            tests = 0;

            for (Object v : disallowVal) {
                if (tests > num_tests) break;

                assertTimeout(Duration.ofMillis(CONTAINS_VALUE_TIMEOUT_MS()), () -> {
                    assertFalse(t.containsValue(v),
                            "Running containsValue on value that hasn't been added returns true.");
                });

                tests++;
            }
            return;
        }

        // Remove keys corresponding to values that need to be removed.
        for (Iterable<Object> k : disallowKeys) {
            t.remove(k);
        }

        tests = 0;

        for (Object v : disallowVal) {
            if (tests > num_tests) break;

            assertTimeout(Duration.ofMillis(CONTAINS_VALUE_TIMEOUT_MS()), () -> {
                assertFalse(t.containsValue(v),
                        "Value that has been removed still returns true from containsValue.");
            });

            tests++;
        }
    }

    @Order(1)
    @DisplayName("Test containsValue(V value) -- add and remove")
    @ParameterizedTest(name = "Test containsValue (add & remove) on {0}.")
    @MethodSource("testSetData")
    void testContainsValueAddRemove(Map<Iterable<Object>, Object> testSet) {
        testContainsValueHelper(testSet, true);
    }

    @Order(1)
    @DisplayName("Test containsValue(V value) -- add")
    @ParameterizedTest(name = "Test containsValue (add) on {0}.")
    @MethodSource("testSetData")
    void testContainsKeyAdd(Map<Iterable<Object>, Object> testSet) {
        testContainsValueHelper(testSet, false);
    }

    private IDictionary<Iterable<Object>, Object> createPrefixRestrictedTrie(Map<Iterable<Object>, Object> testSet,
                                                                                 Set<List<Object>> disallowPrefix, List<List<Object>> keys, boolean testRemove) {
        IDictionary<Iterable<Object>, Object> t = newDictionary();
        Random rand = new Random(2019101010);

        for (Map.Entry<Iterable<Object>, Object> e : testSet.entrySet()) {
            if (rand.nextDouble() < 0.1) {
                List<Object> kList = iterableToList(e.getKey());

                if (kList.size() > 5) {
                    int l = 5 + rand.nextInt(kList.size() - 5);
                    disallowPrefix.add(kList.subList(0, l));
                }
            }
        }

        // Add all non-disallowPrefix elements to t.
        for (Map.Entry<Iterable<Object>, Object> e: testSet.entrySet()) {
            List<Object> kList = iterableToList(e.getKey());
            boolean shouldBreak = false;

            for (int i = 0; i <= kList.size(); i++) {
                // If we are testing remove, these values will be removed.
                if (disallowPrefix.contains(kList.subList(0, i)) && !testRemove) {
                    shouldBreak = true;
                }
            }

            if (shouldBreak) {
                continue;
            }

            t.put(e.getKey(), e.getValue());
            keys.add(iterableToList(e.getKey()));
        }

        return t;
    }

    private NGram iterableToNGram(Iterable<Object> iter) {
        IDeque<String> keyList = new LinkedDeque<>();
        for (Object kp : iter) {
            keyList.addBack(kp.toString());
        }

        return new NGram(keyList);
    }

    private List<Object> iterableToList(Iterable<Object> iter) {
        List<Object> keyList = new ArrayList<>();
        for (Object kp : iter) {
            keyList.add(kp);
        }

        return keyList;
    }

    @Order(1)
    @DisplayName("Test iterator()")
    @ParameterizedTest(name = "Test iterator on {0}.")
    @MethodSource("testSetData")
    void iterator(Map<Iterable<Object>, Object> testSet) {
        IDictionary<Iterable<Object>, Object> t = newDictionary();
        List<Object> expected = new ArrayList<>();

        for (Map.Entry<Iterable<Object>, Object> e: testSet.entrySet()) {
            t.put(e.getKey(), e.getValue());
            expected.add(iterableToList(e.getKey()));
        }

        List<Object> keys = new ArrayList<>();
        for (Iterable<Object> key: t) {
            keys.add(iterableToList(key));
        }

        MatcherAssert.assertThat(keys,
                IsIterableContainingInAnyOrder.containsInAnyOrder(expected.toArray()));

    }

    @Order(1)
    @DisplayName("Test values()")
    @ParameterizedTest(name = "Test values on {0}.")
    @MethodSource("testSetData")
    void valuesCollection(Map<Iterable<Object>, Object> testSet) {
        IDictionary<Iterable<Object>, Object> t = newDictionary();
        List<Object> expected = new ArrayList<>();

        for (Map.Entry<Iterable<Object>, Object> e: testSet.entrySet()) {
            t.put(e.getKey(), e.getValue());
            expected.add(e.getValue());
        }

        List<Object> values = new ArrayList<>();
        for (Object value: t.values()) {
            values.add(value);
        }

        MatcherAssert.assertThat(values,
                IsIterableContainingInAnyOrder.containsInAnyOrder(expected.toArray()));

    }

    private void testSizeHelper(Map<Iterable<Object>, Object> testSet, boolean testRemove) {
        Random rand = new Random(2019010310);

        IDictionary<Iterable<Object>, Object> t = newDictionary();

        // Test size with insertions.
        int size = 0;

        for (Map.Entry<Iterable<Object>, Object> e : testSet.entrySet()) {
            t.put(e.getKey(), e.getValue());
            size += 1;

            final int s = size;
            assertEquals(s, t.size(), "Adding elements does not appropriately change the size.");
        }


        List<Iterable<Object>> keys = new ArrayList<>();
        Set<Iterable<Object>> keySet = testSet.keySet();
        List<Iterable<Object>> removedArr = new ArrayList<>();

        keys.addAll(keySet);

        int tests = 0;

        for (Map.Entry<Iterable<Object>, Object> e : testSet.entrySet()) {
            if (tests > 100) break;

            t.put(e.getKey(), e.getValue());

            final int s = size;

            assertTimeout(Duration.ofMillis(SINGLE_OP_TIMEOUT_MS()), () -> {
                assertEquals(s, t.size(), "Adding elements that already exist changes the size.");
            });

            tests++;
        }

        if (!testRemove) {
            return;
        }

        // Test size with deletions
        int runs = (int) (keys.size() * 0.5);

        for (int i = 0; i < runs; i++) {
            int inx = rand.nextInt(keys.size());
            Iterable<Object> removed = keys.remove(inx);
            Object v = t.remove(removed);
            size -= 1;

            final int s = size;

            assertEquals(s, t.size(),
                    "Removing a key that exists does not decrease size appropriately.");
            removedArr.add(removed);
        }

        // Test remove elements not in the map
        for (Iterable<Object> k : removedArr) {
            t.remove(k);
            assertEquals(size, t.size(), "Removing a key that does not exist changes the size.");
        }
    }

    @Order(2)
    @DisplayName("Test containsKey() -- complexity")
    @Test
    void testContainsKey() {
        Function<Integer, IDictionary<Iterable<String>, Object>> provide = (Integer numElements) -> {
            IDictionary<Iterable<String>, Object> t = newDictionary();
            for (int i = 1; i < numElements - 4; i++) {
                t.put(new NGram(new String[]{"" + i}), 0);
            }
            return t;
        };

        Consumer<IDictionary<Iterable<String>, Object>> containsKey = (IDictionary<Iterable<String>, Object> t) -> {
            t.containsKey(new NGram(new String[]{"99999999"}));
        };
        RuntimeInstrumentation.assertAtMost("containsKey",
                getAndPutComplexity(), provide, containsKey, 8);
    }

    @Order(2)
    @DisplayName("Test get() -- complexity")
    @Test
    void testGetComplexity() {
        Function<Integer, IDictionary<Iterable<String>, Object>> provide = (Integer numElements) -> {
            IDictionary<Iterable<String>, Object> t = newDictionary();
            for (int i = 0; i < numElements - 4; i++) {
                t.put(new NGram(new String[]{"" + i}), 0);
            }
            return t;
        };

        Consumer<IDictionary<Iterable<String>, Object>> get = (IDictionary<Iterable<String>, Object> t) -> {
            t.get(new NGram(new String[]{"0"}));
        };
        RuntimeInstrumentation.assertAtMost("get",
                getAndPutComplexity(), provide, get, 8);
    }

    @Order(2)
    @DisplayName("Test put() -- complexity")
    @Test
    void testPutComplexity() {
        Function<Integer, IDictionary<Iterable<String>, Object>> provide = (Integer numElements) -> {
            IDictionary<Iterable<String>, Object> t = newDictionary();
            for (int i = 1; i < numElements - 4; i++) {
                t.put(new NGram(new String[]{"" + i}), 0);
            }
            return t;
        };

        Consumer<IDictionary<Iterable<String>, Object>> put = (IDictionary<Iterable<String>, Object> t) -> {
            t.put(new NGram(new String[]{"0"}), 0);
        };
        RuntimeInstrumentation.assertAtMost("put",
                RuntimeInstrumentation.ComplexityType.CONSTANT, provide, put, 8);
    }

    @Order(2)
    @DisplayName("Test size() -- complexity")
    @Test
    void testSizeComplexity() {
        Function<Integer, IDictionary<Iterable<String>, Object>> provide = (Integer numElements) -> {
            Map<Iterable<String>, Object> o = getBooleanTestPoints(numElements);
            IDictionary<Iterable<String>, Object> t = dictionaryFromMap(o);
            return t;
        };

        Consumer<IDictionary<Iterable<String>, Object>> size = (IDictionary<Iterable<String>, Object> t) -> t.size();
        RuntimeInstrumentation.assertAtMost("size",
                getAndPutComplexity(), provide, size, 8);
    }

    @Order(2)
    @DisplayName("Test size() -- add and remove")
    @ParameterizedTest(name = "Test size on {0}.")
    @MethodSource("testSetData")
    void testSizeAddRemove(Map<Iterable<Object>, Object> testSet) {
        testSizeHelper(testSet, true);
    }

    @Order(2)
    @DisplayName("Test size() -- add")
    @ParameterizedTest(name = "Test size on {0}.")
    @MethodSource("testSetData")
    void testSizeAdd(Map<Iterable<Object>, Object> testSet) {
        testSizeHelper(testSet, false);
    }

    @Order(0)
    @DisplayName("Does not use or import disallowed classes")
    @Test
    public void testForInvalidClasses() {
        List<String> regexps = List.of("java.util.(?!Iterator|function.Function)", "java.lang.reflect", "java.io");
        Inspection.assertNoImportsOf(STRING_SOURCE, regexps);
        Inspection.assertNoUsageOf(STRING_SOURCE, regexps);
    }
}