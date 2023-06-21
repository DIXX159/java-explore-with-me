package ru.practicum.ewm.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewUserRequest {
    @NotBlank(message = "Отсутствует email")
    @Size(max = 254, message = "Максимум 254 символа")
    @Size(min = 6, message = "Минимум 6 символов")
    @Email(message = "Некорректный email")
    String email;
    @NotBlank(message = "Отсутствует name")
    @Size(max = 250, message = "Максимум 250 символов")
    @Size(min = 2, message = "Минимум 2 символа")
    String name;
}