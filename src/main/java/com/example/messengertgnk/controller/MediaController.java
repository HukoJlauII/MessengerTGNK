package com.example.messengertgnk.controller;

import com.example.messengertgnk.entity.Media;
import com.example.messengertgnk.entity.User;
import com.example.messengertgnk.service.MediaService;
import com.example.messengertgnk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Principal;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/media")
public class MediaController {

    @Autowired
    private MediaService mediaService;

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    private ResponseEntity<?> getImageById(@PathVariable Long id) {
        Media image = mediaService.findMediaById(id).orElse(null);
        return ResponseEntity.ok()
                .header("fileName", image.getOriginalFileName())
                .contentType(MediaType.valueOf(image.getMediaType()))
                .contentLength(image.getSize())
                .body(new InputStreamResource(new ByteArrayInputStream(image.getBytes())));
    }

    @PostMapping("/upload")
    public ResponseEntity<?> loadMedia(Principal principal, @RequestParam(value = "file") MultipartFile multipartFile,@RequestParam(value = "user", required=false ) String json) throws IOException {
        User user = userService.getUserAuth(principal);
        if (multipartFile != null) {
            Media media = Media.builder()
                    .originalFileName(multipartFile.getOriginalFilename())
                    .mediaType(multipartFile.getContentType())
                    .size(multipartFile.getSize())
                    .bytes(multipartFile.getBytes()).build();
            user.setAvatar(media);
            userService.save(user);
        }
        return new ResponseEntity<>(userService.mapToInfoDto(user), HttpStatus.CREATED);
    }
}
