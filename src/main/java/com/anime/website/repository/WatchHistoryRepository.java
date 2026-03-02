package com.anime.website.repository;

import com.anime.website.entity.WatchHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Long> {
    Page<WatchHistory> findByUserIdOrderByWatchedAtDesc(Long userId, Pageable pageable);
    Optional<WatchHistory> findByUserIdAndVideoId(Long userId, Long videoId);
    void deleteByUserIdAndVideoId(Long userId, Long videoId);
    void deleteByUserId(Long userId);
    
    @Query("SELECT COUNT(w) FROM WatchHistory w WHERE w.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT w FROM WatchHistory w WHERE w.userId = :userId ORDER BY w.watchedAt DESC")
    List<WatchHistory> findTop20ByUserId(@Param("userId") Long userId, Pageable pageable);
}
