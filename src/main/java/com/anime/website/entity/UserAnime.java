package com.anime.website.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_anime", indexes = {
    @Index(name = "idx_user_anime_user_id", columnList = "user_id"),
    @Index(name = "idx_user_anime_video_id", columnList = "video_id"),
    @Index(name = "idx_user_anime_status", columnList = "status"),
    @Index(name = "idx_user_anime_user_status", columnList = "user_id, status")
})
public class UserAnime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "video_id")
    private Long videoId;
    
    @Column(length = 20)
    private String status;
    
    @Column(name = "added_at")
    private LocalDateTime addedAt;
    
    @PrePersist
    protected void onCreate() {
        addedAt = LocalDateTime.now();
    }
}
