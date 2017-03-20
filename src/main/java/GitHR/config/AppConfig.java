package GitHR.config;

import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.GsonFactoryBean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AppConfig {
    @Bean
    public ThreadPoolTaskExecutor taskExecutorAPICalls() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(10);
//        threadPoolTaskExecutor.setMaxPoolSize(20);
//        threadPoolTaskExecutor.setKeepAliveSeconds(10);
//        By default quehas
//        threadPoolTaskExecutor.setQueueCapacity(1000);
        threadPoolTaskExecutor.setThreadNamePrefix("GitHubTaskExecutor-");
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    @Bean
    public Gson gsonFactoryBean() {
        GsonFactoryBean gsonFactoryBean = new GsonFactoryBean();
        gsonFactoryBean.setPrettyPrinting(true);
        return gsonFactoryBean.getObject();
    }

}