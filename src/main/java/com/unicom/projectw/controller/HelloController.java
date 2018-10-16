package com.unicom.projectw.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/projectw")
public class HelloController {
    @RequestMapping("/hello")
    String home(){
        return "Hello World!";
    }
}
