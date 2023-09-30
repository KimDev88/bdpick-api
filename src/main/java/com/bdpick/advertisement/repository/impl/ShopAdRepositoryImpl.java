package com.bdpick.advertisement.repository.impl;

import com.bdpick.advertisement.domain.AdKeyword;
import com.bdpick.advertisement.domain.ShopAd;
import com.bdpick.advertisement.repository.ShopAdRepository;
import com.bdpick.common.web.rest.dto.Pageable;
import com.bdpick.advertisement.domain.Keyword;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.reactive.stage.Stage;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

/**
 * Shop Advertisement Repository
 */
@Repository
@RequiredArgsConstructor
public class ShopAdRepositoryImpl implements ShopAdRepository {

    /**
     * create shop ad
     *
     * @param shopAd entity
     * @return saved entity
     */
    public CompletionStage<ShopAd> save(ShopAd shopAd, Stage.Session session) {
        // 쿼리를 위해 키워드 리스트 중 키워트 문자열만 추출
        List<String> keywordStrList = shopAd.getKeywordList()
                .stream()
                .map(adKeyword -> adKeyword.getKeyword().getKeyword()).toList();
        List<Keyword> sourceKeywordList = new ArrayList<>(shopAd.getKeywordList()
                .stream()
                .map(AdKeyword::getKeyword)
                .toList());

        return session.createQuery("select k from Keyword k where k.keyword in :keywordList", Keyword.class)
                .setParameter("keywordList", keywordStrList)
                .getResultList()
                .thenApply(keywords -> {
                    System.out.println("keywords = " + keywords);
                    // 중복제거를 위한 keyword map
                    Map<String, Keyword> resKeywordMap = new HashMap<>();
                    // keyword 병합을 위한 리스트
                    List<AdKeyword> resAdKeywordList = new ArrayList<>();
                    // 소스 리스트 -> 맵
                    sourceKeywordList
                            .forEach(keyword -> {
                                resKeywordMap.put(keyword.getKeyword(), keyword);
                            });
                    // 결과 리스트 -> 맵으로 병합
                    keywords
                            .forEach(keyword -> {
                                resKeywordMap.put(keyword.getKeyword(), keyword);
                            });
                    // 결과 keyword -> Adkeyword로 변환
                    resKeywordMap.forEach((s, keyword) -> {
                        AdKeyword adKeyword = new AdKeyword();
                        adKeyword.setShopAd(shopAd);
                        adKeyword.setKeyword(keyword);
                        resAdKeywordList.add(adKeyword);
                    });
                    shopAd.setKeywordList(resAdKeywordList);
                    session.persist(shopAd);
                    return shopAd;
                })
                .exceptionally(throwable -> {
                    System.out.println("throwable = " + throwable);
                    throw new RuntimeException(throwable);
                });
    }

    /**
     * find last created shopAd
     *
     * @param session entity session
     * @return last created entity
     */
    public CompletionStage<ShopAd> findLastShopAd(Stage.Session session) {
        return session.createQuery("select sa from ShopAd sa order by id desc limit 1", ShopAd.class)
//                .setFirstResult(0)
                .getSingleResultOrNull();

    }

    /**
     * find shop ad with pageable
     *
     * @param pageable pageable dto
     * @param session  session
     * @return shop ad list
     */
    public CompletionStage<List<ShopAd>> findShopAds(Pageable pageable, @NonNull Stage.Session session) {
        String sql = """
                select ad from ShopAd ad
                        left join fetch ad.keywordList
                        left join fetch ad.adImageList
                        order by ad.id asc
                """;
        return session.createQuery(sql, ShopAd.class)
//        return session.createQuery("select ad from Shop s inner join fetch from ShopAd ad join  on ad.shop = s order by ad.id asc", ShopAd.class)
                .setFirstResult(pageable.getOffset())
                .setMaxResults(pageable.getSize())
                .getResultList();
    }
}
