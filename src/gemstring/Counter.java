/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gemstring;

import java.util.Map;
import java.util.concurrent.RecursiveTask;

/**
 *
 * @author hawk
 */
class Counter extends RecursiveTask <Integer> {
    private final String           start;
    private final String           goal;
    private final PatternGenerator gen;
    
    Counter(PatternGenerator gen, String goal) {
        this(gen, "", goal);
    }
    
    Counter(PatternGenerator gen, String start, String goal) {
        this.gen   = gen;
        this.start = start;
        this.goal  = goal;
    }
    
    @Override
    protected Integer compute() {
        final String sub = this.getSubSequence(this.start);
        if (sub != null) {
            System.out.println("'" + this.start + "' => '" + sub + "' => '" + this.goal + "'");
            final Counter first  = new Counter(this.gen, this.start, sub);
            first.fork();
            final Counter second = new Counter(this.gen, sub, this.goal);
            second.fork();
            final int result = first.join() + second.join();
            this.printResult(result);
            return result;
        }
        
        String seq = this.start;
        try {
            for (int i = 1; true; i++) {
                final String next = this.gen.next(seq);
                if (next.equals(goal)) {
                    this.printResult(i);
                    return i;
                }
                seq = next;
            }
        } catch (NoMoreSequenceException e) {
            throw new IllegalArgumentException("Target '" + this.goal + "' is not reachable.");
        }
    }
    
    private String getSubSequence(String start) {
        return this.getSubSequence(start, false);
    }
    
    private String getSubSequence(String start, boolean skipped) {
        final int    length = start.length();
        if (this.gen.getLength() - 8 < length) {
            return null;
        }
        if (length == 0) {
            return this.goal.equals("a") ? null : "a";
        }
        
        final Map<Character, Long> rest = this.gen.getRest(start);
        final int    index  = length - 1;
        final String prefix = start.substring(0, index);
        final char   from   = start.charAt(index);
        if (index < this.goal.length()) {
            final char dest = this.goal.charAt(index);
            while (from < dest) {
                final char nextGem = (char) (from + 1);
                if (rest.get(nextGem) == 0) {
                    continue;
                }
                final String candidate = prefix + (char) (from + 1);
                if (!candidate.equals(this.goal)) {
                    return candidate;
                }
            }
        }
        
        for (Map.Entry<Character, Long> entry : rest.entrySet()) {
            char c     = entry.getKey();
            long count = entry.getValue();
            if (count == 0) {
                continue;
            }
            final String candidate = start + c;
            if (candidate.equals(this.goal)) {
                continue;
            }
            if (0 < candidate.compareTo(this.goal)) {
                continue;
            }
            
            return skipped ? candidate : this.getSubSequence(candidate, true);
        }
        
        return null;
    }
    
    private void printResult(int i) {
        if (1 < i) {
            System.out.println("count: " + i + " by '" + this.start + "' => '" + this.goal + "'");
        }
    }
}
