package ru.softmg.workers.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
