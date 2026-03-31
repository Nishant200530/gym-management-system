package com.example.GymManagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // 1 Product images 
        Path productUploadDir = Paths.get("uploads");
        String productUploadPath = productUploadDir.toFile().getAbsolutePath();

        //  Trainer images 
        String trainerUploadPath = "C:/Users/91790/Desktop/uploads/";

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(
                        "file:" + productUploadPath + "/",   // products
                        "file:" + trainerUploadPath           // trainers
                );
    }
}
