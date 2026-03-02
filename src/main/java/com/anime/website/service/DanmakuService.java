package com.anime.website.service;

import com.anime.website.dto.*;
import com.anime.website.entity.*;
import com.anime.website.repository.*;
import com.anime.website.util.XssUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DanmakuService {
    
    private final DanmakuRepository danmakuRepository;
    
    public List<DanmakuDTO> getDanmaku(Long videoId, Long episodeId) {
        List<Danmaku> danmakuList;
        
        if (episodeId != null) {
            danmakuList = danmakuRepository.findByVideoIdAndEpisodeIdOrderByTimeAsc(videoId, episodeId);
        } else {
            danmakuList = danmakuRepository.findByVideoIdOrderByTimeAsc(videoId);
        }
        
        return danmakuList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<DanmakuDTO> getDanmakuByEpisode(Long videoId, Long episodeId) {
        List<Danmaku> danmakuList = danmakuRepository.findByVideoIdAndEpisodeIdOrderByTimeAsc(videoId, episodeId);
        return danmakuList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Map<Long, List<DanmakuDTO>> getDanmakuBatch(DanmakuBatchRequest request) {
        List<Danmaku> danmakuList = danmakuRepository.findByVideoIdAndEpisodeIdIn(
                request.getVideoId(), request.getEpisodeIds());
        
        Map<Long, List<DanmakuDTO>> result = new HashMap<>();
        for (Long episodeId : request.getEpisodeIds()) {
            result.put(episodeId, new ArrayList<>());
        }
        
        for (Danmaku danmaku : danmakuList) {
            Long episodeId = danmaku.getEpisodeId();
            if (result.containsKey(episodeId)) {
                result.get(episodeId).add(convertToDTO(danmaku));
            }
        }
        
        return result;
    }
    
    @Transactional
    public DanmakuDTO createDanmaku(CreateDanmakuRequest request, User user) {
        Danmaku danmaku = new Danmaku();
        danmaku.setVideoId(request.getVideoId());
        danmaku.setEpisodeId(request.getEpisodeId());
        danmaku.setUserId(user.getId());
        // XSS过滤弹幕内容
        danmaku.setContent(XssUtil.sanitize(request.getContent()));
        danmaku.setTime(request.getTime());
        danmaku.setColor(request.getColor() != null ? request.getColor() : "#ffffff");
        danmaku.setType(request.getType() != null ? request.getType() : "scroll");
        
        danmaku = danmakuRepository.save(danmaku);
        
        return convertToDTO(danmaku);
    }
    
    @Transactional
    public void deleteDanmaku(Long danmakuId, User user) {
        Danmaku danmaku = danmakuRepository.findById(danmakuId)
                .orElseThrow(() -> new RuntimeException("弹幕不存在"));
        
        if (!danmaku.getUserId().equals(user.getId())) {
            throw new RuntimeException("无权删除此弹幕");
        }
        
        danmakuRepository.delete(danmaku);
    }
    
    public DanmakuStatsDTO getDanmakuStats(Long videoId, Long episodeId) {
        DanmakuStatsDTO stats = new DanmakuStatsDTO();
        
        if (episodeId != null) {
            stats.setTotal(danmakuRepository.countByVideoIdAndEpisodeId(videoId, episodeId));
            stats.setScrollCount(danmakuRepository.countByVideoIdAndEpisodeIdAndType(videoId, episodeId, "scroll"));
            stats.setTopCount(danmakuRepository.countByVideoIdAndEpisodeIdAndType(videoId, episodeId, "top"));
            stats.setBottomCount(danmakuRepository.countByVideoIdAndEpisodeIdAndType(videoId, episodeId, "bottom"));
        } else {
            stats.setTotal(danmakuRepository.countByVideoId(videoId));
            stats.setScrollCount(danmakuRepository.countByVideoIdAndType(videoId, "scroll"));
            stats.setTopCount(danmakuRepository.countByVideoIdAndType(videoId, "top"));
            stats.setBottomCount(danmakuRepository.countByVideoIdAndType(videoId, "bottom"));
        }
        
        return stats;
    }
    
    public void reportDanmaku(Long danmakuId, String reason, User user) {
        Danmaku danmaku = danmakuRepository.findById(danmakuId)
                .orElseThrow(() -> new RuntimeException("弹幕不存在"));
        
        log.info("弹幕举报: danmakuId={}, reason={}, reporter={}", danmakuId, reason, user.getId());
    }
    
    private DanmakuDTO convertToDTO(Danmaku danmaku) {
        DanmakuDTO dto = new DanmakuDTO();
        dto.setId(danmaku.getId());
        dto.setContent(danmaku.getContent());
        dto.setTime(danmaku.getTime());
        dto.setColor(danmaku.getColor());
        dto.setType(danmaku.getType());
        dto.setUserId(danmaku.getUserId());
        dto.setCreatedAt(danmaku.getCreatedAt());
        return dto;
    }
}
