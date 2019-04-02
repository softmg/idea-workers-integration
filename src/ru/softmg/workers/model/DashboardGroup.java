package ru.softmg.workers.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DashboardGroup {
    @JsonProperty("worker")
    private Worker worker;

    @JsonProperty("tasks")
    private List<Task> tasks = new ArrayList<>();
}
