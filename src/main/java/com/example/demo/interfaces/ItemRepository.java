package com.example.demo.interfaces;

import com.example.demo.data.user;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ItemRepository extends MongoRepository<user, String> {
}
