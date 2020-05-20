package com.mAdmin.component;

import com.mAdmin.util.PrimeFacesWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.ApplicationScope;


@Configuration
public class PrimeFacesWrapperProvider {

    
    @Bean
    @ApplicationScope
    public PrimeFacesWrapper primeFaces() {
        return new PrimeFacesWrapper();
    }

}
