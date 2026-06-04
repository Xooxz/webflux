# Spring WebFlux Playground

## 소개

Spring WebFlux 학습 및 참고용 예제 프로젝트입니다.

실무에서 자주 사용하는 Reactive Programming 패턴을 예제 형태로 정리합니다.

### 주요 목표

- Mono 사용법
- Flux 사용법
- Reactive Redis 연동
- WebClient 비동기 호출
- SSE(Server-Sent Events)
- Timeout / Retry / Fallback
- Mono.zip 병렬 처리

---

## 기술 스택

### Backend

- Java 21
- Spring Boot
- Spring WebFlux
- Project Reactor

### Cache

- Redis
- Reactive Redis

### External API

- WebClient

### Infra

- Docker Compose

---

## 구현 예제

### 1. Mono

단건 데이터 비동기 처리

```java
Mono<RateResponse>
```

### 2. Flux

스트림 데이터 처리

```java
Flux<RateResponse>
```

### 3. SSE

실시간 데이터 Push

```java
@GetMapping(
    value = "/stream",
    produces = MediaType.TEXT_EVENT_STREAM_VALUE
)
```

### 4. Reactive Redis

최신 환율 정보 저장 및 조회

```java
ReactiveStringRedisTemplate
```

### 5. WebClient

외부 API 비동기 호출

```java
webClient.get()
         .retrieve()
         .bodyToMono(...)
```

### 6. Timeout / Retry / Fallback

외부 API 장애 대응

```java
.timeout(Duration.ofSeconds(3))
.retry(3)
.onErrorResume(...)
```

### 7. Mono.zip

여러 비동기 작업 병렬 처리

```java
Mono.zip(usd, jpy, eur)
```

---

## API

### 최신 환율 조회

```http
GET /api/rates/{symbol}
```

예시

```http
GET /api/rates/USD-KRW
```

---

### 실시간 환율 스트림

```http
GET /api/rates/stream
```

SSE 기반으로 실시간 환율 정보를 전달합니다.

---

### 환율 스냅샷 조회

```http
GET /api/rates/snapshot
```

Mono.zip을 사용하여 여러 통화의 환율 정보를 병렬 조회합니다.

---

### 외부 환율 API 조회

```http
GET /api/rates/external
```

WebClient를 사용하여 외부 환율 API를 비동기 호출합니다.

---

## 프로젝트 구조

```text
src/main/java
└─ com.study.webflux
   ├─ config
   │   └─ WebClientConfig
   │
   └─ rate
      ├─ client
      │   └─ ExternalRateClient
      │
      ├─ RateController
      ├─ RateService
      │
      ├─ RateResponse
      ├─ FrankfurterResponse
      └─ MarketSnapshot
```

---

## 학습 포인트

### Mono

0~1개의 데이터를 처리한다.

### Flux

0~N개의 데이터를 처리한다.

### flatMap

비동기 작업을 연결한다.

### thenReturn

기존 결과를 무시하고 다른 객체를 반환한다.

### switchIfEmpty

데이터가 없을 경우 대체 로직을 수행한다.

### Mono.zip

여러 비동기 작업을 병렬 실행한 후 결과를 결합한다.

### WebClient

Non-Blocking 방식으로 외부 API를 호출한다.

### Reactive Redis

Reactive Stream 기반으로 Redis를 조회하고 저장한다.

---

## 실행 방법

### Redis 실행

```bash
docker compose up -d
```

### 애플리케이션 실행

```bash
./gradlew bootRun
```

### API 테스트

```bash
curl http://localhost:8080/api/rates/USD-KRW
```

```bash
curl http://localhost:8080/api/rates/stream
```

```bash
curl http://localhost:8080/api/rates/snapshot
```

```bash
curl http://localhost:8080/api/rates/external
```