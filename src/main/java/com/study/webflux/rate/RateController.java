package com.study.webflux.rate;

import com.study.webflux.rate.dto.MarketSnapshot;
import com.study.webflux.rate.dto.RateResponse;
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
     * 여러 통화의 환율 정보를 동시에 조회한다.
     * <p>
     * WebFlux의 Mono.zip()을 활용하여
     * USD, JPY, EUR 환율 정보를 병렬로 조회한 후
     * 하나의 응답 객체로 반환한다.
     * <p>
     * 실제 운영 환경에서는 여러 외부 API를 동시에 호출하여
     * 응답 시간을 단축하는 용도로 활용할 수 있다.
     *
     * @return 시장 환율 스냅샷
     */
    @GetMapping("/snapshot")
    public Mono<MarketSnapshot> getMarketSnapshot() {
        return rateService.getMarketSnapshot();
    }

    /**
     * 외부 환율 API를 통해 USD-KRW 환율을 조회한다.
     * <p>
     * WebClient 기반 non-blocking 호출을 사용하며,
     * timeout, retry, fallback 처리를 포함한다.
     *
     * @return 외부 API 환율 정보
     */
    @GetMapping("/external")
    public Mono<RateResponse> getExternalRate() {
        return rateService.getExternalRate();
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
