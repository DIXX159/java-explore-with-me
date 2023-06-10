package ru.practicum.ewm.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCategoryDto {
    @NotNull(message = "Отсутствует название")
    String name;
}