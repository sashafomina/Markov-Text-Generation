package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.textgenerator.NGram;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class NGramTests {
    private static NGram stringToNGram(String s) {
        String[] sa = new String[s.length()];
        for (int i = 0; i < s.length(); i++) {
            sa[i] = "" + s.charAt(i);
        }
        return new NGram(sa);
    }

    private static int compareStrings(String a, String b) {
        return Integer.signum(stringToNGram(a).compareTo(stringToNGram(b)));
    }

     private static boolean equalsStrings(String a, String b) {
        return stringToNGram(a).equals(stringToNGram(b));
    }

    @Order(0)
    @Tag("A")
    @DisplayName("The NGram compareTo method functions correctly")
    @ParameterizedTest(name = "{0}.compareTo({1}) == {2}")
    @CsvSource({
        "a, a, 0",
        "a, aa, -1",
        "a, aaa, -1",
        "a, aaaa, -1",
        "aa, a, 1",
        "aa, aa, 0",
        "aa, aaa, -1",
        "aa, aaaa, -1",
        "aaa, a, 1",
        "aaa, aa, 1",
        "aaa, aaa, 0",
        "aaa, aaaa, -1",
        "aaaa, a, 1",
        "aaaa, aa, 1",
        "aaaa, aaa, 1",
        "aaaa, aaaa, 0",
        "abc, abc, 0",
        "abc, acb, -1",
        "abc, bac, -1",
        "abc, bca, -1",
        "abc, cab, -1",
        "abc, cba, -1",
        "acb, abc, 1",
        "acb, acb, 0",
        "acb, bac, -1",
        "acb, bca, -1",
        "acb, cab, -1",
        "acb, cba, -1",
        "bac, abc, 1",
        "bac, acb, 1",
        "bac, bac, 0",
        "bac, bca, -1",
        "bac, cab, -1",
        "bac, cba, -1",
        "bca, abc, 1",
        "bca, acb, 1",
        "bca, bac, 1",
        "bca, bca, 0",
        "bca, cab, -1",
        "bca, cba, -1",
        "cab, abc, 1",
        "cab, acb, 1",
        "cab, bac, 1",
        "cab, bca, 1",
        "cab, cab, 0",
        "cab, cba, -1",
        "cba, abc, 1",
        "cba, acb, 1",
        "cba, bac, 1",
        "cba, bca, 1",
        "cba, cab, 1",
        "cba, cba, 0"
    })
    public void testCompareTo(String A, String B, int expected) {
        assertEquals(expected, compareStrings(A, B));
    }

    @Tag("C")
    @DisplayName("The NGram equals method functions correctly")
    @ParameterizedTest(name = "{0}.equals({1}) == {2}")
    @CsvSource({
        "a, a, true",
        "a, aa, false",
        "a, aaa, false",
        "a, aaaa, false",
        "aa, a, false",
        "aa, aa, true",
        "aa, aaa, false",
        "aa, aaaa, false",
        "aaa, a, false",
        "aaa, aa, false",
        "aaa, aaa, true",
        "aaa, aaaa, false",
        "aaaa, a, false",
        "aaaa, aa, false",
        "aaaa, aaa, false",
        "aaaa, aaaa, true",
        "abc, abc, true",
        "abc, acb, false",
        "abc, bac, false",
        "abc, bca, false",
        "abc, cab, false",
        "abc, cba, false",
        "acb, abc, false",
        "acb, acb, true",
        "acb, bac, false",
        "acb, bca, false",
        "acb, cab, false",
        "acb, cba, false",
        "bac, abc, false",
        "bac, acb, false",
        "bac, bac, true",
        "bac, bca, false",
        "bac, cab, false",
        "bac, cba, false",
        "bca, abc, false",
        "bca, acb, false",
        "bca, bac, false",
        "bca, bca, true",
        "bca, cab, false",
        "bca, cba, false",
        "cab, abc, false",
        "cab, acb, false",
        "cab, bac, false",
        "cab, bca, false",
        "cab, cab, true",
        "cab, cba, false",
        "cba, abc, false",
        "cba, acb, false",
        "cba, bac, false",
        "cba, bca, false",
        "cba, cab, false",
        "cba, cba, true"
    })
    public void testEquals(String A, String B, boolean expected) {
        assertEquals(expected, equalsStrings(A, B));

    }

    public static int[] generateHashCodes(int n) {
        int[] hashCodes = new int[n];

        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = i * 3700001;
            if(i > 0)
                arr[i] += arr[i - 1];
        }

        boolean allZero = true;
        for (int i = 0; i < n; i++) {
            NGram ngram = stringToNGram(Integer.toString(arr[i], 36));
            int hashCode = ngram.hashCode();
            hashCodes[i] = hashCode;
            if(allZero && hashCode != 0)
                allZero = false;
        }

        return hashCodes;
    }

    @Test
    @Tag("C")
    @DisplayName("The NGram hashCode method distributes simple inputs reasonably")
    public void hashOverlap() {
        // Their hash function shouldn't overlap with these simple inputs
        NGram[] diff_length = {
                stringToNGram("a"),
                stringToNGram("aa"),
                stringToNGram("aaa"),
                stringToNGram("aaaa")};
        NGram[] permutations = {
                stringToNGram("abc"),
                stringToNGram("acb"),
                stringToNGram("bac"),
                stringToNGram("bca"),
                stringToNGram("cab"),
                stringToNGram("cba")};

        for(NGram A : diff_length) {
            for(NGram B : diff_length) {
                assertFalse(A.equals(B) && A.hashCode() != B.hashCode(), "The hashCode function returns different values for two equal NGrams");
                assertFalse(!A.equals(B) && A.hashCode() == B.hashCode(), "The hashCode function returns the same hashCode for (simple) non-equal NGrams");
            }
        }
        for(NGram A : permutations) {
            for(NGram B : permutations) {
                assertFalse(A.equals(B) && A.hashCode() != B.hashCode(), "The hashCode function returns different values for two equal NGrams");
                assertFalse(!A.equals(B) && A.hashCode() == B.hashCode(), "The hashCode function returns the same hashCode for (simple) non-equal NGrams");
            }
        }
    }

    @Test
    @Tag("C")
    @DisplayName("The NGram hashCode method yields high variance on simple inputs")
    public void testHighVariance() {
        int n = 1000;
        int[] hashCodes = generateHashCodes(n);

        // Compute the variance of the hashCodes and make sure it's above a threshold
        double mean = 0.0;
        double var = 0.0;
        double std = 0.0;

        for (int hashCode : hashCodes ) {
            mean += (double) hashCode / n;
        }
        for (int hashCode : hashCodes ) {
            var += (double) hashCode * hashCode / n;
        }
        var -= mean * mean;
        std = Math.sqrt(var);

        assertTrue(std > 1e7, "The standard deviation of simple hashcodes is too small");
    }


}
