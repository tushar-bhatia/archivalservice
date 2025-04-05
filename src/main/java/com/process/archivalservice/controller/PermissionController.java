package com.process.archivalservice.controller;

import com.process.archivalservice.dao.PermissionRepository;
import com.process.archivalservice.dao.UserRepository;
import com.process.archivalservice.model.Permission;
import com.process.archivalservice.model.User;
import com.process.archivalservice.model.request.PermissionRequest;
import com.process.archivalservice.model.response.UserDetail;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    @Qualifier("permissionRepository")
    PermissionRepository permissionRepository;

    @Autowired
    @Qualifier("userRepository")
    UserRepository userRepository;

    @PostMapping("/grant")
    public ResponseEntity<String> grantPermission(@Valid @RequestBody PermissionRequest permissionRequest) {
        Optional<User> user = userRepository.findById(permissionRequest.getUserId());
        if(user.isEmpty()) {
            return new ResponseEntity<>("No user found with given user id!", HttpStatus.BAD_REQUEST);
        }
        Permission permission = permissionRepository.findPermissionByUserIdAndRole(permissionRequest.getUserId(), permissionRequest.getRoleName());
        if(permission != null) return new ResponseEntity<>("User already permissioned for given role", HttpStatus.OK);
        else {
            permission = Permission.builder()
                    .user(user.get())
                    .roleName(permissionRequest.getRoleName())
                    .build();
            permissionRepository.save(permission);
            return new ResponseEntity<>("Role successfully granted to user!", HttpStatus.OK);
        }
    }

    @DeleteMapping("/revoke")
    public ResponseEntity<String> revokePermission(@Valid @RequestBody PermissionRequest permissionRequest) {
        Permission permission = permissionRepository.findPermissionByUserIdAndRole(permissionRequest.getUserId(), permissionRequest.getRoleName());
        if(permission == null) return new ResponseEntity<>("User is not permissioned for this role", HttpStatus.NOT_FOUND);
        else {
            permissionRepository.deleteById(permission.getId());
            return new ResponseEntity<>("Permission revoked for the user!", HttpStatus.OK);
        }
    }


    @GetMapping("/{userId}")
    public ResponseEntity<?> getRolesForUser(
            @Valid
            @NotNull(message = "UserId must not be null")
            @Positive
            @PathVariable(name = "userId") Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()) {
            return new ResponseEntity<>("User not found with given id!", HttpStatus.BAD_REQUEST);
        }
        List<Permission> permissions = permissionRepository.findByUserId(userId);
        Set<String> roles = permissions.stream().map(Permission::getRoleName).collect(Collectors.toSet());
        UserDetail detail = new UserDetail(user.get().getId(), user.get().getName(), roles);
        return new ResponseEntity<>(detail, HttpStatus.OK);
    }
}
