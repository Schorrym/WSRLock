package de.mariokramer.wsrlock.config.db;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
public class DatabaseJPAConfig {

	@Bean
	public DataSource generateDataSource() {
		DriverManagerDataSource driverManager = new DriverManagerDataSource();
		driverManager.setDriverClassName("org.postgresql.Driver");
		driverManager.setUrl("jdbc:postgresql:wsrlock");
		driverManager.setUsername("wsrlock");
		driverManager.setPassword("wsrlock");
		
		return driverManager;
	}
	
	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setDatabase(Database.POSTGRESQL);
		vendorAdapter.setGenerateDdl(true);
		vendorAdapter.setShowSql(true);
		
		return vendorAdapter;
	}
	
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {		
		LocalContainerEntityManagerFactoryBean entityManagerFB = new LocalContainerEntityManagerFactoryBean();
		entityManagerFB.setJpaVendorAdapter(jpaVendorAdapter());
		entityManagerFB.setDataSource(generateDataSource());
		entityManagerFB.setPackagesToScan("de.mariokramer.wsrlock.model");
		
		return entityManagerFB;		
	}
	
	@Bean
	@Autowired
	public JpaTransactionManager transactionManager() {
		JpaTransactionManager txManager = new JpaTransactionManager();
		txManager.setEntityManagerFactory(entityManagerFactory().getObject());
		
		return txManager;
	}
}
