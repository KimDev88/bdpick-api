package com.bdpick.shop.adaptor;

import com.bdpick.shop.domain.ShopImage;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

/**
 * shop event producer
 */
public interface ShopProducer {
    /**
     * 사업자 등록번호 검증
     *
     * @param registerNumber 사업자 등록번호
     */
    void verifyRegisterNumber(String registerNumber) throws JsonProcessingException;

    /**
     * 가게 광고 통계 조회
     *
     * @param clickCount    클릭 횟수
     * @param favoriteCount 즐겨찾기 횟수
     * @param uploadCount   업로드 횟수
     */
    void findShopAdvertisementStatics(long clickCount, long favoriteCount, long uploadCount) throws JsonProcessingException;

    /**
     * 가게 광고 생성
     *
     * @param shopId 가게 아이디
     */
    void createdShop(long shopId) throws JsonProcessingException;

    /**
     * 가게 광고 생성
     *
     * @param imageList 이미지 리스트
     */
    void createdShopImages(long shopId, List<ShopImage> imageList) throws JsonProcessingException;

    /**
     * 가게 광고 수정
     *
     * @param imageList 이미지 리스트
     */
    void changedShopImages(long shopId, List<ShopImage> imageList) throws JsonProcessingException;
}
