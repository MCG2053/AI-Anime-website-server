package com.anime.website.repository;

import com.anime.website.entity.VideoTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VideoTagRepository extends JpaRepository<VideoTag, Long> {
    List<VideoTag> findByVideoId(Long videoId);
    
    @Query("SELECT vt FROM VideoTag vt JOIN FETCH vt.tag WHERE vt.videoId = :videoId")
    List<VideoTag> findByVideoIdWithTag(@Param("videoId") Long videoId);
}
