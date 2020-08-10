package i.am.whp.bean;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TimeRange implements Serializable {
    private Date startTime;
    private Date endTime;
}
