package com.anime.website.repository;

import com.anime.website.entity.UserAnime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserAnimeRepository extends JpaRepository<UserAnime, Long> {
    List<UserAnime> findByUserId(Long userId);
    List<UserAnime> findByUserIdAndStatus(Long userId, String status);
    Optional<UserAnime> findByUserIdAndVideoId(Long userId, Long videoId);
    void deleteByUserIdAndVideoId(Long userId, Long videoId);
    Long countByUserId(Long userId);
    
    @Query("SELECT ua FROM UserAnime ua WHERE ua.userId = :userId AND ua.status = :status")
    List<UserAnime> findByUserIdAndStatusWithVideo(@Param("userId") Long userId, @Param("status") String status);
}
