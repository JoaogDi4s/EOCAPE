package com.eocape.backend.Lead;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/lead")
public class LeadController {

    private final LeadService service;

    public LeadController(LeadService service) {
        this.service = service;
    }

    @GetMapping("/lista")
    public List<Lead> list() {
        return service.listAll();
    }

    @PostMapping
    public Lead create(@Valid @RequestBody Lead lead) {
        return service.create(lead);
    }
}