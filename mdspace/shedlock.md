# ShedLock

### 1. ShedLock의 필요성
- 서버가 증설되어 여러 인스턴스에서 동일한 어플리케이션이 구동되고 있다면, 스케줄러를 통해 정해진 시간에 동작하는 로직은 문제가 발생할 수 있음
- ShedLock은 이러한 중복 실행 문제에 대해서 각 인스턴스 간의 잠금 처리를 통해 하나의 인스턴스에서만 태스크를 실행할 수 있도록 함 (정확히는 태스크에 잠금을 지정하여, 동일한 이름의 태스크가 중복 실행 될 수 없도록 하는 것)
- ShedLock은 Redis나 MongoDB, JDBC와 같은 외부 저장소를 사용하여 태스크 간 잠금 처리를 함 (보통은 Redis를 통해 진행)
- ShedLock은 분산 스케줄러가 아니고, 단지 잠금 처리만 진행 (분산 스케줄러는 db-scheduler, JobRunr 등)
- 잠금은 시간 기반이며, ShedLock은 각 서버, 혹은 노드의 시간이 서로 동기화되어 있다고 가정함
### 2. ShedLock 구성
- Core - locking mechanism
- Integration - Spring AOP 또는 수동 코드를 사용하여 애플리케이션과 통합
- LockProvider - SQL DB, Mongo, Redis와 같은 외부 프로세스를 사용하여 잠금 제공
### 3. ShedLock 사용
- Config - @EnableSchedulerLock 어노테이션 적용
```java
@Configuration 
@EnableScheduling 
@EnableSchedulerLock(defaultLockAtMostFor = "10m") 
class MySpringConfiguration { ... }
```
- 스케줄 태스크에 @SchedulerLock 어노테이션 적용
```java
@Scheduled(...) 
@SchedulerLock(name = "scheduledTaskName") 
public void scheduledTask() { 
	// To assert that the lock is held (prevents misconfiguration errors)
	LockAssert.assertLocked(); // do something 
}
```
- @SchedulerLock 속성
	- name
		- 고유한 태스크 이름 사용
		- 동일한 이름의 태스크는 여러 인스턴스에 대해서 잠금처리로 인해 한 번만 수행됨
	- lockAtLeastFor
		- 잠금이 유지되어야 하는 최소 시간
		- 태스크 작업이 완료된 시간보다 지정된 시간이 길면, 지정된 시간 동안 잠금 처리
		- 태스크 작업 소요 시간이 지정된 시간보다 길면 lockAtMostFor에 지정된 시간까지 잠금이 유지되지만 lockAtMostFor duration 이전에 작업이 완료되면 잠금은 해제됨
	- lockAtMostFor
		- 실행 노드가 죽었을 때 잠금을 얼마나 오래 유지해야 하는지 지정하는 속성
		- 정상적인 상황에서는 태스크 작업이 완료되는 즉시 잠금이 해제되는데, lockAtLeastFor 속성이 지정되지 않은 경우 정상적인 실행 시간보다 훨씬 긴 값으로 lockAtMost 속성을 지정하는 해야 함
		- lockAtLeastFor > lockAtMostFor 인 경우 예외가 발생
### 4. LockProvider Redis
- LockProvider에 RedisConnectionFactory를 전달하기 위한 Bean 등록 (application.yml에 Redis 연결 설정이 되어 있다면, RedisConnectionFactory는 Bean으로 자동 등록됨)
```java
@Bean 
public LockProvider lockProvider(RedisConnectionFactory connectionFactory) { 
	return new RedisLockProvider(connectionFactory, ENV); 
}
```
- RedisLockProvider 클래스 생성자
	- Lock 데이터 저장 시 Key 형식 : {key-prefix}:{environment}:{SchedulerLock-name} 
```java
public RedisLockProvider(@NonNull RedisConnectionFactory redisConn) { 
	this(redisConn, "default"); 
} 

public RedisLockProvider(@NonNull RedisConnectionFactory redisConn, 
							@NonNull String environment) { 
	this(redisConn, environment, "job-lock"); 
} 

public RedisLockProvider(@NonNull RedisConnectionFactory redisConn, 
							@NonNull String environment, 
								@NonNull String keyPrefix) { 
	this(new StringRedisTemplate(redisConn), environment, keyPrefix); 
}
```
### 5. 기간 설정 형식
- duration + unit - ex) 1s, 5ms, 5m, 1d (4.0.0부터)
- duration in ms - ex) 100 (Spring integration 만)
- ISO-8601 - ex) PT15M
```text
Examples: 
	"PT20.345S" -- parses as "20.345 seconds" 
	"PT15M" -- parses as "15 minutes" (where a minute is 60 seconds) 
	"PT10H" -- parses as "10 hours" (where an hour is 3600 seconds) 
	"P2D" -- parses as "2 days" (where a day is 24 hours or 86400 seconds) 
	"P2DT3H4M" -- parses as "2 days, 3 hours and 4 minutes" 
	"P-6H3M" -- parses as "-6 hours and +3 minutes" 
	"-P6H3M" -- parses as "-6 hours and -3 minutes" 
	"-P-6H+3M" -- parses as "+6 hours and -3 minutes"
```