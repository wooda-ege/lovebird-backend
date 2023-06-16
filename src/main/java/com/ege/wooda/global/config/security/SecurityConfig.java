package com.ege.wooda.global.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ege.wooda.global.security.jwt.filter.JwtAuthenticationFilter;
import com.ege.wooda.global.security.oauth.service.CustomOAuth2UserService;
import com.ege.wooda.global.security.oauth.service.CustomOidcUserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOidcUserService customOidcUserService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationSuccessHandler successHandler;

    @Bean
    SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()
                .headers().frameOptions().disable()
                .and()
                .authorizeHttpRequests(setAuthorizeHttpRequests())
                .oauth2Login(setOAuth2Config())
                .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    private Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> setAuthorizeHttpRequests() {
        return requests ->
                requests.requestMatchers("/auth2/**", "/api/v1/auth").permitAll()
                        .requestMatchers("/api/docs/api-docs.html").hasRole("ADMIN")
                        .requestMatchers("/api/v1/**").hasRole("MEMBER")
                        .anyRequest().authenticated();
    }

    private Customizer<OAuth2LoginConfigurer<HttpSecurity>> setOAuth2Config() {
        return oauth ->
                oauth.authorizationEndpoint(o -> o.baseUri("/auth2/authorize"))
                     .userInfoEndpoint(userInfoEndpointConfig ->
                                               userInfoEndpointConfig.userService(customOAuth2UserService)
                                                                     .oidcUserService(customOidcUserService))
                     .successHandler(successHandler);
    }
}
