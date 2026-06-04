package com.study.webflux.rate.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 환율 조회 응답 DTO
 *
 * @param symbol    통화쌍 코드 (예: USD-KRW)
 * @param price     현재 환율
 * @param createdAt 환율 생성 시각
 */
@Builder
public record RateResponse(
        String symbol,
        BigDecimal price,
        LocalDateTime createdAt
) {
}
