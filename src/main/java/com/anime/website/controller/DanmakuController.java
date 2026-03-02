package com.anime.website.controller;

import com.anime.website.dto.*;
import com.anime.website.entity.User;
import com.anime.website.security.CurrentUser;
import com.anime.website.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "弹幕管理", description = "弹幕相关接口")
public class DanmakuController {
    
    private final DanmakuService danmakuService;
    
    @GetMapping("/videos/{videoId}/danmaku")
    @Operation(summary = "获取弹幕列表")
    public ResponseEntity<ApiResponse<List<DanmakuDTO>>> getDanmaku(
            @PathVariable @Min(value = 1, message = "视频ID必须大于0") Long videoId,
            @RequestParam(required = false) @Min(value = 1, message = "集数ID必须大于0") Long episodeId) {
        List<DanmakuDTO> response = danmakuService.getDanmaku(videoId, episodeId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/videos/{videoId}/episodes/{episodeId}/danmaku")
    @Operation(summary = "按集数获取弹幕")
    public ResponseEntity<ApiResponse<List<DanmakuDTO>>> getDanmakuByEpisode(
            @PathVariable @Min(value = 1, message = "视频ID必须大于0") Long videoId,
            @PathVariable @Min(value = 1, message = "集数ID必须大于0") Long episodeId) {
        List<DanmakuDTO> response = danmakuService.getDanmakuByEpisode(videoId, episodeId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping("/danmaku/batch")
    @Operation(summary = "批量获取弹幕")
    public ResponseEntity<ApiResponse<Map<Long, List<DanmakuDTO>>>> getDanmakuBatch(
            @Valid @RequestBody DanmakuBatchRequest request) {
        Map<Long, List<DanmakuDTO>> response = danmakuService.getDanmakuBatch(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping("/danmaku")
    @Operation(summary = "发送弹幕")
    public ResponseEntity<ApiResponse<DanmakuDTO>> createDanmaku(
            @Valid @RequestBody CreateDanmakuRequest request,
            @CurrentUser User user) {
        DanmakuDTO response = danmakuService.createDanmaku(request, user);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @DeleteMapping("/danmaku/{danmakuId}")
    @Operation(summary = "删除弹幕")
    public ResponseEntity<ApiResponse<SuccessResponse>> deleteDanmaku(
            @PathVariable @Min(value = 1, message = "弹幕ID必须大于0") Long danmakuId,
            @CurrentUser User user) {
        danmakuService.deleteDanmaku(danmakuId, user);
        return ResponseEntity.ok(ApiResponse.success(SuccessResponse.of(true)));
    }
    
    @GetMapping("/videos/{videoId}/danmaku/stats")
    @Operation(summary = "获取弹幕统计")
    public ResponseEntity<ApiResponse<DanmakuStatsDTO>> getDanmakuStats(
            @PathVariable @Min(value = 1, message = "视频ID必须大于0") Long videoId,
            @RequestParam(required = false) @Min(value = 1, message = "集数ID必须大于0") Long episodeId) {
        DanmakuStatsDTO response = danmakuService.getDanmakuStats(videoId, episodeId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/videos/{videoId}/episodes/{episodeId}/danmaku/stats")
    @Operation(summary = "获取指定集数弹幕统计")
    public ResponseEntity<ApiResponse<DanmakuStatsDTO>> getDanmakuStatsByEpisode(
            @PathVariable @Min(value = 1, message = "视频ID必须大于0") Long videoId,
            @PathVariable @Min(value = 1, message = "集数ID必须大于0") Long episodeId) {
        DanmakuStatsDTO response = danmakuService.getDanmakuStats(videoId, episodeId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping("/danmaku/{danmakuId}/report")
    @Operation(summary = "举报弹幕")
    public ResponseEntity<ApiResponse<SuccessResponse>> reportDanmaku(
            @PathVariable @Min(value = 1, message = "弹幕ID必须大于0") Long danmakuId,
            @Valid @RequestBody DanmakuReportRequest request,
            @CurrentUser User user) {
        danmakuService.reportDanmaku(danmakuId, request.getReason(), user);
        return ResponseEntity.ok(ApiResponse.success(SuccessResponse.of(true)));
    }
}
