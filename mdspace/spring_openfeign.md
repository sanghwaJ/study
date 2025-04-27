# Spring - OpenFeign

## 1. 서버 간 통신
- MSA 구조 상 서비스 간 REST API 통신이 필요한데, 이 때 동기 통신으로는 RestTemplate, WebClient, OpenFeign 등이 주로 사용됨

### Spring Cloud OpenFeign
- Spring Cloud 프로젝트에 포함된 동기 통신 클라이언트로, Spring Data JPA와 유사하게 선언적(Declarative) HTTP 클라이언트 도구
- OpenFeign의 장점
    - Interface와 Annotation 기반으로 코드 작성이 간결함(Spring MVC, Spring Data JPA와 비슷)
    - Interface 선언만 해두면 자동으로 클라이언트 구현체가 생성됨
    - 다른 Spring Cloud 기술들(Eureka, Circuit Breaker, Load Balancer)과 통합이 쉬움

### 서버 간 통신 Client 비교
항목 | RestTemplate | WebClient | Retrofit | OpenFeign
|---|---|---|---|---|
통신 방식 | 동기 | 비동기/동기 | 비동기/동기 | 동기
스타일 | 코드 기반 | 코드 기반 | 선언형 인터페이스 | 선언형 인터페이스
스프링 최적화 | 보통 | 매우 좋음 | 낮음 | 매우 좋음
비동기 지원 | X | O | O | X
추천 여부 | ❌ (구식) | ⭕ (최신) | ➖ (별로 추천 X) | ⭕ (특히 MSA 환경)

## 2. OpenFeign 사용

### build.gradle - 의존성 추가
```groovy
implementation 'org.springframework.cloud:spring-cloud-starter-openfeign'

dependencyManagement {
    imports {
        mavenBom "org.springframework.cloud:spring-cloud-dependencies:2025.0.1"
    }
}
```

### OpenFeign Configuration
```yaml
feign:
  client:
    config:
      default:
        connectTimeout: 2000
        readTimeout: 3000
        loggerLevel: FULL
```
```java
@Configuration
@EnableFeignClients("com.example.test.service")
public class OpenFeignConfig {
    @Bean
    public Retryer retryer() {
        return new Retryer.Default(100L, TimeUnit.SECONDS.toMillis(1L), 3);
    }

    @Bean
    public ErrorDecoder openFeignErrorDecoder() {
        return new OpenFeignErrorDecoder();
    }
}
```
- Main Class에 @EnableFeignClients를 적용하면 @WebMvcTest나 @DataJpaTest로 진행하는 테스트에도 동일하게 적용되어 테스트 시간이 더 길게 걸리는 단점이 있을 수 있음
- 따라서, Config 파일을 따로 만들어 @EnableFeignClients를 적용하고, 인터페이스를 생성할 위치를 지정해주는 것이 더 좋은 선택
```java
public class OpenFeignErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        FeignException exception = FeignException.errorStatus(methodKey, response);

        ins status = response.status(); // 400, 404, 500 ...
        String message = exception.getMessage();

        return new FeignException.FeignClientException(
            status,
            exception.getMessage(),
            response.request(),
            message.getBytes(),
            response.headers()
        );
    }
}
```
- FeignException은 서버의 응답이 있지만, 400이나 500에러 같은 exception이 발생하는 경우
- 그 외 서버 통신 자체가 실패한 경우
    - RetryableException : 타임 아웃, 서버 연결 실패 등
    - UnknownException : DNS를 찾지 못함
    - ConnectionException : 연결 자체가 불가함

