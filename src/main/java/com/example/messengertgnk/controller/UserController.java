package com.example.messengertgnk.controller;

import com.example.messengertgnk.dto.ChangePasswordDto;
import com.example.messengertgnk.dto.ChangeUserDto;
import com.example.messengertgnk.entity.Media;
import com.example.messengertgnk.entity.User;
import com.example.messengertgnk.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.Validator;
import java.io.IOException;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/profile")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    Validator validator;


    @PutMapping("/changePassword")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto, BindingResult bindingResult, Authentication authentication) {

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getFieldErrors(), HttpStatus.CONFLICT);
        }
        return userService.changeUserPassword(changePasswordDto, bindingResult, authentication);

    }

    @PutMapping("/changeInfo")
    public ResponseEntity<?> changeUserInfo(Authentication authentication, @RequestParam(value = "file", required = false) MultipartFile multipartFile, @RequestParam(value = "user", required = false) String changeUserInfo) throws IOException {
        User user = userService.getUserAuth(authentication);
        ChangeUserDto changeUserDto = new ObjectMapper().readValue(changeUserInfo, ChangeUserDto.class);
        SpringValidatorAdapter springValidator = new SpringValidatorAdapter(validator);
        BindingResult bindingResult = new BeanPropertyBindingResult(changeUserDto, "changeUserDtoResult");
        springValidator.validate(changeUserDto, bindingResult);
        if (!changeUserDto.getEmail().equals(user.getEmail()) && userService.existByEmail(changeUserDto.getEmail())) {
            bindingResult.addError(new FieldError("user", "email", "Пользователь с такой почтой уже существует"));
        }
        if (!changeUserDto.getUsername().equals(user.getUsername()) && userService.existsByUsername(changeUserDto.getUsername())) {
            bindingResult.addError(new FieldError("user", "username", "Пользователь с таким именем уже существует"));
        }
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getFieldErrors(), HttpStatus.CONFLICT);
        }
        if (!changeUserDto.getUsername().equals(user.getUsername())) {
            user.setUsername(changeUserDto.getUsername());
            authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        user.setName(changeUserDto.getName());
        user.setSurname(changeUserDto.getSurname());
        user.setEmail(changeUserDto.getEmail());
        if (multipartFile != null) {
            Media media = Media.builder()
                    .originalFileName(multipartFile.getOriginalFilename())
                    .mediaType(multipartFile.getContentType())
                    .size(multipartFile.getSize())
                    .bytes(multipartFile.getBytes()).build();
            user.setAvatar(media);
        }
        userService.save(user);
        return new ResponseEntity<>(userService.mapToInfoDto(user), HttpStatus.OK);
    }
}
