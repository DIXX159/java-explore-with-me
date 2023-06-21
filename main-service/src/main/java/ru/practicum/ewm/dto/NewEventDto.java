package ru.practicum.ewm.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.model.Location;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {
    @NotNull(message = "Отсутствует аннотация")
    @Size(max = 2000, message = "Максимум 2000 символов")
    @Size(min = 20, message = "Минимум 20 символов")
    String annotation;
    @NotNull(message = "Отсутствует категория")
    Long category;
    @NotNull(message = "Отсутствует описание")
    @Size(max = 7000, message = "Максимум 7000 символов")
    @Size(min = 20, message = "Минимум 20 символов")
    String description;
    @NotNull(message = "Отсутствует дата события")
    String eventDate;
    @NotNull(message = "Отсутствует локация события")
    Location location;
    Boolean paid;
    Long participantLimit = 0L;
    Boolean requestModeration = true;
    @NotNull(message = "Отсутствует заголовок")
    @Size(max = 120, message = "Максимум 120 символов")
    @Size(min = 3, message = "Минимум 30 символов")
    String title;
}