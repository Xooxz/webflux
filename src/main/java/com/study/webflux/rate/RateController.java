package com.study.webflux.rate;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rates")
public class RateController {

    private final RateService rateService;

    /**
     * 단건 환율 조회
     * <p>
     * WebFlux에서는 단일 결과를 반환할 때 Mono를 사용한다.
     * Mono는 0개 또는 1개의 데이터를 비동기적으로 전달한다.
     * <p>
     * 예)
     * GET /api/rates/USD-KRW
     */
    @GetMapping("/{symbol}")
    public Mono<RateResponse> getRate(@PathVariable String symbol) {
        return rateService.getRate(symbol);
    }

    /**
     * 실시간 환율 스트림 조회
     * <p>
     * Flux는 0개 이상의 데이터를 비동기 스트림으로 전달한다.
     * produces = TEXT_EVENT_STREAM_VALUE 설정을 통해
     * SSE(Server-Sent Events) 형태로 데이터를 지속적으로 전송한다.
     * <p>
     * 클라이언트는 연결을 유지한 채 실시간 환율 변동을 수신할 수 있다.
     * <p>
     * 예)
     * GET /api/rates/stream
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<RateResponse> streamRates() {
        return rateService.streamRates();
    }

}
