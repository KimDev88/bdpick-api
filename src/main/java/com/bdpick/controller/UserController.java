package com.bdpick.controller;

import com.bdpick.domain.User;
import com.bdpick.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.bdpick.common.BdConstants.PREFIX_API_URL;

@RestController
@RequestMapping(PREFIX_API_URL + "/users")
public class UserController

{
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public Flux<User> all(){
        return userRepository.findAll();
    }

    /**
     * find user by id
     *
     * @param id id
     * @return user
     */
    @GetMapping("{id}")
    public Mono<User> findById(@PathVariable("id") String id){
        return userRepository.findById(id);
    }

}
