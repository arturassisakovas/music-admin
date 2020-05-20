package com.mAdmin.config;

import com.mAdmin.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import org.springframework.security.web.session.HttpSessionEventPublisher;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    
    private static final int BCRYPT_STRENGTH = 11;

    
    @Autowired
    private AuthenticationSuccessHandler successHandler;

    
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    
    @Bean
    public static PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(BCRYPT_STRENGTH);
    }

    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(encoder());
        return authProvider;
    }

    
    @Bean
    SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    
    @Bean
    public static ServletListenerRegistrationBean httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean(new HttpSessionEventPublisher());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
        .antMatchers("/admin/**").hasAnyRole("SADMIN", "MANAGER")
        .antMatchers("/teacher/**").hasRole("COACH")
        .antMatchers("/client/**").hasRole("CLIENT");

        http.formLogin()
        .loginPage("/login")
        .failureUrl("/login?error=true")
        .successHandler(successHandler)
        .permitAll();

        http.logout().logoutUrl("/logout").logoutSuccessUrl("/");
        
        http.csrf().disable();

        http.headers().frameOptions().sameOrigin();

        http.sessionManagement()
                .maximumSessions(-1)
                .maxSessionsPreventsLogin(false)
                .expiredUrl("/logout")
                .sessionRegistry(sessionRegistry());

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

}
