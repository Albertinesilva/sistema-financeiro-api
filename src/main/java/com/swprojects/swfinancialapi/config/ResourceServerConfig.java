package com.swprojects.swfinancialapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

  @Override
  public void configure(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .antMatchers("/categorias").permitAll()
            .antMatchers("/oauth2/token", "/oauth2/introspect").permitAll()
            .anyRequest().authenticated())
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(csrf -> csrf.disable())
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
  }

  @Bean
  public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    // Configuração para AuthenticationManager
    AuthenticationManagerBuilder authenticationManagerBuilder = http
        .getSharedObject(AuthenticationManagerBuilder.class);
    authenticationManagerBuilder.inMemoryAuthentication()
        .passwordEncoder(passwordEncoder())
        .withUser("admin@algamoney.com")
        .password(passwordEncoder().encode("admin"))
        .roles("ADMIN");
    return authenticationManagerBuilder.build();
  }

  @Bean
  public JwtDecoder jwtDecoder() {
    var secretKey = new SecretKeySpec("3032885ba9cd6621bcc4e7d6b6c35c2b".getBytes(), "HmacSHA256");

    return NimbusJwtDecoder.withSecretKey(secretKey).build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(); // Usando BCrypt para codificação de senhas
  }

  private JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
      List<String> authorities = jwt.getClaimAsStringList("authorities");

      if (authorities == null) {
        authorities = Collections.emptyList();
      }

      JwtGrantedAuthoritiesConverter scopesAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
      Collection<GrantedAuthority> grantedAuthorities = scopesAuthoritiesConverter.convert(jwt);

      grantedAuthorities.addAll(authorities.stream()
          .map(SimpleGrantedAuthority::new)
          .collect(Collectors.toList()));

      return grantedAuthorities;
    });

    return jwtAuthenticationConverter;
  }

}
