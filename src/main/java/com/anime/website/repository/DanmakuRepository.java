package com.anime.website.repository;

import com.anime.website.entity.Danmaku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DanmakuRepository extends JpaRepository<Danmaku, Long> {
    List<Danmaku> findByVideoIdAndEpisodeIdOrderByTimeAsc(Long videoId, Long episodeId);
    List<Danmaku> findByVideoIdOrderByTimeAsc(Long videoId);
    
    @Query("SELECT d FROM Danmaku d WHERE d.videoId = :videoId AND d.episodeId IN :episodeIds ORDER BY d.time ASC")
    List<Danmaku> findByVideoIdAndEpisodeIdIn(@Param("videoId") Long videoId, @Param("episodeIds") List<Long> episodeIds);
    
    Long countByVideoId(Long videoId);
    Long countByVideoIdAndEpisodeId(Long videoId, Long episodeId);
    Long countByVideoIdAndType(Long videoId, String type);
    Long countByVideoIdAndEpisodeIdAndType(Long videoId, Long episodeId, String type);
}
