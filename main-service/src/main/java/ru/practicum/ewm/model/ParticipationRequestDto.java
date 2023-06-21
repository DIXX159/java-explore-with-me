package ru.practicum.ewm.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "participation_request")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequestDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @Column(name = "created")
    String created;
    @Column(name = "event")
    Long event;
    @Column(name = "requester")
    Long requester;
    @Column(name = "status")
    String status;
}