package ru.softmg.workers.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest extends RequestBase {
    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;
}
