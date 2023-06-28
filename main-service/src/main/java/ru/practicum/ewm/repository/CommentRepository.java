package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.model.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Transactional
    @Modifying
    @Query("update Comment " +
            "set comment = :comment, " +
            "state = :state " +
            "where id = :id")
    void updateComment(@Param("id") Long id, @Param("comment") String comment, @Param("state") String state);

    @Transactional
    @Modifying
    @Query("update Comment " +
            "set state = :state " +
            "where id = :id")
    void updateAdminComment(@Param("id") Long id, @Param("state") String state);

    List<Comment> findAllByCommentator(Long userId);

    List<Comment> findAllByEventIdAndState(Long eventId, String state);
}