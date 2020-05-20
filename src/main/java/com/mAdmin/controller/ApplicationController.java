package com.mAdmin.controller;

import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Optional;


@Controller
@Scope("application")
public class ApplicationController {

    
    private final Optional<BuildProperties> optionalBuildProperties;

    
    public ApplicationController(Optional<BuildProperties> buildProperties) {
        this.optionalBuildProperties = buildProperties;
    }

    
    public String getBuildVersion() {
        return optionalBuildProperties.map(BuildProperties::getVersion).orElse(null);
    }

}
