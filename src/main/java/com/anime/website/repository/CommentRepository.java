package com.anime.website.repository;

import com.anime.website.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByVideoIdAndParentIdIsNull(Long videoId, Pageable pageable);
    List<Comment> findByParentId(Long parentId);
    
    @Query("SELECT c FROM Comment c WHERE c.userId = :userId ORDER BY c.createdAt DESC")
    Page<Comment> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT c FROM Comment c WHERE c.videoId = :videoId AND c.parentId IS NULL ORDER BY c.createdAt DESC")
    List<Comment> findByVideoIdWithReplies(@Param("videoId") Long videoId);
    
    @Query(value = "SELECT c.* FROM comments c WHERE c.video_id = :videoId AND c.parent_id IS NULL ORDER BY c.created_at DESC LIMIT :limit OFFSET :offset", 
           nativeQuery = true)
    List<Comment> findRootCommentsByVideoId(@Param("videoId") Long videoId, @Param("limit") int limit, @Param("offset") int offset);
    
    @Query("SELECT c FROM Comment c WHERE c.parentId IN :parentIds")
    List<Comment> findRepliesByParentIds(@Param("parentIds") List<Long> parentIds);
}
