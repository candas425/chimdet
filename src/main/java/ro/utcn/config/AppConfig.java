package ro.utcn.config;


import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;


import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by lucian.davidescu on 7/11/2016.
 * <p>
 * This class is used for Spring Injection
 */


@Configuration
@ComponentScan("ro.utcn")
@EnableTransactionManagement
@PropertySource("classpath:config/configurations.properties")
@EnableJpaRepositories(basePackages = "ro.utcn.backend.repositories")
public class AppConfig implements ApplicationContextAware {

    @Autowired
    private Environment environment;

    private ApplicationContext applicationContext;

    /**
     * Used to create and configure our sessionFactory from the properties file.
     *
     * @return a configured LocalSessionFactoryBean.
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("ro.utcn.backend.model");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(hibernateProperties());

        return em;
    }

    /**
     * This method is used to create the properties for hibernate from properties file
     *
     * @return properties used to configure our sessionFactory
     */
    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", environment.getRequiredProperty("hibernate.dialect"));
        properties.put("hibernate.hbm2ddl.auto", environment.getRequiredProperty("hibernate.update"));
        properties.put("hibernate.update", environment.getRequiredProperty("hibernate.update"));
        properties.put("hibernate.show_sql", environment.getRequiredProperty("hibernate.show_sql"));
        properties.put("hibernate.format_sql", environment.getRequiredProperty("hibernate.format_sql"));
        properties.put("packagesToScan", "ro.utcn.backend.model");
        return properties;
    }

    /**
     * Used to configure our dataSource used in the sessionFactory.
     *
     * @return a dataSource used to configure our sessionFactory.
     */
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(environment.getRequiredProperty("jdbc.driverClassName"));
        dataSource.setUrl(environment.getRequiredProperty("jdbc.url"));
        dataSource.setUsername(environment.getRequiredProperty("jdbc.username"));
        dataSource.setPassword(environment.getRequiredProperty("jdbc.password"));
        return dataSource;
    }

    /**
     * General application window width
     *
     * @return - size
     */
    @Bean
    public int windowWidth() {
        return Integer.parseInt(environment.getProperty("window.width"));
    }

    /**
     * General application window height
     *
     * @return - size
     */
    @Bean
    public int windowHeight() {
        return Integer.parseInt(environment.getProperty("window.height"));
    }

    @Bean
    public String userUsername() {
        return environment.getProperty("username.admin");
    }

    @Bean
    public String userPassword() {
        return environment.getProperty("password.admin");
    }

    @Bean(name = "transactionManager")
    public JpaTransactionManager jpaTransactionManager() {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        jpaTransactionManager.setDataSource(dataSource());

        return jpaTransactionManager;
    }

    /**
     * This method is used to return properties from properties file for a mail server connection
     *
     * @return properties setted
     */
    @Bean
    public Properties mailProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", environment.getRequiredProperty("mail.authEnable"));
        properties.put("mail.smtp.starttls.enable", environment.getRequiredProperty("mail.smtpStarttlsEnable"));
        properties.put("mail.smtp.host", environment.getRequiredProperty("mail.host"));
        properties.put("mail.smtp.port", environment.getRequiredProperty("mail.port"));
        properties.put("mail.from", environment.getRequiredProperty("mail.from"));
        properties.put("mail.username", environment.getRequiredProperty("mail.username"));
        properties.put("mail.password", environment.getRequiredProperty("mail.password"));
        return properties;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}

