package com.process.archivalservice.service;

import com.process.archivalservice.dao.PermissionRepository;
import com.process.archivalservice.dao.UserRepository;
import com.process.archivalservice.model.Permission;
import com.process.archivalservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    @Qualifier("permissionRepository")
    PermissionRepository permissionRepository;

    @Autowired
    @Qualifier("userRepository")
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByName(username);
        if (user == null) throw new UsernameNotFoundException("User not found with username: " + username);
        List<Permission> permission = permissionRepository.findByUserId(user.getId());
        Set<GrantedAuthority> authorities = permission.stream().map(p -> new SimpleGrantedAuthority(p.getRoleName())).collect(Collectors.toSet());
        return new org.springframework.security.core.userdetails.User(user.getName(), user.getPassword(), authorities);
    }
}
