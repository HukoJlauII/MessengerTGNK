package com.example.messengertgnk.service;

import com.example.messengertgnk.configuration.JWT.JWTUtil;
import com.example.messengertgnk.dto.CredentialsDto;
import com.example.messengertgnk.dto.UserRegisterDto;
import com.example.messengertgnk.entity.Role;
import com.example.messengertgnk.entity.User;
import com.example.messengertgnk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;


@Service
@Transactional
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private JWTUtil jwtUtil;

    public void save(User user) {
        userRepository.save(user);
    }

    public ResponseEntity<?> validateRegister(UserRegisterDto userRegisterDto, BindingResult bindingResult) {
        if (!userRegisterDto.getPassword().equals(userRegisterDto.getPasswordConfirm())) {
            bindingResult.addError(new FieldError("user", "passwordConfirm", "Пароли не совпадают"));
        }
        if (existsByUsername(userRegisterDto.getUsername())) {
            bindingResult.addError(new FieldError("user", "username", "Пользователь с таким никнеймом уже существует"));
        }
        if (existsByEmail(userRegisterDto.getEmail())) {
            bindingResult.addError(new FieldError("user", "email", "Пользователь с такой почтой уже существует"));
        }
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getFieldErrors(), HttpStatus.CONFLICT);
        } else {
            registerUser(userRegisterDto);
            String token = jwtUtil.generateToken(userRegisterDto.getUsername());

            return new ResponseEntity<>(Collections.singletonMap("jwt-token", token), HttpStatus.ACCEPTED);
        }
    }

    public void registerUser(UserRegisterDto userRegisterDto) {
        User user = User.builder()
                .name(userRegisterDto.getName())
                .surname(userRegisterDto.getSurname())
                .username(userRegisterDto.getUsername())
                .email(userRegisterDto.getEmail())
                .roles(Set.of(Role.ROLE_USER))
                .password(passwordEncoder.encode(userRegisterDto.getPassword()))
                .registrationDate(LocalDate.now())
                .build();
        save(user);
    }

    public ResponseEntity<?> loginUser(CredentialsDto credentialsDto) {
        try {
            UsernamePasswordAuthenticationToken authInputToken =
                    new UsernamePasswordAuthenticationToken(credentialsDto.getUsername(), credentialsDto.getPassword());

            authenticationManager.authenticate(authInputToken);

            String token = jwtUtil.generateToken(credentialsDto.getUsername());

            return new ResponseEntity<>(Collections.singletonMap("jwt-token", token), HttpStatus.ACCEPTED);
        } catch (AuthenticationException authExc) {
            throw new RuntimeException("Invalid Login Credentials");
        }
    }


    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException("No user with username = " + username));
    }
}
