package edu.caltech.cs2.textgenerator;

import edu.caltech.cs2.datastructures.LinkedDeque;
import edu.caltech.cs2.interfaces.IDeque;

import java.util.Iterator;

public class NGram implements Iterable<String>, Comparable<NGram> {
    public static final String NO_SPACE_BEFORE = ",?!.-,:'";
    public static final String NO_SPACE_AFTER = "-'><=";
    public static final String REGEX_TO_FILTER = "”|\"|“|\\(|\\)|\\*";
    public static final String DELIMITER = "\\s+|\\s*\\b\\s*";
    private IDeque<String> data;

    public static String normalize(String s) {
       return s.replaceAll(REGEX_TO_FILTER, "").strip();
    }

    public NGram(IDeque<String> x) {
        this.data = new LinkedDeque<>();
        for (int i = 0; i < x.size(); i++) {
            this.data.addBack(x.peekFront());
            x.addBack(x.removeFront());
        }
    }

    public NGram(String data) {
        this(normalize(data).split(DELIMITER));
    }

    public NGram(String[] data) {
        this.data = new LinkedDeque<>();
        for (String s : data) {
            s = normalize(s);
            if (!s.isEmpty()) {
                this.data.addBack(s);
            }
        }
    }

    public NGram next(String word) {
        String[] data = new String[this.data.size()];
        for (int i = 0; i < data.length - 1; i++) {
            this.data.addBack(this.data.removeFront());
            data[i] = this.data.peekFront();
        }
        this.data.addBack(this.data.removeFront());
        data[data.length - 1] = word;
        return new NGram(data);
     }

    public String toString() {
        String result = "";
        String prev = "";
        for (String s : this.data) {
            result += ((NO_SPACE_AFTER.contains(prev) || NO_SPACE_BEFORE.contains(s) || result.isEmpty()) ?  "" : " ") + s;
            prev = s;
        }
        return result.strip();
    }

    @Override
    public Iterator<String> iterator() {
        return this.data.iterator();
    }


    @Override
    public int compareTo(NGram other) {
        if (other.data.size() < this.data.size()){
            return 1;
        }
        else if (other.data.size() > this.data.size()){
            return -1;
        }
        else {
            Iterator<String> dataItr = data.iterator();
            for (String otherStr: other){
                String thisStr = dataItr.next();
                if (thisStr.compareTo(otherStr) < 0){
                    return -1;
                }
                else if (thisStr.compareTo(otherStr) > 0){
                    return 1;
                }
            }
            return 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof NGram) {
            NGram e = (NGram) o;
            //Iterator<String> eItr = e.iterator();
            Iterator<String> dataItr = data.iterator();
            if (e.data.size() != data.size()) {
                return false;
            }
            for (String s : e){
               if (!dataItr.next().equals((s))){
                   return false;
               }
            }
//            while (dataItr.hasNext() && eItr.hasNext()) {
//                if (!dataItr.next().equals(eItr.next())) {
//                    return false;
//                }
//            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int sum = 0;
        IDeque<String> temp = new LinkedDeque<>();
        for (String s: this.data){
            temp.add(s);
        }
        for (int i = 0; i < this.data.size(); i++){
            int mult = 1;
            mult = (int) Math.pow(37, i);
            //for (int j = 0; j < i; j++){
            //    mult *= 37;
            //}
            sum += mult * temp.peekFront().hashCode();
            temp.removeFront();
        }
        return sum;
    }
}