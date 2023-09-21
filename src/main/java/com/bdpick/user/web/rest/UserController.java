package com.bdpick.user.web.rest;

import com.bdpick.domain.request.CommonResponse;
import com.bdpick.user.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static com.bdpick.common.BdConstants.PREFIX_API_URL;

/**
 * user controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(PREFIX_API_URL + "/users")
public class UserController {
    private final UserServiceImpl userService;
//
//    public UserController(UserRepositoryImpl userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @GetMapping
//    public Flux<User> all() {
//        return userRepository.findAll();
//    }
//
    /**
     * find user by id
     *
     * @param id id
     * @return user
     */
    @GetMapping("{id}")
    public Mono<CommonResponse> findById(@PathVariable("id") String id) {
        return Mono.just(new CommonResponse().setData(userService.findById(id)));
    }

}