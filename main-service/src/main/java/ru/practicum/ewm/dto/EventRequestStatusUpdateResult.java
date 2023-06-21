package ru.practicum.ewm.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.model.ParticipationRequestDto;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestStatusUpdateResult {
    List<ParticipationRequestDto> confirmedRequests;
    List<ParticipationRequestDto> rejectedRequests;
}