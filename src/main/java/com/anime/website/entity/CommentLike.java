package com.anime.website.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comment_likes", indexes = {
    @Index(name = "idx_comment_like_user_id", columnList = "user_id"),
    @Index(name = "idx_comment_like_comment_id", columnList = "comment_id"),
    @Index(name = "idx_comment_like_user_comment", columnList = "user_id, comment_id")
})
public class CommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "comment_id")
    private Long commentId;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
