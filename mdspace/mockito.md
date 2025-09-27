# Mockito

## 1. Mockito란?
- 개발자가 동작을 직접 제어할 수 있는 mock 객체를 지원하는 테스트 프레임워크
- mock 객체는 테스트를 수행할 모듈과 연결되는 외부의 다른 서비스나 모듈들을 실제 사용하는 모듈을 사용하지 않고, 실제의 모듈을 흉내내는 가짜 모듈을 작성하여 테스트의 효용성을 높이는데 사용됨
	- 객체 지향 프로그래밍에서는 여러 객체들 간 의존성이 생기기 때문에 단위 테스트 작성 시 문제가 발생할 수 있기 때문에 mock 객체를 사용
	- 예를 들어 테스트 할 코드가 외부 서비스와의 API 연계나 DB 연결이 포함되어 있는 경우 mock 객체 사용
- Mockito는 이러한 mock을 쉽게 만들고 mock 행동을 정하는 stubbing, 정상적으로 동작하는지에 대한 verify 등의 기능을 제공함

## 2. Mockito 사용

### Mock 생성
- @Mock : @Mock으로 만든 mock 객체는 가짜 객체로, 그 안에 메소드를 호출해서 사용하려면 반드시 stubbing을 해야하며, stubbing을 거치지 않으면 null을 반환하여 테스트를 진행할 수 없음
- @Spy : @Spy로 만든 mock 객체는 진짜 객체로, 메소드 실행 시 stubbing을 하지 않으면 기존 객체의 로직을 실행한 값을, stubbing 한 경우에는 stubbing 값을 리턴함
- @InjectMocks : @Mock이나 @Spy로 생성된 mock 객체를 자동으로 주입

### Stubbing
- Stubbing이란, mock 객체의 메소드 실행 시 어떤 리턴 값을 리턴할지 미리 정하는 것
- 즉, Stubbing은 자신이 작성한 코드를 검사하기 위해 연관된 mock 객체 메소드의 결과를 조작하는 것
- Stubbing 관련 메소드
	- when : 스터빙 조건, 스터빙할 조건을 넣고 그 이후에 어떤 동작을 어떻게 제어할지 메소드 체이닝 형태로 작성
	- thenReturn : 스터빙한 메소드 호출 후 어떤 객체를 리턴할지 정의
	- thenThrow : 스터빙한 메소드 호출 후 어떤 Exception을 Throw할지 정의
- 예제 코드
  ```java
  @ExtendWith(MockitoExtension.class)
  class AccountServiceTest {
      @Mock
      private AccountRespository accountRespository;
      @Mock
      private AccountUserRepository accountUserRepository;
      @InjectMocks
      private AccountService accountService;

      @Test
      @DisplayName("계좌 생성 성공")
      void createAccountSuccess() {
          AccountUser accountUser = AccountUser.builder()
                                              .id(12L)
                                              .name("pobi").build();

          when(accountUserRepository.findById(anyLong()))
                  .thenReturn(Optional.of(accountUser));
      
      Account account = Account.builder()
                  .accountNumber("1000000013").build()
      
          when(accountRespository.findFirstByOrderByIdDesc())
                  .thenReturn(Optional.of(account));

          ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

          when(accountRespository.save(any()))
                  .thenReturn(Account.builder()
                      .accountUser(accountUser)
                                    .accountNumber("1000000013").build());
          // given
          // when
          AccountDto dto = accountService.createAccount(1L, 1000L);
          // then
          assertEquals(12L, dto.getUserId());
          assertEquals("1000000013", dto.getAccountNumber());
      }
  }
  ```
  1. accountUserRepostory.findById() 수행 시, id 12, name pobi인 accountUser가 반환됨
  2. accountRepostory.findFirstByOrderByDesc() 수행 시 acountNumber가 1000000013인 account가 반환됨
  3. stubbing을 통해 계좌를 저장하면, 1번과 2번에서 정의한 accountUser와 accountNumber를 멤버로 가지는 account 객체가 리턴
  4. JUnit의 assertEquals를 통해 stubbing 결과와 일치하는지 검증

### Verify
- stubbing한 메소드가 제대로 실행되는지 확인하는 기능
- verify 관련 옵션
	- times : 몇 번 호출됐는지 검증
	- never : 한 번도 호출되지 않았는지 검증
	- atLeastOne : 최소 한 번 호출됐는지 검증
	- atLeast : 최소 n번 호출됐는지 검증
	- atMostOnce : 최대 한 번 호출됐는지 검증
	- atMost : 최대 n번 호출됐는지 검증
	- calls : n번 호출됐는지 검증
	- timeout : n ms 시간 안에 호출됐는지 검증
	- after : n ms 시간 뒤에 호출됐는지 검증
	- description : 실패한 경우 표시될 문구

### API 연동 메소드 Mock
- service 예시
  ```java
  public class MyApiService {
      private final ExternalApiClient apiClient;

      public MyApiService(ExternalApiClient apiClient) {
          this.apiClient = apiClient;
      }

      public String getUserName(int userId) {
          // 실제 API 호출
          ApiResponse response = apiClient.requestUserInfo(userId);
          return response.getName();
      }
  }
  ```
- test code 예시
  ```java
  class MyApiServiceTest {

      @Test
      void getUserName_returnsCorrectName() {
          // 1. Mock 준비
          ExternalApiClient mockApiClient = mock(ExternalApiClient.class);

          // 2. mock의 행동 정의 (when - thenReturn)
          ApiResponse fakeResponse = new ApiResponse("abc123", "홍길동");
          when(mockApiClient.requestUserInfo(42)).thenReturn(fakeResponse);

          // 3. 테스트 대상 객체 생성(생성자에 mock 주입)
          MyApiService service = new MyApiService(mockApiClient);

          // 4. 메소드 수행 및 결과 확인
          String result = service.getUserName(42);

          assertEquals("홍길동", result);

          // 5. 외부 API가 실제로 해당 파라미터로 호출됐는지도 검증
          verify(mockApiClient, times(1)).requestUserInfo(42);
      }
  }
  ```
- 예외 test code 예시
  ```java
  @Test
  void getUserName_handlesApiErrorGracefully() {
      ExternalApiClient mockApiClient = mock(ExternalApiClient.class);
      // 예외 상황 stub
      when(mockApiClient.requestUserInfo(anyInt()))
      .thenThrow(new RuntimeException("API 에러"));

      MyApiService service = new MyApiService(mockApiClient);

      assertThrows(RuntimeException.class, () -> {
          service.getUserName(1);
      });
  }
  ```
---
### 참고
- https://velog.io/@choidongkuen/Mockito-%EB%A5%BC-%EB%BF%8C%EC%85%94-%EB%B4%85%EC%8B%9C%EB%8B%A4
- https://www.nextree.io/mockito/