package web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Objects;
import java.util.Properties;


@Configuration
@PropertySource("classpath:db.properties")
@EnableTransactionManagement
@ComponentScan(value = "web")
public class JpaConfig {

   private final Environment env;

   @Autowired
   public JpaConfig(Environment env) {
      this.env = env;
   }

   @Bean
   public DataSource getDataSource() {
      DriverManagerDataSource dataSource = new DriverManagerDataSource();
      dataSource.setDriverClassName(Objects.requireNonNull(env.getProperty("db.driver")));
      dataSource.setUrl(env.getProperty("db.url"));
      dataSource.setUsername(env.getProperty("db.username"));
      dataSource.setPassword(env.getProperty("db.password"));
      return dataSource;
   }

   @Bean
   public HibernateJpaVendorAdapter getJpaVendorAdapter() {
      HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
      jpaVendorAdapter.setShowSql(true);
      return jpaVendorAdapter;
   }

   @Bean
   public LocalContainerEntityManagerFactoryBean getEntityManagerFactory() {
       LocalContainerEntityManagerFactoryBean entityManagerBean = new LocalContainerEntityManagerFactoryBean();
       entityManagerBean.setDataSource(getDataSource());
       entityManagerBean.setPackagesToScan("web");
       entityManagerBean.setJpaVendorAdapter(getJpaVendorAdapter());

       Properties properties = new Properties();
       properties.setProperty("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
       properties.setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
       properties.setProperty("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
       entityManagerBean.setJpaProperties(properties);

       return entityManagerBean;
   }

   @Bean
   public JpaTransactionManager getTransactionManager() {
      JpaTransactionManager transactionManager = new JpaTransactionManager();
      transactionManager.setEntityManagerFactory(getEntityManagerFactory().getObject());
      return transactionManager;
   }
}
