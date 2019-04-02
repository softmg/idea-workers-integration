package ru.softmg.workers.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.softmg.workers.util.DashboardDeserializer;

import java.util.HashMap;

@JsonDeserialize(using = DashboardDeserializer.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Dashboard {
    @JsonProperty("tasks")
    private HashMap<String, DashboardGroup> tasks = new HashMap<>();
}
