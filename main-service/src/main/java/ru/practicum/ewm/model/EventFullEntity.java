package ru.practicum.ewm.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.dto.enums.State;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "events")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @Column(name = "annotation")
    String annotation;
    @Column(name = "category")
    Long category;
    @Column(name = "confirmedrequests")
    Long confirmedRequests;
    @Column(name = "createdon")
    LocalDateTime createdOn;
    @Column(name = "description")
    String description;
    @Column(name = "eventdate")
    LocalDateTime eventDate;
    @Column(name = "initiator")
    Long initiator;
    @Column(name = "location")
    Long location;
    @Column(name = "paid")
    Boolean paid;
    @Column(name = "participantlimit")
    Long participantLimit;
    @Column(name = "publishedon")
    LocalDateTime publishedOn;
    @Column(name = "requestmoderation")
    Boolean requestModeration;
    @Column(name = "state")
    State state;
    @Column(name = "title")
    String title;
    @Column(name = "views")
    Long views;
}