package ru.softmg.workers.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    private String id;
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
