package com.study.webflux.rate;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class RateService {

    /**
     * 통화쌍에 대한 현재 환율 정보를 조회한다.
     * <p>
     * WebFlux의 Mono를 사용하여 단일 환율 데이터를 비동기적으로 반환한다.
     *
     * @param symbol 통화쌍 코드 (예: USD-KRW)
     * @return 환율 정보
     */
    public Mono<RateResponse> getRate(String symbol) {
        return Mono.just(createDummyRate(symbol));
    }

    /**
     * 실시간 환율 스트림을 생성한다.
     * <p>
     * 1초마다 새로운 환율 데이터를 생성하여 Flux로 전달한다.
     * 실제 운영 환경에서는 외부 환율 API 또는 Kafka Consumer로부터
     * 수신한 데이터를 스트림으로 전달할 수 있다.
     *
     * @return 실시간 환율 스트림
     */
    public Flux<RateResponse> streamRates() {
        return Flux.interval(Duration.ofSeconds(1)).map(i -> createDummyRate("USD-KRW"));
    }

    /**
     * 테스트용 더미 환율 데이터를 생성한다.
     * <p>
     * 지정된 범위 내의 랜덤 환율을 생성하여 응답 객체를 반환한다.
     * 현재는 시뮬레이션 데이터이며 추후 실제 환율 API 연동으로 대체할 수 있다.
     *
     * @param symbol 통화쌍 코드
     * @return 생성된 환율 정보
     */
    private RateResponse createDummyRate(String symbol) {
        double randomPrice = ThreadLocalRandom.current().nextDouble(1370, 1400);

        return new RateResponse(
                symbol,
                BigDecimal.valueOf(randomPrice).setScale(2, java.math.RoundingMode.HALF_UP),
                LocalDateTime.now()
        );
    }

}
