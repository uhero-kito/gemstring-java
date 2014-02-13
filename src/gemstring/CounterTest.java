package gemstring;

import java.util.concurrent.ForkJoinPool;

/**
 * gemstring の実行クラスです.
 * @author hawk_snow
 */
public class CounterTest {
    /**
     * gemstring の問題を計算します.
     * 1 番目の引数に宝石の種類の一覧 ("abbbbcddddeefggg" など),
     * 2 番目の引数に目的のパターン ("eagcdfbe" など) を指定します.
     * 
     * @param args
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: CounterTest gems target");
            return;
        }
        
        final String gems   = args[0];
        final String target = args[1];
        System.out.println("gems   : " + gems);
        System.out.println("target : " + target);
        final Counter c = new Counter(new PatternGenerator(gems), target);
        final ForkJoinPool pool = new ForkJoinPool(32);
        final long t1 = System.nanoTime();
        System.out.println(pool.invoke(c) + ":" + target);
        final long t2 = System.nanoTime();
        System.out.println("Time: " + (t2 - t1));
    }
}
