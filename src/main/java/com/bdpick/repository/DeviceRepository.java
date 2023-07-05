package com.bdpick.repository;

import com.bdpick.domain.Device;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface DeviceRepository extends ReactiveCrudRepository<Device, Long> {

    @Query("SELECT SEQ_DEVICE.nextval FROM DUAL")
    Mono<Long> getSequence();

    Mono<Device> findDeviceByUserIdAndUuid(String userId, String uuid);
    Mono<Device> findDeviceByUserIdAndRefreshToken(String userId, String refrehToken);
}
