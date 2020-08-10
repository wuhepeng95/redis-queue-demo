package i.am.whp.manager;

import i.am.whp.bean.SimpleBean;
import i.am.whp.util.RandomSuccessUtil;
import org.slf4j.MDC;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static i.am.whp.constants.CommonConstants.LOCK_ARRANGE_KEY;
import static i.am.whp.constants.CommonConstants.LOCK_TRY_TIMES;

@Component
public class CommonManager {

    @Resource
    RedisTemplate redisTemplate;

    private volatile Map<Long, Boolean> doneMap = new HashMap<>();

    public void doArrangeWithLock(SimpleBean simpleBean, Integer retryTimes) {
        String guid = MDC.get("GUID");

        // 双重判断 锁前判断
        Boolean isDone = doneMap.get(simpleBean.getArrangeCourseId());
        if (isDone != null && isDone) {
            System.out.println(guid + "---------------已经被处理成功(锁外)--------------" + simpleBean.getArrangeCourseId());
            return;
        }

        System.out.println(guid + "-尝试业务处理次数" + (LOCK_TRY_TIMES - retryTimes));

        // 30s超时（大于最长业务时间）,自动取消key
        boolean lockSuccess = redisTemplate.opsForValue().setIfAbsent(LOCK_ARRANGE_KEY + simpleBean.getArrangeCourseId().toString(), "doing", Duration.ofSeconds(30));

        if (lockSuccess) {
            // 锁后再判断是否已经被成功处理
            Boolean doneFlag = doneMap.get(simpleBean.getArrangeCourseId());
            if (doneFlag != null && doneFlag) {
                System.out.println(guid + "---------------已经被处理成功(锁内)--------------" + simpleBean.getArrangeCourseId());
                return;
            }
            System.out.println(guid + "---------------排课开始--------------" + simpleBean.getArrangeCourseId());
            List<Long> teacherIds = simpleBean.getTeacherIds();
            boolean successFlag = false;
            for (Long teacherId : teacherIds) {
                simpleBean.setTeacherIds(Collections.singletonList(teacherId));
                if (doArrangeSingle(simpleBean)) {
                    successFlag = true;
                    System.out.println(guid + "---------------排课结束(success)--------------");
                    break;
                }
            }
            if (!successFlag) {
                System.out.println(guid + "---------------排课结束(fail已尝试所有老师)--------------");
            }
            doneMap.put(simpleBean.getArrangeCourseId(), successFlag);
            redisTemplate.delete(LOCK_ARRANGE_KEY + simpleBean.getArrangeCourseId().toString());

        } else {
            // 失败之后先睡眠
            try {
                TimeUnit.MILLISECONDS.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (retryTimes == 0) {
                System.out.println(guid + "---------------排课结束(fail尝试获取锁失败)--------------");
                // 没有重新放回队列（失败机制）
                return;
            }
            doArrangeWithLock(simpleBean, retryTimes - 1);
        }
    }

    /**
     * 指定老师
     *
     * @param simpleBean
     */
    public boolean doArrangeSingle(SimpleBean simpleBean) {
        String guid = MDC.get("GUID");
        // step1 校验冲突
        try {
            boolean checkConfigResult = RandomSuccessUtil.getSuccessPercent(75);
            if (checkConfigResult) {
                System.out.println(guid + "-校验冲突成功");
            } else {
                System.out.println(guid + "-校验冲突失败");
                return false;
            }
        } catch (Exception e) {
            System.out.println(guid + "-校验冲突异常");
            return false;
        }

        // step2 预排
        try {
            boolean preArrangeResult = RandomSuccessUtil.getSuccessPercent(80);
            if (preArrangeResult) {
                System.out.println(guid + "-预排成功");
            } else {
                System.out.println(guid + "-预排失败");
                return false;
            }
        } catch (Exception e) {
            System.out.println(guid + "-预排异常");
            return false;
        }

        // step3 轮询预排结果
        try {
            boolean pollingPreArrangeResult = pollingPreArrangeResult();
            if (pollingPreArrangeResult) {
                System.out.println(guid + "-获取预排结果成功");
            } else {
                System.out.println(guid + "-获取预排结果失败");
                return false;
            }
        } catch (Exception e) {
            System.out.println(guid + "-获取预排结果异常");
            return false;
        }

        System.out.println(guid + "-正式排课");
        return true;
    }

    /**
     * 轮询预排结果
     *
     * @return
     */
    public boolean pollingPreArrangeResult() {
        String guid = MDC.get("GUID");
        long currentTimeMillis = System.currentTimeMillis();
        boolean flag = false;
        int time = 1;
        boolean notTimeOutFlag = true;
        // 5s中内尝试
        while (notTimeOutFlag = System.currentTimeMillis() - currentTimeMillis < 2 * 1000) {
            System.out.println(guid + "-查询预排结果尝试第" + time++ + "次");
            // 判断成功
            if (RandomSuccessUtil.getSuccessPercent(90)) {
                flag = true;
                break;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (!notTimeOutFlag) {
            System.out.println(guid + "-查询预排结果超时");
        }
        return flag;
    }

    public void doArrangeNonLock(SimpleBean simpleBean) {
        String guid = MDC.get("GUID");
        System.out.println(guid + "---------------排课开始--------------");
        List<Long> teacherIds = simpleBean.getTeacherIds();
        boolean successFlag = false;
        for (Long teacherId : teacherIds) {
            simpleBean.setTeacherIds(Collections.singletonList(teacherId));
            if (doArrangeSingle(simpleBean)) {
                successFlag = true;
                System.out.println(guid + "---------------排课结束(success)--------------");
                break;
            }
        }
        if (!successFlag) {
            System.out.println(guid + "---------------排课结束(fail已尝试所有老师)--------------");
        }
    }

}
