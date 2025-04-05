package com.process.archivalservice.dao;

import com.process.archivalservice.model.Permission;
import com.process.archivalservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer>  {

    @Query(value = "SELECT * FROM core.permission where USER_ID=:userId and ROLE_NAME=:role", nativeQuery = true)
    Permission findPermissionByUserIdAndRole(@Param("userId") int userId, @Param("role") String role);

    List<Permission> findByUserId(@Param("userId") int userId);

    @Query(value = "SELECT * FROM core.permission where ROLE_NAME='admin'", nativeQuery = true)
    List<Permission> findAllAdmin();

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM core.permission where USER_ID=:userId", nativeQuery = true)
    void deleteByUserId(@Param("userId") int userId);
}
