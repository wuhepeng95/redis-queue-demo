package i.am.whp.constants;

/**
 * @author wuhepeng
 */
public class CommonConstants {
    public static final String REDIS_QUEUE_KEY = "qingqing:wuhepeng:test_queue";
    public static final String LOCK_ARRANGE_KEY = "qingqing:wuhepeng:lock_key";

    public static final Integer LOCK_TRY_TIMES = 3;
    public static final Integer POP_QUEUE_THREAD_COUNT = 3;
}
