# Java - Record & MapStruct

## 1. Record
- Java14에서 프리뷰로 처음 소개되었으며, Java16 부터 정식 기능으로 추가됨

### Record 특징 1. 불변 객체
- 상태 변경이 불가능하여 멀티 쓰레드 환경에서의 안정성을 높일 수 있음
- 코드의 가독성과 유지보수성을 향상 시킴

### Record 특징 2. 간결함
- construct, getter, equals(), toString(), hashCode() 등의 메소드가 자동 생성되어 반복해서 작성하지 않아도 됨
- 즉, 비즈니스 로직이나 핵심 기능을 나타내지 않는 Boilerplate 코드 작성을 줄여줌
- Record는 객체의 의도를 명확히 드러내어 불필요한 코드 작성을 줄여 가독성을 높여줌

### Record 사용법
```java
// 선언
public record Person (String name, String address) { }

// 객체 인스턴스화
Person person = new Person("John Doe", "100 Linda Ln.");

// Getter
String name = "Gom";
String address = "55 GomeHouse";

Person person1 = new Person(name, address);
Person person2 = new Person(name, address);

assertEquals(name, person1.name()); // true
assertEquals(address, person1.address()); // true

// equals
assertTrue(person1.equals(person2)); // true

// hashCode
assertEquals(person1.hashCode(), person2.hashCode()); // true

// toString
log.info(person.toString()) // Person[name=Gom, address=50 GomeHouse.]
```

### Record 사용시 주의할 점
- Record는 암묵적으로 final로 선언되며, 상속이 불가능함 (abstract, sealed, non-sealed와 같은 수식어 사용 불가)
- 상속은 불가능하지만, 인터페이스 구현은 가능함
- RecordHeader에 선언된 필드는 final로 정의되어 있기 때문에 Record의 생성자에서만 초기화 할 수 있음
- Record는 기본적으로 불변성을 유지해야 하는 데이터 객체로, 비즈니스 로직을 Record 내부에 포함시키는 것은 권장되지 않음
- 

## 2. Record Constructor (레코드 생성자)

### Normal Canonical Constructors (정식 생성자)
```java
record Point(int x, int y) {
    // 정식 생성자
    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

Point p = new Point(10, 20);
```
- Record의 모든 필드를 초기화하는 생성자로, Record 객체를 생성할 때 사용
- 데이터 클래스로 불변성을 유지하기 위해 모든 필드를 초기화하는 생성자를 제공함

### Compact Canonical Constructors (컴팩트 생성자)
```java
record Point(int x, int y) {
    // 컴팩트 생성자, 필드를 나열하지 않음
    Point {
        validate(x, y);
    }

    static void validate(int x, int y) {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("x와 y는 0보다 작을 수 없습니다.");
        }
    }
}

Point p = new Point(-1, 1); // 검증 메서드로 예외가 발생함
```
- Record 필드를 별도로 나열하지 않고 필드가 암묵적으로 선언됨
- 컴팩트 생성자는 주로 매개변수를 검증하거나 값을 정규화하는 로직을 포함

### 생성자 사용 시 주의점
- 정식 생성자, 컴팩트 생성자 두 방식 중 하나만 사용이 가능하며, 둘 다 명시적으로 정의할 경우 컴파일 오류가 발생
- 생성자의 접근 제어자는 Record 클래스의 접근 제어자와 일치해야 하며, 생성자를 명시하지 않으면 컴파일러가 자동으로 정식 생성자를 추가함
- Record는 Entity에는 사용하지 말고, DTO로 사용해야함 (Entity는 불변하지 않기 때문)

## 3. Record & Builder
### Builder 사용
```java
@Getter
@Setter
@NoArgsConstructor
public class QuestionSaveRequestDto {
  @NotBlank(message = "제목은 필수 입력 사항입니다.")
  @Size(max = 200, message = "제목이 너무 길어요.")
  private String title;
  @NotBlank(message = "내용은 필수 입력 사항입니다.")
  private String content;

  @Builder
  public QuestionDto(String title, String content) {
    this.title = title;
    this.content = content;
  }

  public Question toEntity(Member member) {
      return Question.builder()
                .title(title)
                .content(content)
                .member(member)
                .build();
  }
}
```
### Record 사용
```java
// 예시 1
public record QuestionDto(
  @NotBlank(message = "제목은 필수 입력 사항입니다.")
  @Size(max = 200, message = "제목이 너무 길어요.")
  String title,
  @NotBlank(message = "내용은 필수 입력 사항입니다.") 
  String content
) {
  public Question toEntity(String title, String content) {
      return new Question(title, content);
  }
}

// 예시 1 사용
QuestionDto dto = QuestionDto.of("Alice", "WonderLand");

// 예시 2
public record QuestionDtoBuilder(
  private String title,
  private String content
) {
  public QuestionDtoBuilder title(String title) {
    this.title = title;
    return title;
  }
  
  public QuestionDtoBuilder content(String content) {
    this.content = content;
    return content;
  }

  public Question build() {
      return new Question(title, content);
  }
}

// 예시 2 사용
QuestionDto dto = new QuestionDtoBuilder().title("Alice").content("WonderLand").build();
```
- 위의 예시 1과 같이 Record에서는 new 생성자 키워드가 Builder 대신 사용되며, Builder 형식 처럼 사용하고 싶으면 예시 2와 같이 builder 클래스를 만들어 사용하면 됨

