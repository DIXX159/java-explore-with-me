package ru.practicum.ewm.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comments")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @Column(name = "comment")
    @NotBlank(message = "must not be blank")
    @Size(max = 50, message = "Максимум 50 символа")
    @Size(min = 1, message = "Минимум 1 символ")
    String comment;
    @Column(name = "commentator")
    Long commentator;
    @Column(name = "state")
    String state;
    @Column(name = "eventid")
    Long eventId;
}