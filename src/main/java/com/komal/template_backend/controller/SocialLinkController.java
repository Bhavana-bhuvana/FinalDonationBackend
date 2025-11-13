package com.komal.template_backend.controller;

import com.komal.template_backend.model.SocialLink;
import com.komal.template_backend.repo.SocialLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/social-links")
    public class SocialLinkController {

    @Autowired
    private SocialLinkRepository repository;

    // ✅ Get all social links
    @GetMapping
    public List<SocialLink> getAllLinks() {
        return repository.findAll();
    }

    // ✅ Add new social link
    @PostMapping
    public SocialLink addLink(@RequestBody SocialLink link) {
        SocialLink saved = repository.save(link); // ensure it returns _id
        return saved;
    }

    // ✅ Update link
    @PutMapping("/{id}")
    public SocialLink updateLink(@PathVariable String id, @RequestBody SocialLink updatedLink) {
        Optional<SocialLink> existing = repository.findById(id);
        if (existing.isPresent()) {
            SocialLink link = existing.get();
            link.setPlatform(updatedLink.getPlatform());
            link.setUrl(updatedLink.getUrl());
            link.setIcon(updatedLink.getIcon());
            return repository.save(link);
        } else {
            throw new RuntimeException("Social link not found with id: " + id);
        }
    }

    // ✅ Delete link
    @DeleteMapping("/{id}")
    public void deleteLink(@PathVariable String id) {
        repository.deleteById(id);
    }
}
