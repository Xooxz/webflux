package com.study.webflux.rate;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class RateService {

    private final ReactiveStringRedisTemplate redisTemplate;

    /**
     * 통화쌍에 대한 최신 환율 정보를 조회한다.
     * <p>
     * Redis에 저장된 최신 환율 정보를 조회하여 반환한다.
     * 실시간 환율 스트림에서 생성된 데이터는 Redis에 저장되며,
     * 본 메서드는 해당 데이터를 조회하는 역할을 수행한다.
     *
     * @param symbol 통화쌍 코드 (예: USD-KRW)
     * @return 최신 환율 정보
     */
    public Mono<RateResponse> getRate(String symbol) {
        String key = "rate:" + symbol;

        return redisTemplate.opsForValue()
                .get(key)
                .map(price -> new RateResponse(symbol, new BigDecimal(price), LocalDateTime.now()));
    }

    /**
     * 실시간 환율 스트림을 생성한다.
     * <p>
     * 1초마다 새로운 환율 데이터를 생성하여 Flux로 전달한다.
     * 생성된 환율 정보는 Redis에 최신값으로 저장한 후
     * SSE를 통해 클라이언트로 전송된다.
     * <p>
     * 실제 운영 환경에서는 외부 환율 API, Kafka Consumer,
     * QuickFIX/J 등의 실시간 데이터 소스로 대체될 수 있다.
     *
     * @return 실시간 환율 스트림
     */
    public Flux<RateResponse> streamRates() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(i -> createDummyRate("USD-KRW"))
                .flatMap(rate -> saveLatestRate(rate).thenReturn(rate));
    }

    /**
     * 최신 환율 정보를 Redis에 저장한다.
     * <p>
     * 단건 조회 API에서 최신 환율을 조회할 수 있도록
     * 실시간 스트림에서 생성된 환율 정보를 캐시에 저장한다.
     *
     * @param rate 저장할 환율 정보
     * @return 저장 성공 여부
     */
    private Mono<Boolean> saveLatestRate(RateResponse rate) {
        String key = "rate:" + rate.symbol();

        return redisTemplate.opsForValue().set(key, rate.price().toString(), Duration.ofMinutes(10));
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
