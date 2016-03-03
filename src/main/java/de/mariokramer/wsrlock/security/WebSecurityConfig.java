package de.mariokramer.wsrlock.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.session.SessionRepository;
import org.springframework.session.web.http.SessionRepositoryFilter;

import de.mariokramer.wsrlock.config.db.DatabaseJPAConfig;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Override
	@Autowired
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
			.withUser("q").password("q").roles("USER")
			.and().withUser("mario").password("test123").roles("USER");
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
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
//			.and()
//				.portMapper().http(8080).mapsTo(8443);
	}
}
