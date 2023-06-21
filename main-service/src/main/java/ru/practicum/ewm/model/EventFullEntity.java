package ru.practicum.ewm.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "events", uniqueConstraints = {
        @UniqueConstraint(name = "uc_eventfullentity", columnNames = {"id"})
})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @Column(name = "annotation")
    String annotation;
    @JoinColumn(name = "category")
    @ManyToOne(fetch = FetchType.EAGER)
    CategoryDto category;
    @Column(name = "confirmedrequests")
    Long confirmedRequests;
    @Column(name = "createdon")
    LocalDateTime createdOn;
    @Column(name = "description")
    String description;
    @Column(name = "eventdate")
    LocalDateTime eventDate;
    @JoinColumn(name = "initiator")
    @ManyToOne(fetch = FetchType.EAGER)
    UserDto initiator;
    @JoinColumn(name = "location")
    @ManyToOne(fetch = FetchType.EAGER)
    Location location;
    @Column(name = "paid")
    Boolean paid;
    @Column(name = "participantlimit")
    Long participantLimit;
    @Column(name = "publishedon")
    LocalDateTime publishedOn;
    @Column(name = "requestmoderation")
    Boolean requestModeration;
    @Column(name = "state")
    String state;
    @Column(name = "title")
    String title;
    @Column(name = "views")
    Long views;
}