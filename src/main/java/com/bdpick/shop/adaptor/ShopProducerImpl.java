package com.bdpick.shop.adaptor;

import com.bdpick.shop.domain.ShopImage;
import com.bdpick.shop.domain.event.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * shop event producer implement class
 */
@RequiredArgsConstructor
@Service
public class ShopProducerImpl implements ShopProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC_REGISTER_NUMBER = "topic_register_number";
    private static final String TOPIC_SHOP_ADVERTISEMENT = "topic_shop_advertisement";
    private static final String TOPIC_SHOP = "topic_shop";
    private static final String TOPIC_SHOP_IMAGE = "topic_shop_image";

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 사업자 등록번호 검증
     *
     * @param registerNumber 사업자 등록번호
     */
    @Override
    public void verifyRegisterNumber(String registerNumber) throws JsonProcessingException {
        RegisterNumberVerified registerNumberVerified = new RegisterNumberVerified(registerNumber);
        String message = objectMapper.writeValueAsString(registerNumberVerified);
        kafkaTemplate.send(TOPIC_REGISTER_NUMBER, message);
    }

    @Override
    public void findShopAdvertisementStatics(long clickCount, long favoriteCount, long uploadCount) throws JsonProcessingException {
        ShopAdvertisementStaticsFounded shopAdvertisementStaticsFounded = new ShopAdvertisementStaticsFounded(clickCount, favoriteCount, uploadCount);
        String message = objectMapper.writeValueAsString(shopAdvertisementStaticsFounded);
        kafkaTemplate.send(TOPIC_SHOP_ADVERTISEMENT, message);

    }

    @Override
    public void createdShop(long shopId) throws JsonProcessingException {
        ShopCreated shopCreated = new ShopCreated(shopId);
        String message = objectMapper.writeValueAsString(shopCreated);
        kafkaTemplate.send(TOPIC_SHOP, message);

    }

    @Override
    public void createdShopImages(long shopId, List<ShopImage> imageList) throws JsonProcessingException {
        ShopImageCreated shopImageCreated = new ShopImageCreated(shopId, imageList);
        String message = objectMapper.writeValueAsString(shopImageCreated);
        kafkaTemplate.send(TOPIC_SHOP_IMAGE, message);

    }

    @Override
    public void changedShopImages(long shopId, List<ShopImage> imageList) throws JsonProcessingException {
        ShopImageChanged shopImageCreated = new ShopImageChanged(shopId, imageList);
        String message = objectMapper.writeValueAsString(shopImageCreated);
        kafkaTemplate.send(TOPIC_SHOP_IMAGE, message);

    }
}
