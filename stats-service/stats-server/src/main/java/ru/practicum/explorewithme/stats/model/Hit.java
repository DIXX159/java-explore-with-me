package ru.practicum.explorewithme.stats.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.explorewithme.data.EntityData;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "endpointhit")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Hit extends EntityData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @Column(name = "app")
    String app;
    @Column(name = "uri")
    String uri;
    @Column(name = "ip")
    String ip;
    @Column(name = "timestamp")
    LocalDateTime timestamp;
}