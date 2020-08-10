package i.am.whp.handler;

import com.alibaba.fastjson.JSON;
import i.am.whp.manager.CommonManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static i.am.whp.constants.CommonConstants.POP_QUEUE_THREAD_COUNT;
import static i.am.whp.constants.CommonConstants.REDIS_QUEUE_KEY;

/**
 * redis 队列监听
 *
 * @author wuhepeng
 */
@Component
public class RedisQueueConsumerHandler {

    public static final Logger logger = LoggerFactory.getLogger(RedisQueueConsumerHandler.class);
    public static final Integer threadCount = POP_QUEUE_THREAD_COUNT;

    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private CommonManager commonManager;

    public static ThreadPoolExecutor treadPool = new ThreadPoolExecutor(4, 10, 5,
            TimeUnit.SECONDS, new LinkedBlockingQueue(200), new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            logger.error("redis queue consumer out of blockingqueue");
            // 打印出拒绝的日志 调整好队列大小 这种情况很难出现
            System.out.println(((MyRunnable) r).getParam());
        }
    });

    @PostConstruct
    public void start() {
        logger.info("redis queue consumer handler starting");
        // 开启一个线程监听 把队列中取出的任务放入线程池
        new Thread(() -> {
            while (true) {
                // 阻塞式取值避免过多的while端
                // rightPop(key, blockSeconds, TimeUnit.SECONDS)
                Object o = redisTemplate.opsForList().rightPop(REDIS_QUEUE_KEY, 5, TimeUnit.SECONDS);
                if (o != null) {
                    logger.info(Thread.currentThread().getName() + "right pop success of testQueue" + "\t" + JSON.toJSONString(o));
                    treadPool.execute(new MyRunnable(o, commonManager));
                } else {
                    System.out.println("队列为空");
                }
            }
        }).start();
    }

//    @PostConstruct
//    public void start() {
//        for (int i = 0; i < threadCount; i++) {
//            treadPool.execute(new Runnable() {
//                @Override
//                public void run() {
//                    logger.info("redis queue consumer handler starting");
//                    while (true) {
//                        // 如果队列取出不为空
////                        rightPop(key, blockSeconds, TimeUnit.SECONDS)
//                        Object o = redisTemplate.opsForList().rightPop(REDIS_QUEUE_KEY, 5, TimeUnit.SECONDS);
//                        if (o != null) {
//                            logger.info(Thread.currentThread().getName() + "right pop success of testQueue" + "\t" + JSON.toJSONString(o));
//                            MDC.put("GUID", UUID.randomUUID().toString() + Thread.currentThread().getName() + "-" + "threadPool thread count : " + treadPool.getActiveCount());
//                            commonManager.doArrangeWithLock((SimpleBean) o, LOCK_TRY_TIMES);
//                        } else {
//                            // 休眠一秒 防止机器抗不住
//                            try {
//                                TimeUnit.SECONDS.sleep(1);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            System.out.println("队列空了");

//                        }
//                    }
//                }
//            });
//        }
//    }
}
