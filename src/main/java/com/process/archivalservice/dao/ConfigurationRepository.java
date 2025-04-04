package com.process.archivalservice.dao;

import com.process.archivalservice.model.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Integer> {

    List<Configuration> findByConfigurationType(@Param("type") String configurationType);

    @Query(value = "SELECT * FROM core.configuration where CONFIGURATION_TYPE=:type and TABLE_NAME=:table", nativeQuery = true)
    Configuration findByConfigurationByTableAndType(@Param("table") String table, @Param("type") String configurationType);
}
