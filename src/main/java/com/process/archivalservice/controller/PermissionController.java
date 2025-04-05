package com.process.archivalservice.controller;

import com.process.archivalservice.dao.PermissionRepository;
import com.process.archivalservice.model.Permission;
import com.process.archivalservice.model.request.PermissionRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    @Qualifier("permissionRepository")
    PermissionRepository permissionRepository;

    @PostMapping("/grant")
    public ResponseEntity<String> grantPermission(@Valid @RequestBody PermissionRequest permissionRequest) {
        Permission permission = permissionRepository.findPermissionByUserAndRole(permissionRequest.getUserName(), permissionRequest.getRoleName());
        if(permission != null) return new ResponseEntity<>("User already permissioned for given role", HttpStatus.OK);
        else {
            permission = Permission.builder()
                    .userName(permissionRequest.getUserName())
                    .roleName(permissionRequest.getRoleName())
                    .build();
            permissionRepository.save(permission);
            return new ResponseEntity<>("Role successfully granted to user!", HttpStatus.OK);
        }
    }

    @DeleteMapping("/revoke")
    public ResponseEntity<String> revokePermission(@Valid @RequestBody PermissionRequest permissionRequest) {
        Permission permission = permissionRepository.findPermissionByUserAndRole(permissionRequest.getUserName(), permissionRequest.getRoleName());
        if(permission == null) return new ResponseEntity<>("User is not permissioned for this role", HttpStatus.NOT_FOUND);
        else {
            permissionRepository.deleteById(permission.getId());
            return new ResponseEntity<>("Permission revoked for the user!", HttpStatus.OK);
        }
    }


    @GetMapping("/{user}")
    public Set<String> getPermissionsForUser(
            @NotNull(message = "User must not be null")
            @NotEmpty(message = "User must not be empty")
            @NotBlank(message = "User must not be blank")
            @PathVariable(name = "user") String user) {
        return permissionRepository.findByUserName(user);
    }
}
