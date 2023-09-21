package com.bdpick.service;

import com.bdpick.user.domain.enumeration.UserType;
import com.bdpick.domain.dto.Token;
import com.bdpick.user.domain.Device;
import com.bdpick.user.domain.User;
import com.bdpick.user.domain.Verify;
import com.bdpick.user.repository.impl.DeviceRepositoryImpl;
import com.bdpick.user.service.impl.DeviceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.util.Objects;

/**
 * device service test class
 */
@SpringBootTest
public class DeviceServiceImplTest {
    @Autowired
    private DeviceServiceImpl deviceService;
    @Autowired
    private DeviceRepositoryImpl deviceRepository;

    private User user;
    private Verify verify;
    private Token token;
    private Device device;

    @BeforeEach
    void setUp() {
        String email = "yong2407@hanmail.net";
        String userId = "su2407";

        user = new User();
        user.setId(userId);
        user.setType(UserType.N);
        user.setEmail(email);
        user.setPassword("gs225201");

        device.setUser(user);
        device.setUuid("TEST");
    }

    /**
     * findDeviceByUserAndUuid test
     */
    @Test
    public void findDeviceByUserAndUuidTest() {
        StepVerifier.create(deviceService.findDeviceByUserAndUuid(device))
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();
    }

}
