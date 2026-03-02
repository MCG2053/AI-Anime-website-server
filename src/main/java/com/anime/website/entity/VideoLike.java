package com.anime.website.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "video_likes", indexes = {
    @Index(name = "idx_video_like_user_id", columnList = "user_id"),
    @Index(name = "idx_video_like_video_id", columnList = "video_id"),
    @Index(name = "idx_video_like_user_video", columnList = "user_id, video_id")
})
public class VideoLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "video_id")
    private Long videoId;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
