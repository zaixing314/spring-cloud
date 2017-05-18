package com.sdj.lzx.spring;


import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.sdj.lzx.spring.listener.MyApplicationEnvironmentPreparedEventListener;
import com.sdj.lzx.spring.listener.MyApplicationFailedEventListener;
import com.sdj.lzx.spring.listener.MyApplicationPreparedEventListener;
import com.sdj.lzx.spring.listener.MyApplicationStartedEventListener;

@SpringBootApplication
//@EnableEurekaServer
//@EnableHystrixDashboard
@MapperScan("com.sdj.lzx.spring.mybatis.mapper")
public class SpringBoot {
	
	
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource(){
		return  new org.apache.tomcat.jdbc.pool.DataSource();
	}
	
	@Bean
	public SqlSessionFactory sqlSessionFactoryBean() throws Exception{
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource());
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:/mybatis/*.xml"));
		return sqlSessionFactoryBean.getObject();
	}
	
	@Bean
	public PlatformTransactionManager transactionManager(){
		return new DataSourceTransactionManager(dataSource());
	}
	
	public static void main(String[] args){
		//SpringApplication.run(SpringBoot.class, args).addApplicationListener(new MyApplicationStartedEventListener());
		//new SpringApplicationBuilder(Application.class).web(true).run(args);
		SpringApplication application = new SpringApplication(SpringBoot.class);
		application.addListeners(new MyApplicationStartedEventListener(), 
				new MyApplicationEnvironmentPreparedEventListener(), 
				new MyApplicationPreparedEventListener(), 
				new MyApplicationFailedEventListener());
	}
}
