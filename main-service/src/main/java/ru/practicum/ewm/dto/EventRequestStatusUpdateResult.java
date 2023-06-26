package ru.practicum.ewm.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.model.ParticipationRequest;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestStatusUpdateResult {
    List<ParticipationRequest> confirmedRequests;
    List<ParticipationRequest> rejectedRequests;
}