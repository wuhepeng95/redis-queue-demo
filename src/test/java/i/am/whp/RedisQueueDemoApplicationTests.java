package i.am.whp;

import com.alibaba.fastjson.JSON;
import i.am.whp.bean.SimpleBean;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class RedisQueueDemoApplicationTests {
    @Test
    void javaTest() {
        SimpleBean simpleBean = new SimpleBean();
        simpleBean.setArrangeCourseId(12L);
        simpleBean.setStudentId(11111L);
//        simpleBean.setTimeRange(TimeRange.builder().startTime(new Date()).endTime(new Date()).build());
        simpleBean.setTeacherIds(Arrays.asList(112L, 113L, 12313L));
        System.out.println(JSON.toJSONString(simpleBean));
    }

}
