package com.bdpick.service;

import com.bdpick.domain.UserType;
import com.bdpick.domain.dto.Token;
import com.bdpick.domain.entity.Device;
import com.bdpick.domain.entity.User;
import com.bdpick.domain.entity.Verify;
import com.bdpick.repository.DeviceRepository;
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
public class DeviceServiceTest {
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private DeviceRepository deviceRepository;

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
