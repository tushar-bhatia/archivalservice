package com.process.archivalservice.controller;

import com.process.archivalservice.dao.ConfigurationRepository;
import com.process.archivalservice.model.request.ConfigRequest;
import com.process.archivalservice.model.Configuration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/config")
public class ConfigurationController {

    @Autowired
    @Qualifier("configurationRepository")
    ConfigurationRepository configurationRepository;

    @PostMapping("/create")
    public ResponseEntity<String> insertOrUpdateConfiguration(@Valid @RequestBody ConfigRequest configRequest) {
        Configuration existingConfig = null;
        Integer id = null;
        if(configRequest.getId()==null) {
            existingConfig = configurationRepository.findByConfigurationByTableAndType(configRequest.getTableName(), configRequest.getConfigurationType());
            if(existingConfig != null) {
                id = existingConfig.getId();
            }
        } else id = configRequest.getId();
        Configuration config = Configuration.builder()
                .id(id)
                .tableName(configRequest.getTableName())
                .configurationType(configRequest.getConfigurationType())
                .years(configRequest.getYears())
                .months(configRequest.getMonths())
                .weeks(configRequest.getWeeks())
                .months(configRequest.getMonths())
                .days(configRequest.getDays())
                .hours(configRequest.getHours())
                .minutes(configRequest.getMinutes())
                .build();
        configurationRepository.save(config);
        return new ResponseEntity<>("Configuration Inserted successfully!", HttpStatus.CREATED);
    }

    @GetMapping("/view")
    public List<Configuration> getConfigurationsByType(@Valid @RequestParam(name = "type")
           @Pattern(regexp = "ARCHIVAL|DELETION", message = "Configuration Type must be either ARCHIVAL or DELETION") String type) {
        return configurationRepository.findByConfigurationType(type);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteConfigurationsById(@Valid @PathVariable(name = "id") @Positive Integer id) {
        if(configurationRepository.existsById(id)) {
            configurationRepository.deleteById(id);
            return new ResponseEntity<>("Configuration deleted successfully!", HttpStatus.OK);
        }
        return new ResponseEntity<>("No configuration exists with id="+id, HttpStatus.NOT_FOUND);
    }
}
