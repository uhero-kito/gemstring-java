package gemstring;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * とあるパターンから, その次のパターンを算出するクラスです.
 * @author hawk_snow
 */
class PatternGenerator {
    /**
     * 宝石の一覧です.
     * キーが宝石の種類 (小文字アルファベット), 値がその宝石の個数となります.
     */
    private final SortedMap<Character, Integer> materials;
    
    /**
     * 全宝石の個数です. コンストラクタの文字列の長さになります.
     */
    private int materialCount;
    
    /**
     * 指定された宝石の一覧を持つ PatternGenerator を構築します.
     * @param seq 使用される宝石の一覧. "abbbbcddddeefggg" など.
     */
    PatternGenerator(String seq) {
        if (seq == null || seq.length() == 0) {
            throw new IllegalArgumentException("Sequence is empty");
        }
        
        this.materials = new TreeMap<>();
        final int length = seq.length();
        for (int i = 0; i < length; i++) {
            final char c    = seq.charAt(i);
            final int count = this.materials.containsKey(c) ? this.materials.get(c) + 1 : 1;
            this.materials.put(c, count);
        }
        this.materialCount = length;
    }
    
    /**
     * 指定されたパターンの次のパターンを計算します.
     * @param seq ある時点のパターン
     * @return 次のパターン
     * @throws NoMoreSequenceException 次のパターンが存在しない場合
     */
    String next(String seq) throws NoMoreSequenceException {
        return this.next(seq, ' ');
    }
    
    /**
     * 次のパターンを計算します.
     * もしも残りの宝石がある場合は, 残りの宝石のうち一番小さい種類 (文字) を末尾に繋げた結果を返します.
     * 
     * 残りの宝石がない (すべての宝石を使った) パターンの場合は繰り上がり処理を行います.
     * 例えば "aaadecb" というパターンの場合, 繰り上がり対象の文字は 4 文字目の 'd' となるので
     * 再帰的に next("aaa", 'd') を呼び出してその結果を返します. (この場合, 結果は "aaae" になります)
     *  
     * @param seq
     * @param pass 繰り上がる桁の文字 (繰り上がりを行わない場合は ' ')
     * @return 次のパターン
     * @throws NoMoreSequenceException
     */
    private String next(String seq, char pass) throws NoMoreSequenceException {
        final SortedMap<Character, Integer> rest = this.getRest(seq);
        for (Map.Entry<Character, Integer> e : rest.entrySet()) {
            final char c = e.getKey();
            final int count = e.getValue();
            if (pass < c && 0 < count) {
                return seq + c;
            }
        }
        final int index = this.getLastIndex(seq);
        return this.next(seq.substring(0, index), seq.charAt(index));
    }
    
    /**
     * 繰り上がりが行われる桁のインデックスを計算します.
     * 文字列の末尾から一文字ずつ舐めていき, 直後の文字よりも小さい文字になった箇所のインデックスを返します.
     * 例えば "aaadecb" というパターンの場合, 'd' の桁が繰り上がり対象のインデックスとなるので 3 を返します.
     * ('d' < 'e' となるため)
     * 該当のインデックスが見つからない場合 ("ccbaaa" など) は, 次のパターンが計算できないことを意味するので
     * NoMoreSequenceException をスローします.
     * 
     * @param seq パターン
     * @return 繰り上がり対象のインデックス
     * @throws NoMoreSequenceException 次のパターンが存在しない場合
     */
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
    
    /**
     * 全部の宝石から, 指定されたパターンの宝石を除いた結果, どの種類がいくつ残るかを数えます.
     * @param seq パターン
     * @return 残りの宝石 (キーが宝石の種類, 値がその宝石の個数)
     */
    SortedMap<Character, Integer> getRest(String seq) {
        final SortedMap<Character, Integer> rest = new TreeMap<>(this.materials);
        final int length = seq.length();
        for (int i = 0; i < length; i++) {
            final char c = seq.charAt(i);
            final Integer count = rest.get(c);
            if (count == null || count == 0) {
                throw new IllegalArgumentException("Invalid sequence: " + seq);
            }
            rest.put(c, count - 1);
        }
        return rest;
    }
    
    /**
     * 全宝石の個数を返します.
     * @return 全種類の宝石の個数を足しあわせた整数
     */
    int getMaterialCount() {
        return this.materialCount;
    }
}
