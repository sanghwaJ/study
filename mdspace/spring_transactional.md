# Spring - @Transactional

## 1. @Transactional 사용 시 주의 사항

- @Transactional은 public 메소드에만 적용이 가능함
- 동일한 클래스 내 @Transactional이 선언되지 않은 메소드에서 @Transactional이 선언된 메소드를 호출하면 트랜잭션이 적용되지 않기 때문에, 같은 트랜잭션에 두고 싶다면 두 메소드 모두 @Transactional이 적용되어야 함

```java
@Controller
class PackingController {
  packingService.packing();
}

@Service
class PackingService {
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
```

- PackingController에서 PackingService의 packing() 메소드를 호출했지만, packing() 메소드에 @Transactional이 선언되어 있지 않기 때문에, callExternalApi(), processCompleted() 두 메소드의 트랜잭션 역시 적용되지 않은 상태
- 즉, 트랜잭션이 모두 적용되어있지 않아 예외가 발생해도 데이터가 롤백되지 않음
- @Transactional(propagation=Propagation.REQUIRES_NEW)을 사용하여 새로운 트랜잭션을 생성하려는 경우, 동일한 클래스 메소드끼리 호출하면 새로 생성되지 않음, 반드시 다른 클래스 메소드를 호출해야함
- @Transactional이 적용된 메소드에서 외부 API 호출을 통해 modify를 하고, 그 modify된 데이터를 사용하여 데이터를 다시 modify하는 경우, 트랜잭션이 끝나는 시점에 DB에 반영이 되기 때문에 데이터가 꼬일 수 있으므로 이런 경우엔 @Transactional을 사용하지 않는게 나음
- 외부 서버와 연계하는 로직이 있다면, 같은 DB 테이블을 수정 조회 하는 경우 문제가 발생할 수 있는데, 이 때 트랜잭션 관리를 해주는 것이 중요함

