package api.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gateway/")
public class Endpoints {

    @GetMapping("/hello")
    public String hello(){
        return "Hello World";
    }
}
