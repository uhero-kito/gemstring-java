/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gemstring;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author hawk
 */
class PatternGenerator {
    private final SortedMap<Character, Long> materials;
    private int   length;

    PatternGenerator(String seq) {
        if (seq == null || seq.length() == 0) {
            throw new IllegalArgumentException("Sequence is empty");
        }
        
        this.materials = new TreeMap<>();
        final int length = seq.length();
        for (int i = 0; i < length; i++) {
            final char c     = seq.charAt(i);
            final long count = this.materials.containsKey(c) ? this.materials.get(c) + 1 : 1;
            this.materials.put(c, count);
        }
        this.length = seq.length();
    }
    
    String next(String seq) throws NoMoreSequenceException {
        return this.next(seq, ' ');
    }
    
    private String next(String seq, char pass) throws NoMoreSequenceException {
        final SortedMap<Character, Long> rest = this.getRest(seq);
        for (Map.Entry<Character, Long> e : rest.entrySet()) {
            final char c = e.getKey();
            final long count = e.getValue();
            if (pass < c && 0 < count) {
                return seq + c;
            }
        }
        final int index = this.getLastIndex(seq);
        return this.next(seq.substring(0, index), seq.charAt(index));
    }
    
    private int getLastIndex(String seq) throws NoMoreSequenceException {
        final int length = seq.length();
        char max = ' ';
        for (int i = length - 1; i >= 0; i--) {
            final char c = seq.charAt(i);
            if (c < max) {
                return i;
            }
            max = c;
        }
        
        throw new NoMoreSequenceException();
    }
    
    SortedMap<Character, Long> getRest(String seq) {
        final SortedMap<Character, Long> rest = new TreeMap<>(this.materials);
        final int length = seq.length();
        for (int i = 0; i < length; i++) {
            final char c = seq.charAt(i);
            final Long count = rest.get(c);
            if (count == null || count == 0) {
                throw new IllegalArgumentException("Invalid sequence: " + seq);
            }
            rest.put(c, count - 1);
        }
        return rest;
    }
    
    int getLength() {
        return this.length;
    }
}
