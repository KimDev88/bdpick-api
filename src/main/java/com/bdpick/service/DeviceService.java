package com.bdpick.service;

import com.bdpick.domain.entity.Device;
import com.bdpick.repository.DeviceRepository;
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
public class DeviceService {
    private final Stage.SessionFactory factory;
    private final DeviceRepository deviceRepository;

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
