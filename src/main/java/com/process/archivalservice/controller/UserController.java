package com.process.archivalservice.controller;

import com.process.archivalservice.dao.UserRepository;
import com.process.archivalservice.model.User;
import com.process.archivalservice.model.request.UpdateUserRequest;
import com.process.archivalservice.model.request.UserRequest;
import jakarta.validation.Valid;
import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RequestMapping("/user")
@RestController
public class UserController {

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

    @DeleteMapping("/delete")
    public ResponseEntity<String> updateUser(@Valid @RequestBody UserRequest userRequest) {
        User user = userRepository.findUserByNameAndPassword(userRequest.getUsername(), userRequest.getPassword());
        if(user == null) {
            return new ResponseEntity<>("Either user with name doesn't exist or you have given wrong password", HttpStatus.NOT_FOUND);
        }
        userRepository.deleteById(user.getId());
        return new ResponseEntity<>("User deleted successfully!", HttpStatus.OK);
    }

    @GetMapping("/view/all")
    public ResponseEntity<List<User>> getUserRoles() {
        return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
    }


}
