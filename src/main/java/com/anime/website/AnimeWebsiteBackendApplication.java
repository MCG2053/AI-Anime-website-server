package com.anime.website;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AnimeWebsiteBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnimeWebsiteBackendApplication.class, args);
    }
}
