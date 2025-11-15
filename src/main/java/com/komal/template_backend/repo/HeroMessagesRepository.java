package com.komal.template_backend.repo;

import com.komal.template_backend.model.HeroMessages;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HeroMessagesRepository extends MongoRepository<HeroMessages, String> {}