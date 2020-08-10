package i.am.whp.util;

import i.am.whp.config.SpringContextHolder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.ArrayList;
import java.util.List;

public class RedisUtil {

    /**
     * redisUtil.setIfAbsent 新加的带有超时的setIfAbsent 脚本
     */
    private static String newSetIfAbsentScriptStr = " if 1 == redis.call('setnx',KEYS[1],ARGV[1]) then" +
            " redis.call('expire',KEYS[1],ARGV[2])" +
            " return 1;" +
            " else" +
            " return 0;" +
            " end;";

    public static RedisScript<Boolean> newSetIfAbsentScript = new DefaultRedisScript<>(newSetIfAbsentScriptStr, Boolean.class);

    /**
     * @param seconds 超时时间，秒为单位
     * @Description: setIfAbsent升级版，加了超时时间
     * @return: boolean
     */
    public static boolean setIfAbsent(String key, String value, Long seconds) {
        List<Object> keys = new ArrayList<>();
        keys.add(key);
        Object[] args = {value, seconds.toString()};
        return (boolean) SpringContextHolder.getBean("redisTemplate", RedisTemplate.class).execute(newSetIfAbsentScript, keys, args);
    }
}
