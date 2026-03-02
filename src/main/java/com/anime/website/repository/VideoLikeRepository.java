package com.anime.website.repository;

import com.anime.website.entity.VideoLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VideoLikeRepository extends JpaRepository<VideoLike, Long> {
    Optional<VideoLike> findByUserIdAndVideoId(Long userId, Long videoId);
    List<VideoLike> findByUserId(Long userId);
    Long countByUserId(Long userId);
    boolean existsByUserIdAndVideoId(Long userId, Long videoId);
}
