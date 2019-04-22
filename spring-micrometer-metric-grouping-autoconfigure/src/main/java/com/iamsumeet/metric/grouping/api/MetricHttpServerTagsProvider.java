package com.iamsumeet.metric.grouping.api;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import org.springframework.boot.actuate.metrics.web.servlet.WebMvcTags;
import org.springframework.boot.actuate.metrics.web.servlet.WebMvcTagsProvider;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Set;

import static org.springframework.boot.actuate.metrics.web.servlet.WebMvcTags.*;

/**
 * Provides {@link Tag Tags} (after filtering the uris) for Spring MVC Controller which are
 * annotated with @CaptureDetailedMetric.
 * <p>
 * The uris which are annotated with @CaptureDetailedMetric are found
 * and mapped to metricEnabledUriGroups and all configured group names are mapped to groupNames
 * upon container init. additionalTagsProvider is optional
 *
 *  @author Sumeet Mondal
 */
public class MetricHttpServerTagsProvider implements WebMvcTagsProvider {

    private final String OTHERS = "others";
    private final Tag URI_OTHERS = Tag.of("uri", OTHERS);

    private Map<String, String> metricEnabledUriGroups;
    private Set<String> groupNames;
    private AdditionalTagsProvider additionalTagsProvider;

    public void initializeFields(Map<String, String> metricEnabledUriGroups, Set<String> groupNames,
                                 AdditionalTagsProvider additionalTagsProvider) {
        this.metricEnabledUriGroups = metricEnabledUriGroups;
        this.groupNames = groupNames;
        this.additionalTagsProvider = additionalTagsProvider;
    }

    @Override
    public Iterable<Tag> getTags(HttpServletRequest req, HttpServletResponse res, Object handler, Throwable ex) {
        if (isExceptionCase(ex)) {
            return getTagsWithNoFiltering(req, res, handler, ex);
        }
        return getTagsWithFiltering(req, res, handler, ex);
    }

    @Override
    public Iterable<Tag> getLongRequestTags(HttpServletRequest request, Object handler) {
        //This is only called for log running http request which as @Async, no need to filter them
        return Tags.of(new Tag[]{WebMvcTags.method(request), WebMvcTags.uri(request, null)});
    }

    private Iterable<Tag> getTagsWithNoFiltering(HttpServletRequest req, HttpServletResponse res, Object handler,
                                                 Throwable ex) {
        return Tags.of(method(req), uri(req, res), status(res))
                .and(additionalTagsProvider.getAdditionalHttpServerTags(req, res, handler, ex));
    }


    private Iterable<Tag> getTagsWithFiltering(HttpServletRequest req, HttpServletResponse res, Object handler, Throwable ex) {
        Tag uri = this.getUri(req, res);

        if ((uri == URI_OTHERS) || groupNames.contains(uri.getValue()))//groups and others will not have detailed metrics of all fields
            return Tags.of(method(req), uri, status(res))
                    .and(additionalTagsProvider.getAdditionalOthersHttpServerTags(req, res, handler, ex));

        return Tags.of(method(req), uri, status(res)).
                and(additionalTagsProvider.getAdditionalHttpServerTags(req, res, handler, ex));
    }

    private boolean isExceptionCase(Throwable ex) {
        return ex != null;
    }

    private Tag getUri(HttpServletRequest req, HttpServletResponse res) {
        String pattern = getMatchingPattern(req);
        if (pattern != null) {
            String groupOrUriName = getUriNameForPattern(pattern);
            if (groupOrUriName != null)
                return Tag.of("uri", groupOrUriName);
            return URI_OTHERS;//CaptureDetailedMetric not enabled on uri
        }
        return uri(req, res);//continue to handle where mvc not resolved, 3xx, 404 and others
    }

    private String getMatchingPattern(HttpServletRequest req) {
        //Set by spring for all matching patterns
        return (String) req.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
    }

    private String getUriNameForPattern(String pattern) {
        if (metricEnabledUriGroups != null)
            return metricEnabledUriGroups.get(pattern);
        return null;
    }

}