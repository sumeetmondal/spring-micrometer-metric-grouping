package com.iamsumeet.metric.grouping.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configure restTemplateDetailEnabledPattern as a regex to filter rest template calls which needs to be captured in metric.
 * null is default and WILL group all calls. Configure it to (.*) and this will capture all calls without any grouping
 *
 * @author Sumeet Mondal
 */
@ConfigurationProperties(prefix = "micrometergrouping")
public class MicrometerGroupingProperties {

    private Boolean enabled = true;

    private String restTemplateDetailEnabledPattern;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getRestTemplateDetailEnabledPattern() {
        return restTemplateDetailEnabledPattern;
    }

    public void setRestTemplateDetailEnabledPattern(String restTemplateDetailEnabledPattern) {
        this.restTemplateDetailEnabledPattern = restTemplateDetailEnabledPattern;
    }
}
