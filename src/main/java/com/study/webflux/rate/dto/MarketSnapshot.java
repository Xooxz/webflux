package com.study.webflux.rate.dto;

import lombok.Builder;

/**
 * 주요 통화의 환율 정보를 포함하는 시장 스냅샷 DTO
 * <p>
 * Mono.zip()을 사용하여 병렬 조회한 환율 정보를
 * 하나의 응답 객체로 결합하기 위해 사용한다.
 *
 * @param usd USD-KRW 환율 정보
 * @param jpy JPY-KRW 환율 정보
 * @param eur EUR-KRW 환율 정보
 */
@Builder
public record MarketSnapshot(
        RateResponse usd,
        RateResponse jpy,
        RateResponse eur
) {
}
