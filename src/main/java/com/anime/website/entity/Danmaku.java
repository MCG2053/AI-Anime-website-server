package com.anime.website.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "danmaku", indexes = {
    @Index(name = "idx_danmaku_video_episode", columnList = "video_id, episode_id"),
    @Index(name = "idx_danmaku_video_id", columnList = "video_id"),
    @Index(name = "idx_danmaku_time", columnList = "time"),
    @Index(name = "idx_danmaku_user_id", columnList = "user_id")
})
public class Danmaku {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "video_id")
    private Long videoId;
    
    @Column(name = "episode_id")
    private Long episodeId;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Column
    private Double time;
    
    @Column(length = 20)
    private String color = "#ffffff";
    
    @Column(length = 20)
    private String type = "scroll";
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
