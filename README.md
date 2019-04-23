# spring-micrometer-metric-grouping
Spring boot micrometer extension to group or categorize uris, add additional fields etc to server and client (restTemplate) metrics

## Objective:
When using spring boot with micrometer api, among many metrics, it also exposes metric of spring mvc requests received by the container
and external rest call made by the container using rest template via http_server_requests_seconds_sum /count /max 
and http_client_requests_seconds_sum /count /max. If using micrometer in an application with too many request URIs or with too many
permutation/combination of tags in this metric, the metric scraping system may show reduced performance due to high page size.
This api can be used to group such URIs under a common metric. This can also be used to add any additional tags, like custom error 
codes, result, request parameters, basically whatever you want to expose under the same metric.

## Features:
* Group metric tags at uri level
* Add additional tags to http_server_requests_seconds_* and http_client_requests_seconds_*
* Metric grouping will be ignored incase of exceptions

## Usage:
Refer project spring-micrometer-metric-grouping-sample for a sample implementation. The sample does not showcase additional 
tags feature but can be easily implemented using the interface AdditionalTagsProvider and creating a Bean of implementation for the container

## Use cases:
* __Case 1. At controller method level, without providing group name__
```
@CaptureDetailedMetric
@RequestMapping(value = "/helloGreen", method = RequestMethod.GET)
public String getHelloGreen()
```
calls to /helloRed will result in all metric being captured in detail at URI level, i.e. no grouping
```
http_server_requests_seconds_count{exception="None",method="GET",outcome="SUCCESS",status="200",uri="/helloGreen",} 4.0
http_server_requests_seconds_sum{exception="None",method="GET",outcome="SUCCESS",status="200",uri="/helloGreen",} 0.014431887
```

* __Case 2. At controller method level, with providing group name__
```
@CaptureDetailedMetric(uriGroupName = "redVarients")
@RequestMapping(value = "/helloRed4", method = RequestMethod.GET)
public String getHelloRed4(){
```
and
```
@CaptureDetailedMetric(uriGroupName = "redVarients")
@RequestMapping(value = "/helloRed3", method = RequestMethod.GET)
public String getHelloRed3(){
```
calls to /helloRed3 and /helloRed4 will result in both metric data being captured in detail at group level, i.e. with grouping

```
http_server_requests_seconds_count{exception="None",method="GET",outcome="SUCCESS",status="200",uri="redVarients",} 9.0
http_server_requests_seconds_sum{exception="None",method="GET",outcome="SUCCESS",status="200",uri="redVarients",} 0.184410881
```

* __Case 3. CaptureDetailedMetric not annotated OR no groupname is mentioned in annotation__
All metrics will be captured against uri = “others”. Time and count data will be captured and aggregated against that common tag, __IMPORTANT: Grouping will be overridden incase of exceptions and will be captured at individual uri level__
```
http_server_requests_seconds_count{exception="None",method="GET",outcome="SUCCESS",status="200",uri="others",} 46.0
http_server_requests_seconds_sum{exception="None",method="GET",outcome="SUCCESS",status="200",uri="others",} 0.183989058
```
###### Dev Notes:
This functionality can also be achieved using Micrometer MetricFilter but it would require a lot of code duplication and 
would have caused performance overhead as tags are created before filtering. Any additional tags newly introduced by micrometer can be added to the api by modifying MetricHttpServerTagsProvider and MetricHttpClientTagsProvider
Feel free to contribute!
