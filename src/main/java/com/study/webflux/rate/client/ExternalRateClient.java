package com.study.webflux.rate.client;

import com.study.webflux.rate.dto.FrankfurterResponse;
import com.study.webflux.rate.dto.RateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ExternalRateClient {

    private final WebClient webClient;

    /**
     * 외부 환율 API에서 환율 정보를 조회한다.
     * <p>
     * WebClient를 사용하여 non-blocking 방식으로 외부 API를 호출한다.
     * timeout, retry, fallback 처리를 적용하여 외부 API 장애에 대응한다.
     *
     * @param from 기준 통화 (예: USD)
     * @param to   대상 통화 (예: KRW)
     * @return 외부 API에서 조회한 환율 정보
     */
    public Mono<RateResponse> getRate(String from, String to) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/latest")
                        .queryParam("from", from)
                        .queryParam("to", to)
                        .build()
                )
                .retrieve()
                .bodyToMono(FrankfurterResponse.class)
                .timeout(Duration.ofSeconds(3))
                .retry(3)
                .map(response -> new RateResponse(
                        from + "-" + to,
                        response.rates().get(to),
                        LocalDateTime.now()
                ))

                .onErrorResume(e -> Mono.just(createFallbackRate(from + "-" + to)));
    }

    /**
     * 외부 환율 API 호출 실패 시 반환할 대체 응답을 생성한다.
     * <p>
     * timeout, 네트워크 장애, API 서버 오류 등으로 인해
     * 정상적인 환율 정보를 조회할 수 없는 경우 사용된다.
     * <p>
     * 서비스 중단을 방지하기 위한 Fallback 전략의 일환으로,
     * 환율 값은 0으로 설정하여 장애 상황임을 표현한다.
     *
     * @param symbol 통화쌍 코드 (예: USD-KRW)
     * @return 기본 환율 정보를 포함한 Fallback 응답
     */
    private RateResponse createFallbackRate(String symbol) {
        return new RateResponse(
                symbol,
                BigDecimal.ZERO,
                LocalDateTime.now()
        );
    }

}
