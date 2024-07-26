package raihanhori.auction_api.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzJobConfiguration {
	
//	@Bean
//    public AutowiringSpringBeanJobFactory springBeanJobFactory(AutowireCapableBeanFactory beanFactory) {
//        return new AutowiringSpringBeanJobFactory(beanFactory);
//    }

	@Bean
	public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource) {
		SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
		schedulerFactory.setDataSource(dataSource);
		schedulerFactory.setApplicationContextSchedulerContextKey("applicationContext");
		
		return schedulerFactory;
	}
	
}
