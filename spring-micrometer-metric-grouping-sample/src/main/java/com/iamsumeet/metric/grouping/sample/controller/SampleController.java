package com.iamsumeet.metric.grouping.sample.controller;

import com.iamsumeet.metric.grouping.api.CaptureDetailedMetric;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Example controller to showcase basic grouping of metric grouping api.
 * Example does not include features like implementing AdditionalTagsProvider
 * and Rest template metric
 *
 *  @author Sumeet Mondal
 */
@RestController
public class SampleController {

    //Metric of this call will be captured at actial uri level
    @CaptureDetailedMetric
    @RequestMapping(value = "/helloGreen", method = RequestMethod.GET)
    public String getHelloGreen(){
        return "Hello There! I am Green. My metric is being captured at detailed level";
    }

    //Metric of this call will be captured in "others"
    @RequestMapping(value = "/helloYellow", method = RequestMethod.GET)
    public String getHelloYellow(){
        return "Hello There! I am Yellow. My metric is being captured at grouped level";
    }

    //Metric of this call will be captured in "others"
    @RequestMapping(value = "/helloBlue", method = RequestMethod.GET)
    public String getHelloBlue(){
        return "Hello There! I am Blue. My metric is being captured at grouped level";
    }

    //Metric of this call will be captured in uri="redVarients"
    @CaptureDetailedMetric(uriGroupName = "redVarients")
    @RequestMapping(value = "/helloRed", method = RequestMethod.GET)
    public String getHelloRed(){
        return "Hello There! I am Red. My metric is being captured at grouped level: redVarients";
    }

    //Metric of this call will be captured in uri="redVarients"
    @CaptureDetailedMetric(uriGroupName = "redVarients")
    @RequestMapping(value = "/helloRed2", method = RequestMethod.GET)
    public String getHelloRed2(){
        return "Hello There! I am SalmonPink, a variant of red. My metric is being captured at grouped level: redVarients";
    }

    //Metric of this call will be captured in uri="redVarients"
    @CaptureDetailedMetric(uriGroupName = "redVarients")
    @RequestMapping(value = "/helloRed3", method = RequestMethod.GET)
    public String getHelloRed3(){
        return "Hello There! I am Light red, a variant of red. My metric is being captured at grouped level: redVarients";
    }

    //Metric of this call will be captured in uri="redVarients"
    @CaptureDetailedMetric(uriGroupName = "redVarients")
    @RequestMapping(value = "/helloRed4", method = RequestMethod.GET)
    public String getHelloRed4(){
        return "Hello There! I am Light Coral pink, a variant of red. My metric is being captured at grouped level: redVarients";
    }
    //Metric of this call will be captured in uri="redVarients"
    @CaptureDetailedMetric(uriGroupName = "redVarients")
    @RequestMapping(value = "/helloRed5", method = RequestMethod.GET)
    public String getHelloRed5() throws Exception{
        throw new Exception("Hello There! I am Light Coral pink, a variant of red. My metric is configured to be captured at grouped level: redVarients, but I am throwing exception" +
                "So, all my metric details are being captured!");
    }
}