### 4. MapStruct

### MapStruct 특징
- 객체의 타입 변환 시 유용하게 사용할 수 있는 라이브러리
- Annotation 기반으로 작성되며 Bean을 등록할 수 있어 DI를 활용하여 사용할 수 있음
- 반복적인 DTO 변환 작업을 대체
- 반환 데이터가 달라지게 될 경우 편리하게 매핑할 수 있도록 도와줌
- 리플렉션이 아닌 직접 메소드를 호출하는 방식으로 동작하여 속도가 빠름
- 컴파일 시점에 매핑 정보가 Type-Safe 한지 검증이 가능함
- 빌드 시점에 매핑이 올바르지 않다면 에러 정보를 Log에 띄워줌
- MapStruct를 사용하면 Builder 패턴인 of, from 메소드를 사용하지 않아도 됨

### MapStruct 기본 사용
```java
public class RequestDto {
  private String title;
  private String content;
  private String sender;
  private List<String> receiver;
  private LocalDateTime requestTime;
  private String type;
}

public class MessageBodyDto {
  private String title;
  private String content;
  private String sender;
  private List<String> receiver;
  private LocalDateTime requestTime;
  private String type;
}

@Mapper
public interface MessageMapper {
	MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);

  // RequestDto -> MessageBodyDto 매핑
	MessageBodyDto toMessageBodyDto(RequestDto requestDto);
}
```

### 여러 객체를 하나의 객체에 매핑
```java
public class PageDto {
	private Integer pageIndex;
	private Integer pageCount
}

public class MessageServiceDto {
  private String title;
	private String content;
	private String sender;
	private List<String> receiver;
	private String type;
	private Integer pageIdx;
	private Integer pageCnt;
}

public interface MessageMapper {
	MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);

  //PageDto, RequestDto -> MessageServiceDto 매핑
	@Mapping(source="pageDto.pageIndex", target="pageIdx")
  @Mapping(source="pageDto.pageCount", target="pageCnt")
	MessageServiceDto toMessageServiceDto(PageDto pageDto, RequestDto requestDto);
}
```
- 만약 매핑 대상을 지정해야 하는 경우, @Mapping을 이용하여 source에는 매핑 값을 가지고 올 대상, target에는 매핑할 대상을 작성해주면 됨

### 매핑에 여러 파라미터가 필요한 경우
```java
public class MessageListServiceDto {
	private String messageId;
	private Integer count;
	private String title;
	private String content;
	private String sender;
	private List<String> receiver;
	private LocalDateTime requestTime;
}

public interface MessageMapper {
	MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);

  //messageId, count, requestDto -> MessageServiceDto 매핑
	MessageListServiceDto toMessageListServiceDto(String messageId, Integer count, RequestDto requestDto);
}
```

### default 값 지정 & 특정 필드 매핑 무시
```java
@Mapper(imports = UUID.class)
public interface MessageMapper {
	MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);

  @Mapping(source = "messageId", target = "messageId", defaultExpression = "java(UUID.randomUUID().toString())")
  @Mapping(source = "requestDto.type", target = "type", defaultValue = "SMS")
  @Mapping(source = "requestDto.sender", target="sender", ignore=true)
  MessageListServiceDto toMessageListServiceDto(String messageId, Integer count, RequestDto requestDto);
}
```
- source에 빈 값이 들어오는 경우, NPE를 피하기 위해 default 값을 지정할 수 있음
- 특정 필드를 빼고 매핑해야하는 경우, ignore를 사용해 제외할 수 있음

### 특정 필드 매핑 시 지정 메소드 이용 (enum)
```java
public class MessageListServiceDto {
   private String messageId;
   private Integer count;
   private String title;
   private String content;
   private String sender;
   private List<String> receiver;
   private LocalDateTime requestTime;
   private Type type;
}

public enum Type {
	SMS("SMS"),
	LMS("LMS"),
	MMS("MMS");

	private String code;

	@Override
	public String toString(){
		return this.code;
	}
}

@Mapper(imports = UUID.class)
public interface MessageMapper {
	MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);
  @Mapping(source = "requestDto.type", target = "type", qualifiedByName = "typeToEnum")
  MessageListServiceDto toMessageListServiceDto(String messageId, Integer count, RequestDto requestDto);

  @Named("typeToEnum")
  static Type typeToEnum(String type) {
    switch (type.toUpperCase()) {
    case "LMS":
      return Type.LMS;
    case "MMS":
      return Type.MMS;
    default:
      return Type.SMS;
    }
  }

  // qualifiedByName 없이 작성하는 경우
  default Type toType(String type) {
    switch (type.toUpperCase()) {
      case "LMS":
        return Type.LMS;
      case "MMS":
        return Type.MMS;
      default:
        return Type.SMS;
    }
  }
}
```

