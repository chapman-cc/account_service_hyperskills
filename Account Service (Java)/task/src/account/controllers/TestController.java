package account.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TestController {

    @GetMapping("/hello-world")
    public Map<String, String> helloWorld() {
        return Map.of("message", "Hello World!");
    }
}
