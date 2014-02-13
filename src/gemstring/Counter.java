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
class Counter extends RecursiveTask <Long> {
    private static final int NO_FORK_COUNT = 10;
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
    protected Long compute() {
        final String sub = this.getSubSequence(this.start);
        if (sub != null) {
            final Counter first  = new Counter(this.gen, this.start, sub);
            first.fork();
            final Counter second = new Counter(this.gen, sub, this.goal);
            second.fork();
            final long result = first.join() + second.join();
            this.printResult(result);
            return result;
        }
        
        String seq = this.start;
        try {
            for (long i = 1; true; i++) {
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
        final int length    = start.length();
        final int threshold = this.gen.getMaterialCount() - NO_FORK_COUNT;
        if (threshold < length) {
            return null;
        }
        if (length == 0) {
            return this.goal.equals("a") ? null : "a";
        }
        
        final Map<Character, Long> rest = this.gen.getRest(start);
        final int    index  = length - 1;
        final String prefix = start.substring(0, index);
        final char   from   = start.charAt(index);
        for (char nextGem = (char) (from + 1); rest.containsKey(nextGem); nextGem = (char) (nextGem + 1)) {
            if (rest.get(nextGem) == 0) {
                continue;
            }
            
            final String candidate = prefix + nextGem;
            if (candidate.compareTo(this.goal) < 0) {
                return candidate;
            } else {
                break;
            }
        }
        
        for (Map.Entry<Character, Long> entry : rest.entrySet()) {
            char c     = entry.getKey();
            long count = entry.getValue();
            if (count == 0) {
                continue;
            }
            final String candidate = start + c;
            if (0 <= candidate.compareTo(this.goal)) {
                break;
            }
            return this.getSubSequence(candidate);
        }
        
        return null;
    }
    
    private void printResult(long i) {
        System.out.println(this.start + "," + this.goal + "," + i);
    }
    
    @Override
    public String toString() {
        return "['" + this.start + "', '" + this.goal + "']";
    }
}
