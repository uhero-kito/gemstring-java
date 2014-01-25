/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gemstring;

import java.util.concurrent.ForkJoinPool;

/**
 *
 * @author hawk
 */
public class CounterTest {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: CounterTest gems target");
            return;
        }
        
        System.out.println("gems   : " + args[0]);
        System.out.println("target : " + args[1]);
        final String target = args[1];
        Counter c = new Counter(new PatternGenerator(args[0]), target);
        ForkJoinPool pool = new ForkJoinPool(32);
        long t1 = System.nanoTime();
        System.out.println(pool.invoke(c) + ":" + target);
        long t2 = System.nanoTime();
        System.out.println("Time: " + (t2 - t1));
    }
}
