package com.process.archivalservice.dao;

import com.process.archivalservice.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer>  {

    @Query(value = "SELECT * FROM core.permission where USER_NAME=:user and ROLE_NAME=:role", nativeQuery = true)
    Permission findPermissionByUserAndRole(@Param("user") String user, @Param("role") String role);

    List<Permission> findByUserName(@Param("user") String user);
}
