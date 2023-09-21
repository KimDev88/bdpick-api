package com.bdpick.user.service.impl;

import com.bdpick.user.domain.Device;
import com.bdpick.user.repository.impl.DeviceRepositoryImpl;
import com.bdpick.user.service.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.reactive.stage.Stage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * device service class
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceServiceImpl implements DeviceService {
    private final Stage.SessionFactory factory;
    private final DeviceRepositoryImpl deviceRepository;

    /**
     * find device by user and uuid
     *
     * @param device device
     * @return device
     */
    public Mono<Device> findDeviceByUserAndUuid(Device device) {
        return factory.withTransaction(session
                        -> deviceRepository.findDeviceByUserAndUuid(device, session)
                )
                .thenApply(Mono::justOrEmpty)
                .exceptionally(Mono::error)
                .toCompletableFuture().join();
    }
}
