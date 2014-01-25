/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gemstring;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.List;

/**
 *
 * @author hawk
 */
public class Main {
    public static void main(String[] args) throws IOException {
        final PatternGenerator p = new PatternGenerator("aaabcc");
        String seq = "";
        try {
            final List<String> expected = Files.readAllLines(Paths.get("resources", "minilist.txt"), Charset.defaultCharset());
            int i = 1;
            for (String line : expected) {
                final String next   = p.next(seq);
                final String output = i + ":" + next;
                System.out.println(output);
                if (!line.equals(output)) {
                    System.out.println("Not matched (Expected => " + line + ")");
                    break;
                }
                seq = next;
                i++;
            }
        } catch (NoMoreSequenceException e) {
            System.out.println("Finished");
        }
    }
}
