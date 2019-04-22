package com.iamsumeet.metric.grouping.config;

import com.iamsumeet.metric.grouping.api.AdditionalTagsProvider;
import com.iamsumeet.metric.grouping.api.CaptureDetailedMetric;
import com.iamsumeet.metric.grouping.api.MetricHttpClientTagsProvider;
import com.iamsumeet.metric.grouping.api.MetricHttpServerTagsProvider;
import com.iamsumeet.metric.grouping.properties.MicrometerGroupingProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration;
import org.springframework.boot.actuate.endpoint.web.PathMappedEndpoints;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This autoconfiguration is responsible for initializing configuration in MetricHttpServerTagsProvider
 * and MetricHttpClientTagsProvider, which are already created by DefaultMicrometerGroupingBeanProvider.
 * Bean initialization is separated from configuration because of a cyclic dependency between WebMvcTagsProvider and
 * RequestMappingHandlerMapping
 *
 * @author Sumeet Mondal
 */
@Configuration
@ConditionalOnProperty(prefix = "micrometergrouping", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(MicrometerGroupingProperties.class)
@AutoConfigureAfter({WebMvcAutoConfiguration.class})
@Import({WebEndpointAutoConfiguration.class, WebMvcAutoConfiguration.class, DefaultMicrometerGroupingBeanProvider.class})
public class MicrometerGroupingTagBeansAutoConfiguration {

    @Autowired
    MicrometerGroupingProperties micrometerGroupingProperties;

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Autowired
    private PathMappedEndpoints allMappedSystemEndpoints;

    @Autowired
    ObjectProvider<AdditionalTagsProvider> additionalTagsProvider;

    @Autowired
    private MetricHttpServerTagsProvider metricHttpServerTagsProvider;

    @Autowired
    private MetricHttpClientTagsProvider metricHttpClientTagsProvider;

    @PostConstruct
    public void configureCustomWebMvcTagsProvider() {
        MetricEnabledHttpServerGroupInfo info = createHttpServerMetricEnabledUriSet();
        metricHttpServerTagsProvider.initializeFields(info.metricEnabledUriGroups, info.configuredGroupNames,
                additionalTagsProvider.getIfAvailable(() -> new AdditionalTagsProvider() {
                }));
        metricHttpClientTagsProvider.initializeFields(micrometerGroupingProperties.getRestTemplateDetailEnabledPattern());
    }

    /**
     * Creates and returns HashMap with metric mappings and HashSet of all configured group names
     * using MetricEnabledHttpServerGroupInfo
     * <p>
     * If the controller method is mapped to a group, the the map will have entry of
     * <b>requestMappingPattern - GroupName</b> and HashSet will have entry of GroupName
     * <p/>
     * If the controller method is mapped to no group, the the map will have entry of
     * <b>requestMappingPattern - requestMappingPattern</b>, nothing will be added to HashSet
     *
     * @return A map of metric mapping groups/uri and a set of group names
     */
    private MetricEnabledHttpServerGroupInfo createHttpServerMetricEnabledUriSet() {
        //Adding all enabled actuator endpoints to log their metrics ar uri level
        Map<String, String> metricEnabledHttpServerUriGroups = new HashMap<>();
        Set<String> configuredGroupNames = new HashSet<>();
        for (String metricPath : allMappedSystemEndpoints.getAllPaths()) {
            metricEnabledHttpServerUriGroups.put(metricPath, metricPath);
        }

        //Adding all controller mappings having @CaptureDetailedMetric
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry :
                requestMappingHandlerMapping.getHandlerMethods().entrySet()) {
            HandlerMethod method = entry.getValue();
            if (method.hasMethodAnnotation(CaptureDetailedMetric.class) && method.hasMethodAnnotation(RequestMapping.class)) {
                for (String pattern : entry.getKey().getPatternsCondition().getPatterns()) {
                    String groupName = method.getMethodAnnotation(CaptureDetailedMetric.class).uriGroupName();
                    //if null then at map to uri level, else map to by groupname
                    metricEnabledHttpServerUriGroups.put(pattern, groupName.isEmpty() ? pattern : groupName);
                    configuredGroupNames.add(groupName);
                }
            }
        }
        return new MetricEnabledHttpServerGroupInfo(metricEnabledHttpServerUriGroups, configuredGroupNames);
    }

    private class MetricEnabledHttpServerGroupInfo {

        private Map<String, String> metricEnabledUriGroups;
        private Set<String> configuredGroupNames;

        private MetricEnabledHttpServerGroupInfo(Map<String, String> metricEnabledUriGroups, Set<String> configuredGroupNames) {
            this.metricEnabledUriGroups = metricEnabledUriGroups;
            this.configuredGroupNames = configuredGroupNames;
        }
    }

}
