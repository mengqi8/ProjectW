package com.unicom.projectw.test;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/projectw")
public class SampleController {
    @RequestMapping("/hello")
    String home(){
        return "Hello World!";
    }
}
