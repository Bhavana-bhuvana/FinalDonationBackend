package com.komal.template_backend.controller;

import com.komal.template_backend.model.HeroMessages;
import com.komal.template_backend.service.HeroMessagesService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
public class HeroMessagesController {

    private final HeroMessagesService service;

    public HeroMessagesController(HeroMessagesService service) {
        this.service = service;
    }

    @GetMapping
    public HeroMessages getMessages() {
        return service.getMessages();
    }

    @PutMapping
    public HeroMessages updateMessages(@RequestBody HeroMessages updated) {
        return service.updateMessages(updated);
    }
}