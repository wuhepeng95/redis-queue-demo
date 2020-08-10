package i.am.whp.bean;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
public class SimpleBean implements Serializable {
    private Long arrangeCourseId;
    private Long studentId;
    private TimeRange timeRange;
    private List<Long> teacherIds;
}


