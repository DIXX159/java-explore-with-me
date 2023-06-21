package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.model.EventFullEntity;
import ru.practicum.ewm.model.UserDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<EventFullEntity, Long>, QuerydslPredicateExecutor<EventFullEntity> {

    @Query("select e from EventFullEntity as e " +
            "where e.initiator.id = ?1 " +
            "group by e.id ")
    Page<EventFullEntity> findEventsByUser(@Param("initiator") Long user, Pageable pageable);

    @Query("SELECT e from EventFullEntity as e " +
            "where (coalesce(:users , null) is null or e.initiator.id in :users) " +
            "and (coalesce(:states , null) is null or e.state in :states) " +
            "and (coalesce(:categories , null) is null or e.category.id in :categories) " +
            "and (coalesce(:rangeStart , null) is null or e.eventDate >= :rangeStart) " +
            "and (coalesce(:rangeEnd , null) is null or e.eventDate <= :rangeEnd) "
    )
    Page<EventFullEntity> findEventsAdmin(@Param("users") List<Long> users,
                                          @Param("states") List<String> states,
                                          @Param("categories") List<Long> categories,
                                          @Param("rangeStart") LocalDateTime rangeStart,
                                          @Param("rangeEnd") LocalDateTime rangeEnd,
                                          Pageable pageable);

    @Query("SELECT e from EventFullEntity as e " +
            "where (coalesce(:text , null) is null or lower(e.description) like lower(concat('%', :text,'%'))) " +
            "or (coalesce(:text , null) is null or lower(e.annotation) like lower(concat('%', :text,'%'))) " +
            "and e.paid = :paid " +
            "and (coalesce(:categories , null) is null or e.category.id in :categories) " +
            "and (coalesce(:rangeStart , null) is null or e.eventDate >= :rangeStart) " +
            "and (coalesce(:rangeEnd , null) is null or e.eventDate <= :rangeEnd) " +
            "and e.participantLimit > e.confirmedRequests "
    )
    Page<EventFullEntity> findFiltredEventsOnlyAvailable(@Param("text") String text,
                                                         @Param("categories") List<Long> categories,
                                                         @Param("paid") Boolean paid,
                                                         @Param("rangeStart") LocalDateTime rangeStart,
                                                         @Param("rangeEnd") LocalDateTime rangeEnd,
                                                         Pageable pageable);

    @Query("SELECT e from EventFullEntity as e " +
            "where (coalesce(:text , null) is null or lower(e.description) like lower(concat('%', :text,'%'))) " +
            "or (coalesce(:text , null) is null or lower(e.annotation) like lower(concat('%', :text,'%'))) " +
            "and e.paid = :paid " +
            "and (coalesce(:categories , null) is null or e.category.id in :categories) " +
            "and (coalesce(:rangeStart , null) is null or e.eventDate >= :rangeStart) " +
            "and (coalesce(:rangeEnd , null) is null or e.eventDate <= :rangeEnd) "
    )
    Page<EventFullEntity> findFiltredEvents(@Param("text") String text,
                                            @Param("categories") List<Long> categories,
                                            @Param("paid") Boolean paid,
                                            @Param("rangeStart") LocalDateTime rangeStart,
                                            @Param("rangeEnd") LocalDateTime rangeEnd,
                                            Pageable pageable);

    Optional<EventFullEntity> findByInitiatorAndId(@Param("initiator") UserDto userId, Long eventId);

    EventFullEntity findEventByInitiatorAndId(@Param("initiator") UserDto user, Long eventId);

    EventFullEntity findEventFullEntityById(Long id);

    Optional<EventFullEntity> findEventFullEntityByIdAndStateLike(Long id, String state);

    @Transactional
    @Modifying
    @Query("update EventFullEntity " +
            "set confirmedRequests = :confirmedRequests " +
            "where id = :id")
    void updateRequest(@Param("id") Long id, @Param("confirmedRequests") Long confirmedRequests);

    @Query("SELECT e from EventFullEntity as e " +
            "where e.category.id = :id "
    )
    List<EventFullEntity> findEventFullEntityByCategory(@Param("id") Long id);
}