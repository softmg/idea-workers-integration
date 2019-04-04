package ru.softmg.workers.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetTasksRequest extends RequestBase {
    @JsonProperty("id")
    private Integer projectId;
}