### Interface
```java
@FeignClient(name = "test", url = "${test.url}", configuration = OpenFeignConfig.class)
public interface TestClient {

    @PostMapping("/send")
    TestResponse sendTest(@RequestBody TestRequest request, 
                          @RequestHeader("Authorization") String authorization);
}
```
- @FeignClient의 옵션
    - name 또는 value : 필수 속성, 클라이언트 이름 지정
    - url : 서비스 인스턴스 URL 지정
    - configuration : 커스텀 설정을 지정할 수 있는 클래스로, 디코더, 인코더, 에러 디코더 등을 커스터마이징 할 때 사용 <br/> ex) @FeignClient(name = "test", configuration = MyFeignConfiguration.class)
    - fallback : 장애 조치를 위한 대체(fallback) 클래스로, 원격 호출이 실패했을 때 기본 동작을 정의하는 클래스 <br/> ex) @FeignClient(name = "test", fallback = MyClientFallback.class)
    - fallbackFactory : 장애 조치를 위한 팩토리 클래스를 지정, FallbackFactory를 사용하면, 예외 발생 시 원인을 전달받아 사용할 수 있음 <br/> ex) @FeignClient(name = "test", fallbackFactory = MyClientFallbackFactory.class)
    - path : 기본 경로를 지정. 인터페이스의 모든 메소드에서 이 경로가 기본 경로로 추가된다. <br/> ex) @FeignClient(name = "test", path = "/api")
    - decode404 : 404 응답을 비즈니스 예외로 처리할지 여부를 설정한다. 기본값은 false이다. <br/> ex) @FeignClient(name = "test", decode404 = true)
    - primary : Bean이 여러 개 존재할 때 주입되는 기본 Bean으로 사용할지 여부를 지정한다. 기본값은 true. <br/> ex) @FeignClient(name = "test", primary = false)

## 3. Webflux & WebClient vs Virtual Thread & OpenFeign

### Webflux & WebClient vs Virtual Thread & OpenFeign 비교
항목 | WebFlux + WebClient | Virtual Thread + OpenFeign
|---|---|---|
통신 방식 | 완전 비동기 논블로킹 | 블로킹식 API + Virtual Thread
코드 스타일 | 함수형/리액티브 스타일 | 전통적 명령형 스타일
동시성 | 최상급 (I/O 처리 최적) | 매우 좋음 (수만 개 스레드)
복잡도 | 높음 (리액티브 적응 필요) | 낮음 (기존 코드 그대로)
스프링 통합 | 좋음 (WebFlux 기반) | 좋음 (Spring Boot + Feign)
운영 안정성(2025년) | 매우 검증됨 | 아직 약간 관찰 중
- 아주 높은 동시성 + 시스템 리소스를 극한으로 아껴야 한다면? WebFlux + WebClient
- 개발 생산성 + 코드의 직관성 + 기존 스프링 친화성을 원한다면? Virtual Thread + OpenFeign
- 따라서, 비교적 가벼운 HTTP 호출을 빠르게 뿌리는게 역할이라면 복잡하게 리액티브를 사용할 필요 없이 Virtual Thread & OpenFeign이 더 나은 선택일 수 있음

### Virtual Thread & OpenFeign 적용 예시
- Virtual Thread 설정
    ```java
    public class VirtualThreadConfig {
        @Bean
        public Executor virtualThreadExecutor() {
            return new ConcurrentTaskExecutor(Executors.newVirtualThreadPerTaskExecutor());
        }
    }
    ```
- OpenFeign 설정
    ```java
    @Configuration
    @EnableFeignClients("com.example.test.service")
    public class OpenFeignConfig {
        @Bean
        public Retryer retryer() {
            return new Retryer.Default(100L, TimeUnit.SECONDS.toMillis(1L), 3);
        }

        @Bean
        public ErrorDecoder openFeignErrorDecoder() {
            return new OpenFeignErrorDecoder();
        }
    }
    ```
- OpenFeign Client
    ```java
    @FeignClient(name = "test", url = "${test.url}", configuration = OpenFeignConfig.class)
    public interface TestClient {

        @PostMapping("/send")
        TestResponse sendTest(@RequestBody TestRequest request, 
                            @RequestHeader("Authorization") String authorization);
    }
    ```
- Sender
    ```java
    @Slf4j
    @Component
    @RequiredArgsConstructor
    public class TestSender {

        private final TestClient testClient;

        @Async
        public void send(TestRequet request) {
            try {
                TestResponse response = webhookClient.sendNotification(request, "Bearer token_value");

                if (response != null && response.isSuccess()) {
                    log.info("성공: {}", response.getMessage());
                } else {
                    log.warn("실패: {}", response);
                }
            } catch (Exception e) {
                log.error("호출 실패 - 최종 fallback 처리", e);
                // fallback 처리: 예를 들면 DB에 실패 기록 저장하거나, 알림 보내기
            }
        }
    }
    ```

### 참고
- https://khdscor.tistory.com/128
- https://velog.io/@joeun-01/Spring-Boot-MSA-%ED%99%98%EA%B2%BD%EC%97%90%EC%84%9C-OpenFeign-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0
