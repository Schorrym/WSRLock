package de.mariokramer.wsrlock.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan(basePackages= {"de.mariokramer.wsrlock"},
				excludeFilters= {@Filter(type=FilterType.ANNOTATION, value=EnableWebMvc.class)})

public class SecurityRootConfig extends AbstractSecurityWebApplicationInitializer {
	
}
