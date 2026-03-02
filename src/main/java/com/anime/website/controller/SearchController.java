package com.anime.website.controller;

import com.anime.website.dto.*;
import com.anime.website.entity.User;
import com.anime.website.security.CurrentUser;
import com.anime.website.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Tag(name = "搜索", description = "搜索相关接口")
public class SearchController {
    
    private final VideoService videoService;
    
    @GetMapping
    @Operation(summary = "搜索视频")
    public ResponseEntity<ApiResponse<PageResponse<VideoDTO>>> searchVideos(
            @RequestParam @Size(min = 1, max = 100, message = "搜索关键词长度必须在1-100个字符之间") String keyword,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于0") Integer page,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "每页数量必须大于0") @Max(value = 100, message = "每页数量不能超过100") Integer pageSize) {
        PageResponse<VideoDTO> response = videoService.searchVideos(keyword.trim(), page, pageSize);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
