package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.model.ParticipationRequest;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    @Transactional
    @Modifying
    @Query("update ParticipationRequest " +
            "set status = :status " +
            "where id = :id and requester = :requester")
    void updateRequest(@Param("id") Long id, @Param("requester") Long userId, @Param("status") String status);

    List<ParticipationRequest> getParticipationRequestDtosByRequester(@Param("id") Long id);

    List<ParticipationRequest> getParticipationRequestDtosByEventAndStatus(@Param("id") Long id, @Param("state") String state);

    ParticipationRequest getParticipationRequestDtoByIdAndRequester(@Param("id") Long id, @Param("requester") Long userId);

    Optional<ParticipationRequest> getParticipationRequestDtoByEventAndRequester(@Param("event") Long eventId, @Param("requester") Long userId);

    List<ParticipationRequest> getParticipationRequestDtosByEvent(@Param("event") Long eventId);

    List<ParticipationRequest> findAllByIdIn(@Param("ids") List<Long> ids);
}