package com.example.demo.repo;

import com.example.demo.model.MyUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<MyUser, Integer> {
    MyUser findByUsername(String username);
}
