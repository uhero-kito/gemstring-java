package gemstring;

import java.util.Map;
import java.util.concurrent.RecursiveTask;

/**
 * とあるパターンから, 別のパターンに至るまでに何パターン存在するかを数えるクラスです.
 * @author hawk_snow
 */
class Counter extends RecursiveTask <Long> {
    private static final int NO_FORK_COUNT = 10;
    private final String           start;
    private final String           goal;
    private final PatternGenerator gen;
    
    /**
     * ゴールを指定して Counter をインスタンス化します.
     * @param gen
     * @param goal
     */
    Counter(PatternGenerator gen, String goal) {
        this(gen, "", goal);
    }
    
    /**
     * スタートとゴールを指定して Counter をインスタンス化します.
     * @param gen
     * @param start
     * @param goal
     */
    Counter(PatternGenerator gen, String start, String goal) {
        this.gen   = gen;
        this.start = start;
        this.goal  = goal;
    }
    
    /**
     * スタートからゴールに至るまでのパターン数 (日数) を計算します.
     * スタートとゴールの間が遠い場合は中間地点を設定し,
     * 「スタート → 中間地点」と「中間地点 → ゴール」に分割してその結果の合計を返します.
     * スタートとゴールが近い場合は普通に計算します.
     * 
     * @return パターン数
     */
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
    
    /**
     * 分割統治法で計算するにあたり, スタートとゴールの間の中間地点となるパターンを算出します.
     * 分割して計算する必要がない (計算量が小さい) 場合は null を返します.
     * @param start スタート地点
     * @return 中間地点のパターン. 分割する必要がない場合は null
     */
    private String getSubSequence(String start) {
        final int length    = start.length();
        final int threshold = this.gen.getMaterialCount() - NO_FORK_COUNT;
        if (threshold < length) {
            return null;
        }
        if (length == 0) {
            return this.goal.equals("a") ? null : "a";
        }
        
        final Map<Character, Integer> rest = this.gen.getRest(start);
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
        
        for (Map.Entry<Character, Integer> entry : rest.entrySet()) {
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
