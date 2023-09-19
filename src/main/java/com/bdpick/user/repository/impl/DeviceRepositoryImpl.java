package com.bdpick.user.repository.impl;

import com.bdpick.user.domain.Device;
import com.bdpick.user.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.reactive.stage.Stage;
import org.springframework.stereotype.Repository;

import java.util.concurrent.CompletionStage;

/**
 * device repository class
 */
@Repository
@RequiredArgsConstructor
public class DeviceRepositoryImpl implements DeviceRepository {
    /**
     * find device by user and uuid
     *
     * @param device  device
     * @param session session
     * @return device
     */
    public CompletionStage<Device> findDeviceByUserAndUuid(Device device, Stage.Session session) {
        return session.createQuery("select d from Device d where d.user = :user and d.uuid = :uuid", Device.class)
                .setParameter("user", device.getUser())
                .setParameter("uuid", device.getUuid())
                .getSingleResultOrNull();
    }

    /**
     * find device by user and uuid
     *
     * @param device  device
     * @param session session
     * @return device
     */
    public CompletionStage<Device> findDeviceByUserIdAndRefreshToken(Device device, Stage.Session session) {
        return session.createQuery("select d from Device d where d.user = :user and d.refreshToken = :refreshToken", Device.class)
                .setParameter("user", device.getUser())
                .setParameter("refreshToken", device.getRefreshToken())
                .getSingleResultOrNull();
    }

    /**
     * save device
     *
     * @param device  device
     * @param session session
     * @return saved device
     */
    public CompletionStage<Device> save(Device device, Stage.Session session) {
        return session.persist(device)
                .thenApply(unused -> device);
    }

}
