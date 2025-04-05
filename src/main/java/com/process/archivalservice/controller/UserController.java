package com.process.archivalservice.controller;

import com.process.archivalservice.dao.PermissionRepository;
import com.process.archivalservice.dao.UserRepository;
import com.process.archivalservice.model.Permission;
import com.process.archivalservice.model.User;
import com.process.archivalservice.model.request.UpdateUserRequest;
import com.process.archivalservice.model.request.UserRequest;
import com.process.archivalservice.model.response.UserDetail;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("/user")
@RestController
public class UserController {

    @Autowired
    @Qualifier("permissionRepository")
    PermissionRepository permissionRepository;

    @Autowired
    @Qualifier("userRepository")
    UserRepository userRepository;



    @PostMapping("/create")
    public ResponseEntity<String> createUser(@Valid @RequestBody UserRequest userRequest) {
        User user = userRepository.findUserByNameAndPassword(userRequest.getUsername(), userRequest.getPassword());
        if(user != null) {
            return new ResponseEntity<>("User already exist!", HttpStatus.FOUND);
        }
        user = User.builder()
                .name(userRequest.getUsername())
                .password(userRequest.getPassword())
                .build();
        userRepository.save(user);
        return new ResponseEntity<>("User Created successfully!", HttpStatus.CREATED);
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateUser(@Valid @RequestBody UpdateUserRequest updateUserRequest) {
        User user = userRepository.findUserByNameAndPassword(updateUserRequest.getUsername(), updateUserRequest.getCurrentPassword());
        if(user == null) {
            return new ResponseEntity<>("Either user with name doesn't exist or you have given wrong password", HttpStatus.NOT_FOUND);
        }
        user.setPassword(updateUserRequest.getNewPassword());
        userRepository.save(user);
        return new ResponseEntity<>("User updated successfully!", HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> updateUser(@Valid @PathVariable(name = "id") @Positive Integer id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()) {
            return new ResponseEntity<>("User with given id does not exist!", HttpStatus.NOT_FOUND);
        }
        userRepository.deleteById(user.get().getId());
        return new ResponseEntity<>("User deleted successfully!", HttpStatus.OK);
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<?> getUserDetails(@Valid @PathVariable(name = "id") @Positive Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()) {
            return new ResponseEntity<>("User not found with given id!", HttpStatus.BAD_REQUEST);
        }
        List<Permission> permissions = permissionRepository.findByUserId(userId);
        Set<String> roles = permissions.stream().map(Permission::getRoleName).collect(Collectors.toSet());
        UserDetail detail = new UserDetail(user.get().getId(), user.get().getName(), roles);
        return new ResponseEntity<>(detail, HttpStatus.OK);
    }


    @GetMapping("/view")
    public ResponseEntity<?> getUserAllDetails() {
        List<User> users = userRepository.findAll();
        if(users.isEmpty()) {
            return new ResponseEntity<>("No Users exist in teh system!", HttpStatus.NOT_FOUND);
        }
        Set<UserDetail> userDetails = users.stream().map(user -> {
            List<Permission> permissions = permissionRepository.findByUserId(user.getId());
            Set<String> roles = permissions.stream().map(Permission::getRoleName).collect(Collectors.toSet());
            return new UserDetail(user.getId(), user.getName(), roles);
        }).collect(Collectors.toSet());
        return new ResponseEntity<>(userDetails, HttpStatus.OK);
    }


}
