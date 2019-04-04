package ru.softmg.workers.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WorkReport extends RequestBase {
    @JsonProperty("project_id")
    private Integer projectId;

    @JsonProperty("task_id")
    private Integer taskId;

    @JsonProperty("spent_time")
    private Integer spentTime;

    @JsonProperty("comment")
    private String comment;
}
