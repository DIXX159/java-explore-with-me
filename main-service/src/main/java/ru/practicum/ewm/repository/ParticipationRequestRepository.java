package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.model.ParticipationRequestDto;

import java.util.List;
import java.util.Optional;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequestDto, Long> {

    @Transactional
    @Modifying
    @Query("update ParticipationRequestDto " +
            "set status = :status " +
            "where id = :id and requester = :requester")
    void updateRequest(@Param("id") Long id, @Param("requester") Long userId, @Param("status") String status);

    List<ParticipationRequestDto> getParticipationRequestDtosByRequester(@Param("id") Long id);

    ParticipationRequestDto getParticipationRequestDtoByIdAndRequester(@Param("id") Long id, @Param("requester") Long userId);

    Optional<ParticipationRequestDto> getParticipationRequestDtoByEventAndRequester(@Param("event") Long eventId, @Param("requester") Long userId);

}