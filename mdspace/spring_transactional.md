# Spring - @Transactional & @Modifying(벌크 연산)

## 1. @Transactional을 사용하는 이유

### Transaction이란?
- 데이터베이스의 상태를 변화시키기 위해 수행하는 작업의 단위

### @Transactional 우선 순위
- (높음) 메소드 > 클래스 > 인터페이스 메소드 > 인터페이스 (낮음)
- 따라서, 공통적인 트랜잭션 규칙은 메소드에, 특별한 규칙은 메소드에 선언하면 좋음

### JPA Dirty Checking & @Transactional
- JPA는 Dirty Checking을 통해 엔티티 상태의 변경을 감지하며, 트랜잭션이 끝나는 시점에 변경된 엔티티들을 DB에 자동으로 반영해주어 별도의 update save 메소드가 필요 없음
- Dirty Checking은 트랜잭션이 커밋될 때, 즉 @Transactional 어노테이션이 끝나는 시점에 적용됨

### Spring AOP & @Transactional
- 클래스와 메소드에 @Transactional이 선언되면, 해당 클래스에 트랜잭션이 적용된 프록시 객체가 생성됨
- 프록시 객체는 @Transactional이 포함된 메소드가 호출된 경우 트랜잭션을 시작하며, CheckedException이나 예외가 없을 땐 Commit이 수행되고, UncheckedException이 발생하면 Rollback을 수행함

### @Transactional 롤백
- 연관관계에 의해 여러 엔티티들을 save 혹은 update를 해주는 경우, 트랜잭션이 성공적으로 완료되면 전체가 반영이 되거나, 실패하면 전체를 취소시켜 이전 상황으로 롤백을 해주어야 함
- @Transactional을 사용한 트랜잭션에서 예외가 발생하는 경우, 런타임 예외인 경우에는 자동적으로 롤백이 발생하지만, 아닌 경우에는 롤백이 되지 않기 때문에, 이러한 경우에는 rollbackFor 옵션을 사용해주어야 함
```java
@Transactional(rollbackFor=CustomException.class)
public void updateuser(UserDTO dto) throws CustomException {
	// 로직 구현
}
```

### @Transactional의 사용
- @Transactional을 Service 레이어에만 적용하면 Repository 레이어에서 굳이 추가할 필요가 없음
- 다만, 쿼리 단위로 트랜잭션 처리가 필요한 경우 Repository 레이어에서 @Transactional을 사용할 수도 있음

### @Transactional(readOnly=true) 사용
- Create, Update, Delete 작업이 동작하지 않아 조회한 데이터를 의도치 않게 변경되는 일을 예방하며, 스냅샷 저장이나 Dirty Checking을 하지 않아 성능이 향상됨
- DB가 Master-Slave 구성으로 되어있다면, 읽기 전용으로 Slave를 호출하여 상황에 따라 DB 서버의 부하를 줄이고 최적화를 할 수 있음
- 직관적으로 READ만 수행하는 메소드임을 알 수 있음
- 즉, 조회성 메소드에는 readOnly=true 옵션을 주는 것이 권장됨
- 지연 로딩(Lazy Loading)과 @Transactional(readOnly=true)
  - 지연 로딩 : 연관된 엔티티의 프록시만 만들어놓고, 해당 객체의 데이트럴 실제로 접근할 때 DB 조회를 하는 것
  - @Transactional이 없는 경우, 준영속 상태에 있는 엔티티들은 지연로딩을 할 수 없음
  - 따라서, 클래스 레벨에는 공통적으로 많이 사용하는 @Transactional(readOnly=true)를 사용하고, Create, Update, Delete가 있는 메소드에 별도로 @Transactional을 사용하는 것이 효율적임

## 2. @Transactional 사용 시 주의 사항

