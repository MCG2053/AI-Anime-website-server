package com.anime.website.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateDanmakuRequest {
    @NotNull(message = "视频ID不能为空")
    private Long videoId;
    
    private Long episodeId;
    
    @NotBlank(message = "弹幕内容不能为空")
    @Size(max = 100, message = "弹幕内容不能超过100个字符")
    private String content;
    
    @NotNull(message = "时间不能为空")
    @Min(value = 0, message = "时间不能为负数")
    private Double time;
    
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "颜色格式不正确，必须是#RRGGBB格式")
    private String color = "#ffffff";
    
    @Pattern(regexp = "^(scroll|top|bottom)$", message = "弹幕类型必须是 scroll、top 或 bottom")
    private String type = "scroll";
}