### 사용자 정의 매퍼 메소드 (default)
```java
default MessageListServiceDto toMessageListServiceDto(String messageId, Integer count, RequestDto requestDto) {
   String messageType = Optional.ofNullable(requestDto.getType()).orElse("sms").toUpperCase();
   Type msgType = Type.SMS;

   if (messageType.equals("LMS")) {
      msgType = Type.LMS;
   } else if (messageType.equals("MMS")){
      msgType = Type.MMS;
   }

   return MessageListServiceDto.builder()
      .messageId(Optional.ofNullable(messageId).orElse(UUID.randomUUID().toString()))
      .count(Optional.ofNullable(count).orElse(0))
      .title(requestDto.getTitle())
      .content(requestDto.getContent())
      .sender(requestDto.getSender())
      .receiver(Optional.ofNullable(requestDto.getReceiver()).orElse(Collections.EMPTY_LIST))
      .requestTime(LocalDateTime.now())
      .type(msgType)
      .build();
}

// default 사용
MessageListServiceDto messageListServiceDto = MessageMapper.INSTANCE.toMessageListServiceDto(messageId, count, resDto);
```

### MapStruct 정책 (@Mapper 옵션)
- ComponentModel : Mapper를 Bean으로 등록해서 사용하거나, Mapper 내부에서 다른 빈을 주입받아 사용이 필요한 경우 설정
- unmmappedTargetPolicy : Target 필드는 존재하는데, Source의 필드가 없는 경우에 대한 정책
  - ERROR : 매핑 대상이 없는 경우, 매핑 코드 생성 시 error 발생
  - WARN : 매핑 대상이 없는 경우, 빌드 시 warn 발생
  - IGNORE : 매핑 대상이 없는 경우, 무시하고 매핑
- nullValueMappingStrategy / nullValueIterableMappingStrategy : Source가 null인 경우 제어할 수 있는 null 정책
  - RETURN_NULL : Source가 null일 경우, Target을 null로 설정
  - RETURN_DEFAULT : Source가 null일 경우, defalut 값으로 설정되며, itarable에는 collection, map은 빈 map으로 매핑됨

### Record & MapStruct
```java
public record MemberCreateRequestDto(
  @NotBlank
  @Pattern(...)
  String username,
  
  @NotBlank
  @Pattern(...)
  @JsonProperty("password")
  String rawPassword,
  
  @NotBlank
  @Pattern(...)
  String nickname
) {}

// 기본 사용
@Mapper
public interface MemberMapper {
  Member from(MemberCreateRequestDto dto, MemberStatus status);
  Member from(MemberCreateRequestDto dto, MemberStatus status, OffsetDateTime joinDate);
}

// 필드 지정
@Mapper
public interface MemberMapper {
  @Mappings({
      @Mapping(target = "password", source = "dto.rawPassword"),
      @Mapping(target = "joinDate", source = "now")
  })
  Member from(
          MemberCreateRequestDto dto,
          MemberStatus status,
          OffsetDateTime now
  );
}
```

### 참고
- https://velog.io/@hyeok_1212/Java-Record-%EC%82%AC%EC%9A%A9%ED%95%98%EC%8B%9C%EB%82%98%EC%9A%94
- https://colevelup.tistory.com/28
- https://www.baeldung.com/java-record-vs-lombok
- https://s7won.tistory.com/2
- https://medium.com/naver-cloud-platform/%EA%B8%B0%EC%88%A0-%EC%BB%A8%ED%85%90%EC%B8%A0-%EB%AC%B8%EC%9E%90-%EC%95%8C%EB%A6%BC-%EB%B0%9C%EC%86%A1-%EC%84%9C%EB%B9%84%EC%8A%A4-sens%EC%9D%98-mapstruct-%EC%A0%81%EC%9A%A9%EA%B8%B0-8fd2bc2bc33b
- https://velog.io/@kevin_/MapStruct-%EC%A0%81%EC%9A%A9-%ED%95%B4%EB%B3%B4%EC%9E%90%EC%9E%87
- https://velog.io/@letsdev/Spring-Boot-DTO%EB%A5%BC-Entity%EB%A1%9C-Entity%EB%A5%BC-DTO%EB%A1%9Cfeat.-MapStruct%EB%8A%94-Record%EB%A5%BC-%EC%A7%80%EC%9B%90%ED%95%A9%EB%8B%88%EB%8B%A4