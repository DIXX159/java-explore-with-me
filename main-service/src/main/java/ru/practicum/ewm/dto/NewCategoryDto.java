package ru.practicum.ewm.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCategoryDto {
    @NotBlank(message = "Отсутствует название")
    @Size(max = 50, message = "Максимум 50 символа")
    @Size(min = 1, message = "Минимум 1 символ")
    String name;
}