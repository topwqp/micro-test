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
        
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public String test(String json, Integer agencyId) {
        LOG.info("third repay notify params: json:[{}], agencyId:[{}]",json,agencyId);
        String responseJson = "test test test test tests test test ";
        LOG.info("test success ");
        return responseJson;
    }
    
}
