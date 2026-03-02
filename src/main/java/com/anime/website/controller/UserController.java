package com.anime.website.controller;

import com.anime.website.dto.*;
import com.anime.website.entity.User;
import com.anime.website.security.CurrentUser;
import com.anime.website.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户认证和信息管理接口")
public class UserController {
    
    private final UserService userService;
    private final UserAnimeService userAnimeService;
    private final CommentService commentService;
    
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public ResponseEntity<ApiResponse<LoginResponse>> register(@Valid @RequestBody RegisterRequest request) {
        LoginResponse response = userService.register(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping("/logout")
    @Operation(summary = "用户登出")
    public ResponseEntity<ApiResponse<Void>> logout(@CurrentUser User user) {
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    @GetMapping("/info")
    @Operation(summary = "获取用户信息")
    public ResponseEntity<ApiResponse<UserDTO>> getUserInfo(@CurrentUser User user) {
        UserDTO response = userService.getUserInfo(user);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PutMapping("/info")
    @Operation(summary = "更新用户信息")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserInfo(
            @CurrentUser User user,
            @Valid @RequestBody UpdateUserRequest request) {
        UserDTO response = userService.updateUserInfo(user, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PutMapping("/avatar")
    @Operation(summary = "更新头像")
    public ResponseEntity<ApiResponse<UserDTO>> updateAvatar(
            @CurrentUser User user,
            @Valid @RequestBody UpdateAvatarRequest request) {
        userService.updateAvatar(user, request.getAvatar());
        UserDTO response = userService.getUserInfo(user);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PutMapping("/password")
    @Operation(summary = "修改密码")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @CurrentUser User user,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(user, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    @GetMapping("/stats")
    @Operation(summary = "获取用户统计数据")
    public ResponseEntity<ApiResponse<UserStatsDTO>> getUserStats(@CurrentUser User user) {
        UserStatsDTO response = userService.getUserStats(user);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/likes")
    @Operation(summary = "获取用户点赞记录")
    public ResponseEntity<ApiResponse<UserLikesDTO>> getUserLikes(@CurrentUser User user) {
        UserLikesDTO response = userService.getUserLikes(user);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/collections")
    @Operation(summary = "获取用户收藏记录")
    public ResponseEntity<ApiResponse<UserCollectionsDTO>> getUserCollections(@CurrentUser User user) {
        UserCollectionsDTO response = userService.getUserCollections(user);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/comments")
    @Operation(summary = "获取用户评论列表")
    public ResponseEntity<ApiResponse<PageResponse<CommentDTO>>> getUserComments(
            @CurrentUser User user,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于0") Integer page,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "每页数量必须大于0") @Max(value = 100, message = "每页数量不能超过100") Integer pageSize) {
        PageResponse<CommentDTO> response = commentService.getUserComments(user, page, pageSize);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/anime-list")
    @Operation(summary = "获取追番列表")
    public ResponseEntity<ApiResponse<UserAnimeListDTO>> getAnimeList(@CurrentUser User user) {
        UserAnimeListDTO response = userAnimeService.getAnimeList(user);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping("/anime")
    @Operation(summary = "添加追番")
    public ResponseEntity<ApiResponse<SuccessResponse>> addAnime(
            @CurrentUser User user,
            @Valid @RequestBody AddAnimeRequest request) {
        userAnimeService.addAnime(request, user);
        return ResponseEntity.ok(ApiResponse.success(SuccessResponse.of(true)));
    }
    
    @DeleteMapping("/anime/{videoId}")
    @Operation(summary = "移除追番")
    public ResponseEntity<ApiResponse<SuccessResponse>> removeAnime(
            @CurrentUser User user,
            @PathVariable @Min(value = 1, message = "视频ID必须大于0") Long videoId) {
        userAnimeService.removeAnime(videoId, user);
        return ResponseEntity.ok(ApiResponse.success(SuccessResponse.of(true)));
    }
    
    @PutMapping("/anime/{videoId}")
    @Operation(summary = "更新追番状态")
    public ResponseEntity<ApiResponse<SuccessResponse>> updateAnimeStatus(
            @CurrentUser User user,
            @PathVariable @Min(value = 1, message = "视频ID必须大于0") Long videoId,
            @Valid @RequestBody UpdateAnimeStatusRequest request) {
        userAnimeService.updateAnimeStatus(videoId, request, user);
        return ResponseEntity.ok(ApiResponse.success(SuccessResponse.of(true)));
    }
    
    @GetMapping("/history")
    @Operation(summary = "获取观看历史")
    public ResponseEntity<ApiResponse<PageResponse<WatchHistoryDTO>>> getWatchHistory(
            @CurrentUser User user,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于0") Integer page,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "每页数量必须大于0") @Max(value = 100, message = "每页数量不能超过100") Integer pageSize) {
        PageResponse<WatchHistoryDTO> response = userAnimeService.getWatchHistory(user, page, pageSize);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping("/history")
    @Operation(summary = "更新观看进度")
    public ResponseEntity<ApiResponse<WatchHistoryDTO>> updateWatchHistory(
            @CurrentUser User user,
            @Valid @RequestBody UpdateWatchHistoryRequest request) {
        userAnimeService.updateWatchHistory(request, user);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    @DeleteMapping("/history/{videoId}")
    @Operation(summary = "删除观看历史")
    public ResponseEntity<ApiResponse<SuccessResponse>> removeWatchHistory(
            @CurrentUser User user,
            @PathVariable @Min(value = 1, message = "视频ID必须大于0") Long videoId) {
        userAnimeService.removeWatchHistory(videoId, user);
        return ResponseEntity.ok(ApiResponse.success(SuccessResponse.of(true)));
    }
    
    @DeleteMapping("/history")
    @Operation(summary = "清空观看历史")
    public ResponseEntity<ApiResponse<SuccessResponse>> clearWatchHistory(@CurrentUser User user) {
        userAnimeService.clearWatchHistory(user);
        return ResponseEntity.ok(ApiResponse.success(SuccessResponse.of(true)));
    }
}
