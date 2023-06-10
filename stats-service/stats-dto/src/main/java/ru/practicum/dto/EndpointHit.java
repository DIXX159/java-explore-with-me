package ru.practicum.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EndpointHit {
    Long id;
    @NotNull(message = "Отсутствует app")
    String app;
    @NotNull(message = "Отсутствует uri")
    String uri;
    @NotNull(message = "Отсутствует ip")
    String ip;
    @NotNull(message = "Отсутствует timestamp")
    String timestamp;
}