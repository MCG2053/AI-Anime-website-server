package com.anime.website.controller;

import com.anime.website.dto.*;
import com.anime.website.entity.User;
import com.anime.website.security.CurrentUser;
import com.anime.website.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/videos")
@RequiredArgsConstructor
@Tag(name = "视频管理", description = "视频相关接口")
public class VideoController {
    
    private final VideoService videoService;
    
    @GetMapping
    @Operation(summary = "获取视频列表")
    public ResponseEntity<ApiResponse<PageResponse<VideoDTO>>> getVideoList(VideoListRequest request) {
        PageResponse<VideoDTO> response = videoService.getVideoList(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取视频详情")
    public ResponseEntity<ApiResponse<VideoDetailDTO>> getVideoDetail(
            @PathVariable @Min(value = 1, message = "视频ID必须大于0") Long id,
            @CurrentUser User user) {
        VideoDetailDTO response = videoService.getVideoDetail(id, user);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/{id}/status")
    @Operation(summary = "获取视频点赞/收藏状态")
    public ResponseEntity<ApiResponse<VideoLikeStatusDTO>> getVideoLikeStatus(
            @PathVariable @Min(value = 1, message = "视频ID必须大于0") Long id,
            @CurrentUser User user) {
        VideoLikeStatusDTO response = videoService.getVideoLikeStatus(id, user);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping("/{id}/like")
    @Operation(summary = "点赞视频")
    public ResponseEntity<ApiResponse<SuccessResponse>> likeVideo(
            @PathVariable @Min(value = 1, message = "视频ID必须大于0") Long id,
            @CurrentUser User user) {
        videoService.likeVideo(id, user);
        return ResponseEntity.ok(ApiResponse.success(SuccessResponse.of(true)));
    }
    
    @DeleteMapping("/{id}/like")
    @Operation(summary = "取消点赞")
    public ResponseEntity<ApiResponse<SuccessResponse>> unlikeVideo(
            @PathVariable @Min(value = 1, message = "视频ID必须大于0") Long id,
            @CurrentUser User user) {
        videoService.unlikeVideo(id, user);
        return ResponseEntity.ok(ApiResponse.success(SuccessResponse.of(true)));
    }
    
    @PostMapping("/{id}/collect")
    @Operation(summary = "收藏视频")
    public ResponseEntity<ApiResponse<SuccessResponse>> collectVideo(
            @PathVariable @Min(value = 1, message = "视频ID必须大于0") Long id,
            @CurrentUser User user) {
        videoService.collectVideo(id, user);
        return ResponseEntity.ok(ApiResponse.success(SuccessResponse.of(true)));
    }
    
    @DeleteMapping("/{id}/collect")
    @Operation(summary = "取消收藏")
    public ResponseEntity<ApiResponse<SuccessResponse>> uncollectVideo(
            @PathVariable @Min(value = 1, message = "视频ID必须大于0") Long id,
            @CurrentUser User user) {
        videoService.uncollectVideo(id, user);
        return ResponseEntity.ok(ApiResponse.success(SuccessResponse.of(true)));
    }
    
    @GetMapping("/{id}/recommendations")
    @Operation(summary = "获取推荐视频")
    public ResponseEntity<ApiResponse<List<VideoDTO>>> getRecommendations(
            @PathVariable @Min(value = 1, message = "视频ID必须大于0") Long id,
            @RequestParam(defaultValue = "6") @Min(value = 1, message = "数量必须大于0") @Max(value = 50, message = "数量不能超过50") Integer limit) {
        List<VideoDTO> response = videoService.getRecommendations(id, limit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/popular")
    @Operation(summary = "获取热门视频")
    public ResponseEntity<ApiResponse<List<VideoDTO>>> getPopularVideos(
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "数量必须大于0") @Max(value = 100, message = "数量不能超过100") Integer limit) {
        List<VideoDTO> response = videoService.getPopularVideos(limit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/latest")
    @Operation(summary = "获取最新视频")
    public ResponseEntity<ApiResponse<List<VideoDTO>>> getLatestVideos(
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "数量必须大于0") @Max(value = 100, message = "数量不能超过100") Integer limit) {
        List<VideoDTO> response = videoService.getLatestVideos(limit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/schedule")
    @Operation(summary = "获取周更时间表")
    public ResponseEntity<ApiResponse<Map<String, List<VideoDTO>>>> getWeekSchedule() {
        Map<String, List<VideoDTO>> response = videoService.getWeekSchedule();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping("/{id}/play")
    @Operation(summary = "增加播放次数")
    public ResponseEntity<ApiResponse<Void>> incrementPlayCount(
            @PathVariable @Min(value = 1, message = "视频ID必须大于0") Long id) {
        videoService.incrementPlayCount(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
