package com.anime.website.repository;

import com.anime.website.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByUserIdAndCommentId(Long userId, Long commentId);
    List<CommentLike> findByUserId(Long userId);
    boolean existsByUserIdAndCommentId(Long userId, Long commentId);
}
