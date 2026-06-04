package com.study.webflux.rate.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Frankfurter 환율 API 응답 DTO
 * <p>
 * 외부 환율 API에서 반환하는 응답 데이터를 매핑하기 위한 객체이다.
 *
 * @param base  기준 통화
 * @param rates 대상 통화별 환율 정보
 */
@Builder
public record FrankfurterResponse(
        String base,
        Map<String, BigDecimal> rates
) {
}
