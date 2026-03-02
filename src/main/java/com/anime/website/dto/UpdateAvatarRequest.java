package com.anime.website.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.URL;
import lombok.Data;

@Data
public class UpdateAvatarRequest {
    @NotBlank(message = "头像URL不能为空")
    @URL(message = "头像URL格式不正确")
    private String avatar;
}
