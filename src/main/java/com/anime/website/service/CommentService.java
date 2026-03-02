package com.anime.website.service;

import com.anime.website.dto.*;
import com.anime.website.entity.*;
import com.anime.website.repository.*;
import com.anime.website.util.XssUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    
    public PageResponse<CommentDTO> getComments(Long videoId, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> commentPage = commentRepository.findByVideoIdAndParentIdIsNull(videoId, pageable);
        
        List<Long> userIds = commentPage.getContent().stream()
                .map(Comment::getUserId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, User> userMap = userRepository.findAllById(userIds)
                .stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        
        List<Long> commentIds = commentPage.getContent().stream()
                .map(Comment::getId)
                .collect(Collectors.toList());
        List<Comment> allReplies = commentRepository.findRepliesByParentIds(commentIds);
        
        Map<Long, List<Comment>> repliesMap = allReplies.stream()
                .collect(Collectors.groupingBy(Comment::getParentId));
        
        Set<Long> replyUserIds = allReplies.stream()
                .map(Comment::getUserId)
                .collect(Collectors.toSet());
        userMap.putAll(userRepository.findAllById(replyUserIds)
                .stream()
                .collect(Collectors.toMap(User::getId, u -> u)));
        
        List<CommentDTO> commentDTOs = commentPage.getContent()
                .stream()
                .map(c -> convertToDTO(c, userMap, repliesMap))
                .collect(Collectors.toList());
        
        return PageResponse.of(commentDTOs, commentPage.getTotalElements(), page, pageSize);
    }
    
    @Transactional
    public CommentDTO createComment(CreateCommentRequest request, User user) {
        videoRepository.findById(request.getVideoId())
                .orElseThrow(() -> new RuntimeException("视频不存在"));
        
        Comment comment = new Comment();
        comment.setUserId(user.getId());
        comment.setVideoId(request.getVideoId());
        // XSS过滤评论内容
        comment.setContent(XssUtil.sanitize(request.getContent()));
        comment.setParentId(request.getParentId());
        
        comment = commentRepository.save(comment);
        
        return convertToDTO(comment, Map.of(user.getId(), user), Map.of());
    }
    
    @Transactional
    public void likeComment(Long commentId, User user) {
        if (commentLikeRepository.existsByUserIdAndCommentId(user.getId(), commentId)) {
            throw new RuntimeException("已经点赞过了");
        }
        
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
        
        CommentLike like = new CommentLike();
        like.setUserId(user.getId());
        like.setCommentId(commentId);
        commentLikeRepository.save(like);
        
        comment.setLikeCount(comment.getLikeCount() + 1);
        commentRepository.save(comment);
    }
    
    @Transactional
    public void unlikeComment(Long commentId, User user) {
        CommentLike like = commentLikeRepository.findByUserIdAndCommentId(user.getId(), commentId)
                .orElseThrow(() -> new RuntimeException("未点赞"));
        
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
        
        commentLikeRepository.delete(like);
        comment.setLikeCount(Math.max(0, comment.getLikeCount() - 1));
        commentRepository.save(comment);
    }
    
    @Transactional
    public void deleteComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
        
        if (!comment.getUserId().equals(user.getId())) {
            throw new RuntimeException("无权删除此评论");
        }
        
        commentRepository.delete(comment);
    }
    
    public PageResponse<CommentDTO> getUserComments(User user, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> commentPage = commentRepository.findByUserId(user.getId(), pageable);
        
        List<CommentDTO> commentDTOs = commentPage.getContent()
                .stream()
                .map(c -> convertToDTO(c, Map.of(user.getId(), user), Map.of()))
                .collect(Collectors.toList());
        
        return PageResponse.of(commentDTOs, commentPage.getTotalElements(), page, pageSize);
    }
    
    private CommentDTO convertToDTO(Comment comment, Map<Long, User> userMap, Map<Long, List<Comment>> repliesMap) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setUserId(comment.getUserId());
        dto.setContent(comment.getContent());
        dto.setLikeCount(comment.getLikeCount());
        dto.setCreatedAt(comment.getCreatedAt());
        
        User user = userMap.get(comment.getUserId());
        if (user != null) {
            dto.setUsername(user.getUsername());
            dto.setAvatar(user.getAvatar());
        }
        
        List<Comment> replies = repliesMap.get(comment.getId());
        if (replies != null && !replies.isEmpty()) {
            dto.setReplies(replies.stream()
                    .map(r -> convertToDTO(r, userMap, repliesMap))
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
}
