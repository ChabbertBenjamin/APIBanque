package fr.miage.bank.security;

import fr.miage.bank.filters.CustomAuthenticationFilter;
import fr.miage.bank.filters.CustomAuthorizationFilter;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final Argon2PasswordEncoder argon2PasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(userDetailsService).passwordEncoder(argon2PasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception{
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean());
        customAuthenticationFilter.setFilterProcessesUrl("/login");
        httpSecurity.csrf().disable();

        httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        httpSecurity.authorizeRequests().antMatchers("/login/**", "/token/refresh/**").permitAll();
        httpSecurity.authorizeRequests().antMatchers("/carts/**").denyAll();
        httpSecurity.authorizeRequests().antMatchers("/operations/**").denyAll();
        httpSecurity.authorizeRequests().antMatchers("/accounts/**").denyAll();
        httpSecurity.authorizeRequests().antMatchers("/paiements/**").denyAll();
        httpSecurity.authorizeRequests().antMatchers("/users/").denyAll();
        httpSecurity.authorizeRequests().antMatchers("/carts").denyAll();
        httpSecurity.authorizeRequests().antMatchers("/operations").denyAll();
        httpSecurity.authorizeRequests().antMatchers("/accounts").denyAll();
        httpSecurity.authorizeRequests().antMatchers("/paiements").denyAll();
        httpSecurity.authorizeRequests().antMatchers("/users").denyAll();

        httpSecurity.authorizeRequests().anyRequest().permitAll();

        httpSecurity.addFilter(customAuthenticationFilter);
        httpSecurity.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
