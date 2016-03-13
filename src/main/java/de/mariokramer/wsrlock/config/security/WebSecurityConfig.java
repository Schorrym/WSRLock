package de.mariokramer.wsrlock.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.RequestMatcher;

import de.mariokramer.wsrlock.persistence.CustomUserDetailsService;

@EnableWebSecurity
@Configuration
@ComponentScan(basePackageClasses = CustomUserDetailsService.class)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	UserDetailsService userDetailsService;
	
	@Autowired
	public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}
	
	@Bean(name = "passwordEncoder")
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean(name = "getMatcher")
	public RequestMatcher getMatcher() {
		return new CsrfSecurityRequestMatcher();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().requireCsrfProtectionMatcher(getMatcher());
		http.logout().logoutUrl("/logout").logoutSuccessUrl("/login?logout").permitAll().invalidateHttpSession(true)
				.and()
				.authorizeRequests().antMatchers("/resources/**").permitAll().antMatchers("/admin/**").hasRole("USER").anyRequest().authenticated()
				.and()
				.formLogin().loginPage("/login").permitAll().failureUrl("/login?error").defaultSuccessUrl("/start");
		http.headers().frameOptions().sameOrigin();
	}
}
