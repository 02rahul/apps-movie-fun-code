package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
public class AppConfig {


    @Bean
    public ServletRegistrationBean actionServletRegistration(ActionServlet actionServlet) {
        return new ServletRegistrationBean(actionServlet, "/moviefun/*");
    }

    @Bean
    public DatabaseServiceCredentials databaseServiceCredentials(){
        return new DatabaseServiceCredentials(System.getenv("VCAP_SERVICES"));
    }

    @Bean(name = "albumsDataSource")
    public DataSource albumsDataSource() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(databaseServiceCredentials().jdbcUrl("albums-mysql", "p-mysql"));
        //return dataSource;
        return getDataSource(dataSource);
    }

    @Bean(name = "movieDataSource")
    public DataSource movieDataSource() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(databaseServiceCredentials().jdbcUrl("movies-mysql", "p-mysql"));
        //return dataSource;
        return getDataSource(dataSource);
    }

    @Bean
    public HibernateJpaVendorAdapter hibernateJpaVendorAdapter(){
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(true);
        vendorAdapter.setDatabase(Database.MYSQL);
        return vendorAdapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean albumsEntityManager(@Qualifier("albumsDataSource") DataSource dataSource){
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setJpaVendorAdapter(hibernateJpaVendorAdapter());
        em.setDataSource(dataSource);
        em.setPackagesToScan("org.superbiz.moviefun.albums");
        em.setPersistenceUnitName("albums");
        return em;
    }


    @Bean
    public LocalContainerEntityManagerFactoryBean moviesEntityManager(@Qualifier("movieDataSource") DataSource dataSource){
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setJpaVendorAdapter(hibernateJpaVendorAdapter());
        em.setDataSource(dataSource);
        em.setPackagesToScan("org.superbiz.moviefun.movies");
        em.setPersistenceUnitName("movies");
        return em;
    }


    @Bean
    public PlatformTransactionManager moviesTransactionManager(
            @Qualifier("moviesEntityManager") EntityManagerFactory
                    entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }

   @Bean
    public PlatformTransactionManager albumsTransactionManager(
            @Qualifier("albumsEntityManager") EntityManagerFactory
                    entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public HikariDataSource hikariDataSource(){
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDataSource(movieDataSource());
        return hikariDataSource;
    }


    @Bean
    public DataSource getDataSource(DataSource dataSource){
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDataSource(dataSource);
        return hikariDataSource;
    }

}
