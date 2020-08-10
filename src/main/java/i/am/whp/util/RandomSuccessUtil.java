package i.am.whp.util;

import java.util.Random;

/**
 * @author wuhepeng
 */
public class RandomSuccessUtil {
    public static final Random random = new Random(100);

    public static boolean getSuccessPercent(int probability) {
        return random.nextInt() < probability;
    }
}
