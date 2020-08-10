package i.am.whp.controller;

import i.am.whp.bean.SimpleBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static i.am.whp.constants.CommonConstants.REDIS_QUEUE_KEY;

@Controller
@RequestMapping("/send2redis")
public class SendRedisController {
    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping("/toArrangeCourse")
    @ResponseBody
    public String add(@RequestBody SimpleBean simpleBean) {
        redisTemplate.opsForList().leftPush(REDIS_QUEUE_KEY, simpleBean);
        return "ok";
    }
}
