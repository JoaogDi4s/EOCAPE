package com.eocape.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/api/ping")
    public String ping() {
        return "EOCAPE backend funcionando!";
    }
}