package com.swprojects.swfinancialapi.config;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

@SuppressWarnings("deprecation")
@Profile("oauth-security")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private UserDetailsService userDetailsService;

  @Override
  public void configure(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .antMatchers("/categorias").permitAll()
            .anyRequest().authenticated())
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(csrf -> csrf.disable())
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
  }

  @Bean
  @Override
  protected AuthenticationManager authenticationManager() throws Exception {
    return super.authenticationManager();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
  }

  // @Bean
  // public AuthenticationManager authenticationManager(HttpSecurity http) throws
  // Exception {
  // AuthenticationManagerBuilder authenticationManagerBuilder = http
  // .getSharedObject(AuthenticationManagerBuilder.class);

  // authenticationManagerBuilder
  // .userDetailsService(userDetailsService()) // Usa o seu bean aqui
  // .passwordEncoder(passwordEncoder());

  // return authenticationManagerBuilder.build();
  // }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(); // Usando BCrypt para codificação de senhas
  }

  @Bean
  public JwtDecoder jwtDecoder() {
    var secretKey = new SecretKeySpec("3032885ba9cd6621bcc4e7d6b6c35c2b".getBytes(), "HmacSHA256");
    return NimbusJwtDecoder.withSecretKey(secretKey).build();
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