### 클래스, 메소드에 따른 주의 사항
- @Transactional은 public 메소드에만 적용이 가능
- 동일한 클래스 내 @Transactional이 선언되지 않은 메소드에서 @Transactional이 선언된 메소드를 호출하면 트랜잭션이 적용되지 않기 때문에, 같은 트랜잭션에 두고 싶다면 두 메소드 모두 @Transactional이 적용되어야 함
- @Transactional(propagation=Propagation.REQUIRES_NEW)을 사용하여 새로운 트랜잭션을 생성하려는 경우, 동일한 클래스 메소드끼리 호출하면 새로 생성되지 않기 때문에 반드시 다른 클래스 메소드를 호출해야함
- 예시
  ```java
  @Controller
  class PackingController {
    packingService.packing();
  }

  @Service
  class PackingService {  
    // 트랜잭션 선언되어있지 않음.
    public void packing() {
      ...
      callExternalApi();
      processCompleted();
    }

    @Transactional
    public void callExternalApi() {
      ...
    }

    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void processCompleted() {
      ...
    }
  }
  ```
  - PackingController에서 PackingService의 packing() 메소드를 호출했지만, packing() 메소드에 @Transactional이 선언되어 있지 않기 때문에, callExternalApi(), processCompleted() 두 메소드의 트랜잭션 역시 적용되지 않은 상태 -> 즉, 트랜잭션이 모두 적용되어있지 않아 예외가 발생해도 데이터가 롤백되지 않음
  - packing() 메소드에서 propagation=Propagation.REQUIRES_NEW 옵션이 있는 processCompleted() 메소드를 호출하여도, 새로운 트랜잭션이 시작되지 않고 기존 packing() 트랜잭션이 이어서 적용됨
    ```java
    class PackingService {
      @Transactional
      public void packing() {
        ...
        // 새로운 트랜잭션 시작
        packingApiService.callExternalApi();	
      }
    }

    class PackingApiService() { 
      @Transactional(propagation=Propagation.REQUIRES_NEW)
      public void callExternalApi() {
        ...
      }
    }
    ```

### 무한 로딩 관련 주의 사항
- 정상적으로 트랜잭션이 적용되어도, @Transactional(propagation=Propagation.REQUIRES_NEW) 사용 시 무한 로딩이 발생할 수 있음

```java
class PackingApiService {	
  @Transactional(propagation=Propagation.REQUIRES_NEW)
  public void callExternalApi(ApiDto apiDto) {
    Long packingId = apiDto.getPackingId();
    String name = apiDto.getName();
    saveInvoiceEntity(id, name);
    ..
  }

  private void saveInvoiceEntity(Long packingId, String name) {
    PackingEntity packingEntity = packingRepository.getOne(packingId);

    InvoiceEntity invoiceEntity = InvoiceEntity.builder()
                                                .packing(packingEntity)
                                                .name(name)
                                                .build();
    invoiceRepository.save(invoiceEntity);
  }
}
```
- callExternalApi() 메소드의 ApiDto가 이전 트랜잭션에서 가져온 DTO라고 가정할 때, saveInvoiceEntity() 메소드에서 DTO에 있는 값으로 엔티티를 조회하고 새로운 엔티티를 저장하는데, 이 때 API 응답 무한 로딩이 발생함
```java
class PackingApiService() {
  @Transactional(propagation=Propagation.REQUIRES_NEW)
  public void callExternalApi(Long packingId) {
    // 조회
    ApiDto apiDto = apiRepository.findApiDtoById(packingId).orElseThrow();
    Long packingId = apiDto.getPackingId();
    String name = apiDto.getName();
    saveInvoiceEntity(packingId, name);
    ...
  }

  private void saveInvoiceEntity(Long packingId, String name) {
    PackingEntity packingEntity = packingRepository.getOne(packingId);

    InvoiceEntity invoiceEntity = InvoiceEntity.builder()
                                                .packing(packingEntity)
                                                .name(name)
                                                .build();
    invoiceRepository.save(invoiceEntity);
  }
}
```
- 무한 로딩이 발생하는 경우, 새로운 트랜잭션 내에서 ApiDto를 다시 조회하고 다음 로직을 수행하면 정상적으로 동작함

### @Transactional(readOnly=true) 사용 시 주의 사항 - Optimistic Lock
- Optimistic Lock : 두 개의 트랜잭션이 동시에 동일한 데이터를 수정하려고 시도할 때, 대부분의 트랜잭션은 충돌이 발생하지 않는다고 낙관적으로 가정하는 방법 
- Optimistic Lock은 @Version 어노테이션을 추가해서 사용할 수 있음
- @Version 어노테이션을 사용하면, 트랜잭션이 엔티티를 수정할 때마다 버전 번호가 자동으로 업데이트되고 기록되는데, 다른 트랜잭션이 조회하는 시점의 버전과 수정하는 시점의 버전이 다르다는 것을 확인하면, 충돌이 발생한 것으로 간주하고 예외가 발생됨
- 만일 @Transactional(readOnly=true)로 설정한 메소드에 엔티티를 수정하는 로직이 있을 경우, 해당 트랜잭션이 엔티티를 수정하는 것이 아니라 읽기 전용으로 설정했기 때문에 버전 번호를 확인하지 못할 수 있으며, 이 때 충돌을 감지하지 못하고 동시에 발생한 트랜잭션의 변경 사항을 덮어쓰게되어 데이터 불일치 문제가 발생할 수 있음
- 따라서, Optimistic Lock이 활성화된 엔티티는 @Transactional(readOnly=true)로 설정된 메소드에서 엔티티 수정 작업을 하지 않도록 조심해야 함

