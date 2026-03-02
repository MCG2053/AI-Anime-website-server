package com.anime.website.service;

import com.anime.website.dto.*;
import com.anime.website.entity.*;
import com.anime.website.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoService {
    
    private final VideoRepository videoRepository;
    private final EpisodeRepository episodeRepository;
    private final VideoLikeRepository videoLikeRepository;
    private final VideoCollectionRepository videoCollectionRepository;
    private final VideoTagRepository videoTagRepository;
    private final TagRepository tagRepository;
    
    public PageResponse<VideoDTO> getVideoList(VideoListRequest request) {
        Pageable pageable = PageRequest.of(
            request.getPage() - 1, 
            request.getPageSize(),
            Sort.by(Sort.Direction.DESC, "createdAt")
        );
        
        Page<Video> videoPage = videoRepository.findByFilters(
            request.getCategory(),
            request.getYear(),
            request.getCountry(),
            pageable
        );
        
        List<VideoDTO> videoDTOs = videoPage.getContent()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return PageResponse.of(videoDTOs, videoPage.getTotalElements(), request.getPage(), request.getPageSize());
    }
    
    public VideoDetailDTO getVideoDetail(Long id, User user) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("视频不存在"));
        
        VideoDetailDTO dto = new VideoDetailDTO();
        dto.setId(video.getId());
        dto.setTitle(video.getTitle());
        dto.setCover(video.getCover());
        dto.setDescription(video.getDescription());
        dto.setPlayCount(video.getPlayCount());
        dto.setLikeCount(video.getLikeCount());
        dto.setCollectCount(video.getCollectCount());
        dto.setEpisode(video.getEpisode());
        dto.setCategory(video.getCategory());
        dto.setCountry(video.getCountry());
        dto.setYear(video.getYear());
        dto.setVideoUrl(video.getVideoUrl());
        dto.setCreatedAt(video.getCreatedAt());
        dto.setUpdatedAt(video.getUpdatedAt());
        
        List<Episode> episodes = episodeRepository.findByVideoIdOrderByEpisodeNumberAsc(id);
        dto.setEpisodes(episodes.stream()
                .map(this::convertEpisodeToDTO)
                .collect(Collectors.toList()));
        
        List<VideoTag> videoTags = videoTagRepository.findByVideoIdWithTag(id);
        List<String> tagNames = videoTags.stream()
                .map(vt -> vt.getTag().getName())
                .collect(Collectors.toList());
        dto.setTags(tagNames);
        
        List<Video> relatedVideos = videoRepository.findRelatedVideos(
            video.getCategory(),
            PageRequest.of(0, 6)
        );
        dto.setRelatedVideos(relatedVideos.stream()
                .filter(v -> !v.getId().equals(id))
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
        
        return dto;
    }
    
    public VideoLikeStatusDTO getVideoLikeStatus(Long videoId, User user) {
        VideoLikeStatusDTO status = new VideoLikeStatusDTO();
        
        if (user != null) {
            status.setIsLiked(videoLikeRepository.existsByUserIdAndVideoId(user.getId(), videoId));
            status.setIsCollected(videoCollectionRepository.existsByUserIdAndVideoId(user.getId(), videoId));
        } else {
            status.setIsLiked(false);
            status.setIsCollected(false);
        }
        
        return status;
    }
    
    @Transactional
    public void likeVideo(Long videoId, User user) {
        if (videoLikeRepository.existsByUserIdAndVideoId(user.getId(), videoId)) {
            throw new RuntimeException("已经点赞过了");
        }
        
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("视频不存在"));
        
        VideoLike like = new VideoLike();
        like.setUserId(user.getId());
        like.setVideoId(videoId);
        videoLikeRepository.save(like);
        
        video.setLikeCount(video.getLikeCount() + 1);
        videoRepository.save(video);
    }
    
    @Transactional
    public void unlikeVideo(Long videoId, User user) {
        VideoLike like = videoLikeRepository.findByUserIdAndVideoId(user.getId(), videoId)
                .orElseThrow(() -> new RuntimeException("未点赞"));
        
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("视频不存在"));
        
        videoLikeRepository.delete(like);
        video.setLikeCount(Math.max(0, video.getLikeCount() - 1));
        videoRepository.save(video);
    }
    
    @Transactional
    public void collectVideo(Long videoId, User user) {
        if (videoCollectionRepository.existsByUserIdAndVideoId(user.getId(), videoId)) {
            throw new RuntimeException("已经收藏过了");
        }
        
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("视频不存在"));
        
        VideoCollection collection = new VideoCollection();
        collection.setUserId(user.getId());
        collection.setVideoId(videoId);
        videoCollectionRepository.save(collection);
        
        video.setCollectCount(video.getCollectCount() + 1);
        videoRepository.save(video);
    }
    
    @Transactional
    public void uncollectVideo(Long videoId, User user) {
        VideoCollection collection = videoCollectionRepository.findByUserIdAndVideoId(user.getId(), videoId)
                .orElseThrow(() -> new RuntimeException("未收藏"));
        
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("视频不存在"));
        
        videoCollectionRepository.delete(collection);
        video.setCollectCount(Math.max(0, video.getCollectCount() - 1));
        videoRepository.save(video);
    }
    
    public PageResponse<VideoDTO> searchVideos(String keyword, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Video> videoPage = videoRepository.searchByKeyword(keyword, pageable);
        
        List<VideoDTO> videoDTOs = videoPage.getContent()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return PageResponse.of(videoDTOs, videoPage.getTotalElements(), page, pageSize);
    }
    
    @Cacheable(value = "popularVideos", key = "#limit")
    public List<VideoDTO> getPopularVideos(Integer limit) {
        return videoRepository.findPopularVideos(PageRequest.of(0, limit))
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Cacheable(value = "latestVideos", key = "#limit")
    public List<VideoDTO> getLatestVideos(Integer limit) {
        return videoRepository.findLatestVideos(PageRequest.of(0, limit))
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<VideoDTO> getRecommendations(Long videoId, Integer limit) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("视频不存在"));
        
        return videoRepository.findRelatedVideos(video.getCategory(), PageRequest.of(0, limit))
                .stream()
                .filter(v -> !v.getId().equals(videoId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Cacheable(value = "weekSchedule")
    public Map<String, List<VideoDTO>> getWeekSchedule() {
        Map<String, List<VideoDTO>> schedule = new LinkedHashMap<>();
        String[] days = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        
        List<Video> latestVideos = videoRepository.findLatestVideos(PageRequest.of(0, 21));
        int videosPerDay = Math.max(3, latestVideos.size() / 7);
        
        for (int i = 0; i < days.length; i++) {
            int start = i * videosPerDay;
            int end = Math.min(start + videosPerDay, latestVideos.size());
            
            List<VideoDTO> dayVideos = latestVideos.subList(start, end)
                    .stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            schedule.put(days[i], dayVideos);
        }
        
        return schedule;
    }
    
    @Transactional
    public void incrementPlayCount(Long videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("视频不存在"));
        
        video.setPlayCount(video.getPlayCount() + 1);
        videoRepository.save(video);
    }
    
    private VideoDTO convertToDTO(Video video) {
        VideoDTO dto = new VideoDTO();
        dto.setId(video.getId());
        dto.setTitle(video.getTitle());
        dto.setCover(video.getCover());
        dto.setDescription(video.getDescription());
        dto.setPlayCount(video.getPlayCount());
        dto.setLikeCount(video.getLikeCount());
        dto.setCollectCount(video.getCollectCount());
        dto.setEpisode(video.getEpisode());
        dto.setCategory(video.getCategory());
        dto.setCountry(video.getCountry());
        dto.setYear(video.getYear());
        dto.setCreatedAt(video.getCreatedAt());
        dto.setUpdatedAt(video.getUpdatedAt());
        return dto;
    }
    
    private EpisodeDTO convertEpisodeToDTO(Episode episode) {
        EpisodeDTO dto = new EpisodeDTO();
        dto.setId(episode.getId());
        dto.setTitle(episode.getTitle());
        dto.setVideoUrl(episode.getVideoUrl());
        dto.setDuration(episode.getDuration());
        return dto;
    }
}
