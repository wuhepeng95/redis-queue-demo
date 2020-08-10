package i.am.whp.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import i.am.whp.bean.SimpleBean;
import i.am.whp.manager.CommonManager;
import org.slf4j.MDC;

import java.util.UUID;

import static i.am.whp.constants.CommonConstants.LOCK_TRY_TIMES;

public class MyRunnable implements Runnable {

    private Object param;
    private CommonManager commonManager;

    public MyRunnable(Object parameter, CommonManager commonManager) {
        this.param = parameter;
        this.commonManager = commonManager;
    }

    @Override
    public void run() {
        MDC.put("GUID", UUID.randomUUID().toString() + Thread.currentThread().getName() + "-");
        commonManager.doArrangeWithLock(JSONObject.parseObject(JSON.toJSONString(param), SimpleBean.class), LOCK_TRY_TIMES);
    }

    public Object getParam() {
        return param;
    }
}
