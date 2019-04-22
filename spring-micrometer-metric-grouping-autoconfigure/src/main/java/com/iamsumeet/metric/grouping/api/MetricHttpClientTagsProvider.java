package com.iamsumeet.metric.grouping.api;

import io.micrometer.core.instrument.Tag;
import org.springframework.boot.actuate.metrics.web.client.RestTemplateExchangeTagsProvider;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static org.springframework.boot.actuate.metrics.web.client.RestTemplateExchangeTags.*;

/**
 * Provides {@link Tag Tags} for an exchange performed by a {@link RestTemplate}.
 * <p>
 * Before providing the Tags, a filtering process will occur which will group
 * all uris to URI_OTHERS and CLIENT_OTHERS which are not matching the pattern
 * defined by restTemplateDetailEnabledPattern.
 * <p>
 * In case of an exception from client, this filtering process is skipped.
 * Caching has been implemented to improve performance for client calls.
 *
 *  @author Sumeet Mondal
 */
public class MetricHttpClientTagsProvider implements RestTemplateExchangeTagsProvider {

    private final String OTHERS = "others";
    private final Tag URI_OTHERS = Tag.of("uri", OTHERS);
    private final Tag CLIENT_OTHERS = Tag.of("clientName", OTHERS);
    private Pattern restTemplateDetailEnabledPattern;
    private final Pattern STRIP_URI_PATTERN = Pattern.compile("^https?://[^/]+/");
    private Map<String, String> formattedUriCache = new HashMap<>();

    public void initializeFields(String restTemplateDetailEnabledPattern) {
        if (restTemplateDetailEnabledPattern != null)
            this.restTemplateDetailEnabledPattern = Pattern.compile(restTemplateDetailEnabledPattern);
        else
            this.restTemplateDetailEnabledPattern = null;
    }

    @Override
    public Iterable<Tag> getTags(String urlTemplate, HttpRequest request, ClientHttpResponse response) {
        if (isExceptionResponse(response)) {
            return getTagsWithNoFiltering(urlTemplate, request, response);
        }
        return getTagsWithFiltering(urlTemplate, request, response);
    }

    private Iterable<Tag> getTagsWithNoFiltering(String urlTemplate, HttpRequest request, ClientHttpResponse response) {
        return Arrays.asList(method(request),
                StringUtils.hasText(urlTemplate) ? uri(urlTemplate) : uri(request),
                status(response),
                clientName(request));
    }

    private Iterable<Tag> getTagsWithFiltering(String urlTemplate, HttpRequest request, ClientHttpResponse response) {
        Tag uri = getUriTag(urlTemplate, request);
        return Arrays.asList(method(request),
                uri,
                status(response),
                uri == URI_OTHERS ? CLIENT_OTHERS : clientName(request));
    }

    private boolean isExceptionResponse(ClientHttpResponse response) {
        return response == null;
    }

    private Tag getUriTag(String uriTemplate, HttpRequest request) {
        return (StringUtils.hasText(uriTemplate) ? getUriTagFromTemplate(uriTemplate) : getUriTagFromRequest(request));
    }

    private Tag getUriTagFromTemplate(String uriTemplate) {
        String formattedUri = formattedUriCache.get(uriTemplate);
        if (formattedUri == null) {
            formattedUri = ensureLeadingSlash(stripUri(uriTemplate));
            formattedUriCache.put(uriTemplate, formattedUri);
        }
        if (isMetricForPatternEnabled(formattedUri)) {
            return Tag.of("uri", formattedUri);
        }
        return URI_OTHERS;
    }

    private Tag getUriTagFromRequest(HttpRequest request) {
        String formattedUri = formattedUriCache.get(request.getURI().toString());
        if (formattedUri == null) {
            formattedUri = ensureLeadingSlash(stripUri(request.getURI().toString()));
            formattedUriCache.put(request.getURI().toString(), formattedUri);
        }
        if (isMetricForPatternEnabled(formattedUri)) {
            return Tag.of("uri", formattedUri);
        }
        return URI_OTHERS;
    }

    private boolean isMetricForPatternEnabled(String formattedUri) {
        return restTemplateDetailEnabledPattern != null
                && restTemplateDetailEnabledPattern.matcher(formattedUri).matches();
    }

    private String ensureLeadingSlash(String url) {
        return (url == null || url.startsWith("/")) ? url : "/" + url;
    }

    private String stripUri(String uri) {
        return STRIP_URI_PATTERN.matcher(uri).replaceAll("");
    }

}