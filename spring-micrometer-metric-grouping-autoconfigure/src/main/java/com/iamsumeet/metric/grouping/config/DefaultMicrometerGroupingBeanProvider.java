package com.iamsumeet.metric.grouping.config;

import com.iamsumeet.metric.grouping.api.MetricHttpClientTagsProvider;
import com.iamsumeet.metric.grouping.api.MetricHttpServerTagsProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Config to provide custom metric tags without any configuration in them.
 * Ref MicrometerGroupingTagBeansAutoConfiguration for Configuration of these TagProviders
 *
 * @author Sumeet Mondal
 */
@Configuration
public class DefaultMicrometerGroupingBeanProvider {

    @Bean
    public MetricHttpServerTagsProvider metricHttpServerTagsProvider(){
       return new MetricHttpServerTagsProvider();
    }

    @Bean
    public MetricHttpClientTagsProvider metricHttpClientTagsProvider(){
        return new MetricHttpClientTagsProvider();
    }

}
