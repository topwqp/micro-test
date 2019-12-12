package com.topwqp.micro.gor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangqiupeng
 * @date 2019年12月10日19:20:57
 * @desc  请求测试
 */
@RestController
@RequestMapping("/v1/goRepay")
public class RequestController {

    private static final Logger LOG = LoggerFactory.getLogger(RequestController.class);
    private static String INJECT_TO_REQUEST_ENTITY_REQUEST_ID = "RequestId";

    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public String test(String json, Integer agencyId) {
        LOG.info("third repay notify params: json:[{}], agencyId:[{}]",json,agencyId);
        String responseJson = "test test test test tests test test ";
        LOG.info("test success ");
        return responseJson;
    }

    @RequestMapping(value = "/www", method = RequestMethod.POST)
    public String test222(String json, Integer agencyId) {
        String secondLine = "POST /v1/goRepay/test?aa=bb&c=9 HTTP/1.1";
        String [] secondLineItem = secondLine.split(" ");
        String requestUrl = secondLineItem[1];
        LOG.info("url is {} ",requestUrl);
        int index = requestUrl.indexOf("?");
        String rewriteUrl = null;
        if (index > 0){
            rewriteUrl = requestUrl + "&" + INJECT_TO_REQUEST_ENTITY_REQUEST_ID + "=111"  ;
        }else {
            rewriteUrl = requestUrl + "?" + INJECT_TO_REQUEST_ENTITY_REQUEST_ID + "=111" ;
        }
        secondLineItem[1] = rewriteUrl;
        StringBuilder result = new StringBuilder(20);
        for (String item : secondLineItem){
            result.append(item).append(" ");
        }
        LOG.info("result is  {} ", result.toString().substring(0,result.toString().length() -1));
        return "test";

    }
}
