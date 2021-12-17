package ru.ustinov.voting.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.session.*;
import ru.ustinov.voting.model.Role;
import ru.ustinov.voting.model.User;
import ru.ustinov.voting.repository.UserRepository;
import ru.ustinov.voting.web.AuthUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.ustinov.voting.util.UserUtil.PASSWORD_ENCODER;


@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Slf4j
@AllArgsConstructor
public class WebSecurityConfig {

    private final UserRepository userRepository;

    @Bean
    static MySimpleUrlAuthantication mySimpleUrlAuthantication() {
        return new MySimpleUrlAuthantication();
    }

    @Bean
    public static SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public static CompositeSessionAuthenticationStrategy concurrentSession() {
        ConcurrentSessionControlAuthenticationStrategy concurrentAuthenticationStrategy = new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry());
        List<SessionAuthenticationStrategy> delegateStrategies = new ArrayList<>();
        delegateStrategies.add(concurrentAuthenticationStrategy);
        delegateStrategies.add(new SessionFixationProtectionStrategy());
        delegateStrategies.add(new RegisterSessionAuthenticationStrategy(sessionRegistry()));
        return new CompositeSessionAuthenticationStrategy(delegateStrategies);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            log.debug("Authenticating '{}'", email);
            Optional<User> optionalUser = userRepository.getByEmail(email);
            return new AuthUser(optionalUser.orElseThrow(
                    () -> new UsernameNotFoundException("User '" + email + "' was not found")));
        };
    }

//    @Bean
//    public HttpSessionEventPublisher httpSessionEventPublisher() {
//        return new HttpSessionEventPublisher();
//    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService())
                .passwordEncoder(PASSWORD_ENCODER);
    }

    @Order(2)
    @Configuration
    public static class RestSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/rest/**")
                    .authorizeRequests()
                    .antMatchers("/rest/admin/**").hasRole(Role.ADMIN.name())
                    .antMatchers("/rest/voting/**").authenticated()
                    .antMatchers(HttpMethod.POST, "/rest/profile").anonymous()
                    .antMatchers("/**").authenticated()
                    .and().httpBasic()
                    .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and().csrf().disable();
        }
    }

    @Order(3)
    @Configuration
    public static class UISecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        private final String[] swaggerPatterns = {"/swagger-ui.html",
                "/swagger-resources/**", "/v3/api-docs/**"};

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.sessionManagement().sessionAuthenticationStrategy(concurrentSession())
                    .maximumSessions(-1);
            http.antMatcher("/**").authorizeRequests()
                    .antMatchers("/login").permitAll()
                    .antMatchers("/profile/register").anonymous()
                    .antMatchers("/profile/**").authenticated()
                    .antMatchers("/admin/**").hasRole("ADMIN")
                    .antMatchers("/swagger-ui/**").permitAll()
                    .antMatchers("/**").authenticated()
                    .and().formLogin().loginPage("/login")
                    .loginProcessingUrl("/spring_security_check")
                    .failureUrl("/login?error=true")
                    .successHandler(mySimpleUrlAuthantication())
                    .and().logout().logoutSuccessUrl("/login");
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().antMatchers("/webjars/**", "/", "/resources/**")
                    .antMatchers(swaggerPatterns);
        }
    }
}