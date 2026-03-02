package com.anime.website.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "episodes", indexes = {
    @Index(name = "idx_episode_video_id", columnList = "video_id"),
    @Index(name = "idx_episode_video_episode", columnList = "video_id, episode_number")
})
public class Episode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "video_id")
    private Long videoId;
    
    @Column(nullable = false, length = 100)
    private String title;
    
    @Column(name = "video_url", length = 500)
    private String videoUrl;
    
    @Column
    private Integer duration;
    
    @Column(name = "episode_number")
    private Integer episodeNumber;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
