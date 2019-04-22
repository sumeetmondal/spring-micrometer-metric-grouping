package com.iamsumeet.metric.grouping.api;

import io.micrometer.core.instrument.Tags;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This can be implemented to add any additional tags neeed in the metric
 *
 * @author Sumeet Mondal
 */
public interface AdditionalTagsProvider {

    /**
     * This method should return additional tags which needs to be part of metric, this will be called for all cases where
     * detailed metric is being captured
     */
    default Tags getAdditionalHttpServerTags(HttpServletRequest req, HttpServletResponse res, Object handler, Throwable ex) {
        return null;
    }

    /**
     * This method should return additional tags for OTHER cases, i.e. for grouped cases. This will be called for to get additional tags
     * for all cases where detailed metric is NOT being captured, and metrics are being grouped
     */
    default Tags getAdditionalOthersHttpServerTags(HttpServletRequest req, HttpServletResponse res, Object handler, Throwable ex) {
        return null;
    }

    /**
     * Logic making use of this method is not implemented yet, you can modify MetricHttpClientTagsProvider to make it work
     */
    default Tags getAdditionalHttpClientTags(String urlTemplate, HttpRequest request, ClientHttpResponse response) {
        return null;
    }

    /**
     * Logic making use of this method is not implemented yet, you can modify MetricHttpClientTagsProvider to make it work
     */
    default Tags getAdditionalOthersHttpClientTags(String urlTemplate, HttpRequest request, ClientHttpResponse response) {
        return null;
    }
}
