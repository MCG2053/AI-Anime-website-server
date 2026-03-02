package com.anime.website.service;

import com.anime.website.dto.*;
import com.anime.website.entity.User;
import com.anime.website.repository.*;
import com.anime.website.security.JwtTokenProvider;
import com.anime.website.util.XssUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final VideoLikeRepository videoLikeRepository;
    private final CommentRepository commentRepository;
    private final UserAnimeRepository userAnimeRepository;
    private final WatchHistoryRepository watchHistoryRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final VideoCollectionRepository videoCollectionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    
    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("邮箱或密码错误"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("邮箱或密码错误");
        }
        
        String token = tokenProvider.generateToken(user.getId(), user.getEmail());
        
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUser(convertToDTO(user));
        
        return response;
    }
    
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("邮箱已被注册");
        }
        
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已被使用");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAvatar("https://api.dicebear.com/7.x/avataaars/svg?seed=" + request.getEmail());
        
        user = userRepository.save(user);
        
        String token = tokenProvider.generateToken(user.getId(), user.getEmail());
        
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUser(convertToDTO(user));
        
        return response;
    }
    
    public UserDTO getUserInfo(User user) {
        return convertToDTO(user);
    }
    
    @Transactional
    public UserDTO updateUserInfo(User user, UpdateUserRequest request) {
        String newUsername = XssUtil.sanitize(request.getUsername());
        if (!user.getUsername().equals(newUsername) && 
            userRepository.existsByUsername(newUsername)) {
            throw new RuntimeException("用户名已被使用");
        }
        
        user.setUsername(newUsername);
        user.setBio(request.getBio() != null ? XssUtil.sanitize(request.getBio()) : null);
        user = userRepository.save(user);
        
        return convertToDTO(user);
    }
    
    @Transactional
    public void updateAvatar(User user, String avatarUrl) {
        user.setAvatar(avatarUrl);
        userRepository.save(user);
    }
    
    @Transactional
    public void changePassword(User user, ChangePasswordRequest request) {
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("旧密码不正确");
        }
        
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
    
    public UserStatsDTO getUserStats(User user) {
        UserStatsDTO stats = new UserStatsDTO();
        stats.setLikeCount(videoLikeRepository.countByUserId(user.getId()).intValue());
        stats.setCommentCount(commentRepository.countByUserId(user.getId()).intValue());
        stats.setAnimeCount(userAnimeRepository.countByUserId(user.getId()).intValue());
        stats.setHistoryCount(watchHistoryRepository.countByUserId(user.getId()).intValue());
        stats.setWatchTime(0L);
        return stats;
    }
    
    public UserLikesDTO getUserLikes(User user) {
        UserLikesDTO likes = new UserLikesDTO();
        likes.setVideoIds(videoLikeRepository.findByUserId(user.getId())
                .stream()
                .map(vl -> vl.getVideoId())
                .collect(Collectors.toList()));
        likes.setCommentIds(commentLikeRepository.findByUserId(user.getId())
                .stream()
                .map(cl -> cl.getCommentId())
                .collect(Collectors.toList()));
        return likes;
    }
    
    public UserCollectionsDTO getUserCollections(User user) {
        UserCollectionsDTO collections = new UserCollectionsDTO();
        collections.setVideoIds(videoCollectionRepository.findByUserId(user.getId())
                .stream()
                .map(vc -> vc.getVideoId())
                .collect(Collectors.toList()));
        return collections;
    }
    
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setAvatar(user.getAvatar());
        dto.setBio(user.getBio());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLikeCount(videoLikeRepository.countByUserId(user.getId()).intValue());
        dto.setCommentCount(commentRepository.countByUserId(user.getId()).intValue());
        dto.setAnimeCount(userAnimeRepository.countByUserId(user.getId()).intValue());
        dto.setHistoryCount(watchHistoryRepository.countByUserId(user.getId()).intValue());
        return dto;
    }
}
