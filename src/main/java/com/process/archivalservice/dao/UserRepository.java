package com.process.archivalservice.dao;

import com.process.archivalservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(value = "SELECT * FROM core.USER where NAME=:name and PASSWORD=:password", nativeQuery = true)
    User findUserByNameAndPassword(@Param("name") String name, @Param("password") String password);
}
