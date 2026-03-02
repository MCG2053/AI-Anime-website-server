package com.anime.website.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AddAnimeRequest {
    @NotNull(message = "视频ID不能为空")
    private Long videoId;
    
    @NotNull(message = "状态不能为空")
    @Pattern(regexp = "^(watching|completed|dropped|planned)$", message = "状态值无效，必须是 watching、completed、dropped 或 planned")
    private String status;
}