### 외부 서버 연계 시 주의 사항
- 데이터 저장 로직과 동일한 데이터의 조회 로직이 동시에 존재하는 경우 경쟁 상태가 발생하여 데이터의 일관성이 깨지는 경우가 발생할 수 있음
  - 만일 바로 데이터를 저장하여 해결하고 싶다면, 데이터 저장 로직과 조회 로직의 Service 레이어를 분리, 트랜잭션을 분리해서 해결할 수 있음
- @Transactional이 적용된 메소드에서 외부 API 호출을 통해 modify를 하고, 그 modify된 데이터를 사용하여 데이터를 다시 modify하는 경우, 트랜잭션이 끝나는 시점에 DB에 반영이 되기 때문에 데이터가 꼬일 수 있으므로 이런 경우엔 @Transactional을 사용하지 않고 바로 수정되게 하는 것이 나을 수 있음
- 외부 서버와 연계하는 로직이 있다면, 같은 DB 테이블을 수정 조회 하는 경우 문제가 발생할 수 있는데, 이 때 트랜잭션 관리를 해주는 것이 중요함

## 5. @Modifying

### @Modifying?
- @Query 어노테이션을 통해 Insert, Update, Delete 작업을 수행할 때 사용
- 특히 Update가 일어나는 쿼리와 함께 사용해야 JPA 변경 감지와 관련된 처리를 생략하고 더 효율적인 실행이 가능함

### 벌크 연산
- 벌크 연산은 다량의 데이터의 Update, Delete 작업을 한 번에 처리하기 위한 작업으로, 한 엔티티에 대하여 JPA Dirty Checking을 하는 것이 아니라 여러 엔티티에 대하여 변경 쿼리를 수행하는 것을 뜻함
- @Modifying을 사용하면 한 번의 쿼리로 여러 데이터를 수정할 수 있기 때문에 효율적인 성능 개선을 할 수 있음

### @Modifying 옵션
- flushAutomatically : 기본 값은 false, true인 경우 해당 쿼리를 실행하기 전에 영속성 컨텍스트의 변경 사항을 DB에 flush함
- clearAutomatically : 기본 값은 false, true인 경우 해당 쿼리를 실행한 후 영속성 컨텍스트를 clear함

### @Modifying 주의할 점
- @Modifying으로 벌크 연산을 수행하면, 영속성 컨텍스트를 무시하고 쿼리를 실행하기 때문에 DB와 데이터 싱크가 맞지 않는 문제가 발생함
- 따라서, 벌크 연산으로 변경된 데이터를 사용하기 전에 clearAutomatically=true 옵션을 통해 영속성 컨텍스트를 초기화 해주는 작업이 필요함
  - 이런 경우 해당 데이터의 조회를 실행하면, 영속성 컨텍스트에 해당 엔티티가 존재하지 않기 때문에 DB를 조회하게되며, 데이터 싱크가 안맞는 문제를 해결할 수 있음
- Repository 레이어의 @Modifying 어노테이션은 기본적으로 Service 레이어의 @Transactional과 함께 사용되는데, 변경 작업은 트랜잭션 내에서 실행되어야 하고, 완료되지 않은 변경 작업이 의도하지 않은 영향을 줄 수 있기 때문

### 참고
- https://resilient-923.tistory.com/415
- https://brush-up.github.io/java/java-springboot-transactional
- https://velog.io/@roro/Spring-Transactional-%EC%82%AC%EC%9A%A9%EC%8B%9C-%EC%A3%BC%EC%9D%98%ED%95%A0%EC%A0%90
- https://velog.io/@jhbae0420/TransactionalreadOnly-true%EB%A5%BC-%EC%82%AC%EC%9A%A9%ED%95%98%EB%8A%94-%EC%9D%B4%EC%9C%A0%EC%99%80-%EC%A3%BC%EC%9D%98%ED%95%A0%EC%A0%90
- https://velog.io/@hsk2454/transaction-issue
- https://hstory0208.tistory.com/entry/JPA-Modifying%EC%9D%B4%EB%9E%80-%EA%B7%B8%EB%A6%AC%EA%B3%A0-%EC%A3%BC%EC%9D%98%ED%95%A0%EC%A0%90-%EB%B2%8C%ED%81%AC-%EC%97%B0%EC%82%B0