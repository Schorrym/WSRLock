package de.mariokramer.wsrlock.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.RequestMatcher;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Override
	@Autowired
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
			.withUser("q").password("q").roles("USER")
			.and().withUser("mario").password("test123").roles("USER");
	}		
	
	@Bean
	public RequestMatcher getMatcher(){
		return new CsrfSecurityRequestMatcher();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().requireCsrfProtectionMatcher(getMatcher());
		http
			.logout()
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login?logout").permitAll()
				.invalidateHttpSession(true).and()
				
			.authorizeRequests()
				.antMatchers("/resources/**").permitAll()
				.antMatchers("/admin/**").hasRole("USER")
				.anyRequest().authenticated().and()
				.formLogin().loginPage("/login").permitAll()
							.failureUrl("/login?error")
							.defaultSuccessUrl("/start");
		
		http.headers().frameOptions().sameOrigin();
	}
}
