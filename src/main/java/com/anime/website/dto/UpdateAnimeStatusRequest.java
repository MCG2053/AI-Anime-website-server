package com.anime.website.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateAnimeStatusRequest {
    @NotBlank(message = "状态不能为空")
    @Pattern(regexp = "^(watching|completed|dropped|planned)$", message = "状态值无效，必须是 watching、completed、dropped 或 planned")
    private String status;
}
