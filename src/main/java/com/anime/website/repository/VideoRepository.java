package com.anime.website.repository;

import com.anime.website.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    @Query("SELECT v FROM Video v WHERE " +
           "(:category IS NULL OR v.category = :category) AND " +
           "(:year IS NULL OR v.year = :year) AND " +
           "(:country IS NULL OR v.country = :country)")
    Page<Video> findByFilters(@Param("category") String category,
                              @Param("year") Integer year,
                              @Param("country") String country,
                              Pageable pageable);
    
    @Query("SELECT v FROM Video v WHERE LOWER(v.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Video> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT v FROM Video v ORDER BY v.playCount DESC")
    List<Video> findPopularVideos(Pageable pageable);
    
    @Query("SELECT v FROM Video v ORDER BY v.createdAt DESC")
    List<Video> findLatestVideos(Pageable pageable);
    
    @Query("SELECT v FROM Video v WHERE v.category = :category ORDER BY v.playCount DESC")
    List<Video> findRelatedVideos(@Param("category") String category, Pageable pageable);
}
