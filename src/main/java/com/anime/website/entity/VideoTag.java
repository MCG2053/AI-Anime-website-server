package com.anime.website.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "video_tags", indexes = {
    @Index(name = "idx_video_tag_video_id", columnList = "video_id"),
    @Index(name = "idx_video_tag_tag_id", columnList = "tag_id"),
    @Index(name = "idx_video_tag_video_tag", columnList = "video_id, tag_id")
})
public class VideoTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "video_id")
    private Long videoId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", insertable = false, updatable = false)
    private Tag tag;
    
    @Column(name = "tag_id")
    private Long tagId;
}
