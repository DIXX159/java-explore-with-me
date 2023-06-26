package ru.practicum.ewm.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.dto.enums.StateAction;
import ru.practicum.ewm.model.Location;

import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventUserRequest {
    @Size(max = 2000, message = "Максимум 2000 символов")
    @Size(min = 20, message = "Минимум 20 символов")
    String annotation;
    Long category;
    @Size(max = 7000, message = "Максимум 7000 символов")
    @Size(min = 20, message = "Минимум 20 символов")
    String description;
    String eventDate;
    Location location;
    Boolean paid;
    Long participantLimit;
    Boolean requestModeration;
    StateAction stateAction;
    @Size(max = 120, message = "Максимум 120 символов")
    @Size(min = 3, message = "Минимум 30 символов")
    String title;
}