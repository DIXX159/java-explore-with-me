package ru.practicum.ewm.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.model.CategoryDto;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDto {
    String annotation;
    CategoryDto category;
    Long confirmedRequests;
    String eventDate;
    Long id;
    UserShortDto initiator;
    Boolean paid;
    String title;
    Long views;
}