# Spring - @Primary & @Qualifier

## 1. NoUniqueBeanDefinitionException
- Spring에서 같은 타입의 Bean을 두 개 이상 사용하거나 같은 인터페이스의 구현체 클래스 두 개 이상이 Bean으로 등록되면 발생하는 에러
- 이럴 경우 Spring에서 어떤 Bean을 주입해야하는지 알려줘야하는데, 이 때 @Primary나 @Qualifier가 사용됨

## 2. @Primary
- Spring Container가 올라갈 때 Spring은 Component를 Scan하며 Bean을 생성하는데, Spring은 Singleton 전략을 채택하기 때문에 하나의 타입의 Bean은 한 번만 생성되며, 어플리케이션이 구동되는 동안 하나의 Bean을 계속해서 사용함
- 하지만, @Bean으로 생성하는 객체 중 같은 클래스(타입)인 Bean이 있다면, Spring은 어떤 것을 Bean으로 생성해야하는지 알 수 없기 때문에 에러가 발생하게됨
- 이처럼 같은 타입의 Bean을 두 개 이상 생성할 때, 하나의 Bean에 더 높은 선호도를 부여하기 위해 @Primary가 사용됨
```java
@Configuration
public class Config {
    @Bean
    public Employee JohnEmployee() {
        return new Employee("John");
    }

    @Bean
    @Primary
    public Employee TonyEmployee() {
        return new Employee("Tony");
    }
}
```

## 3. @Qualifier
- @Qualifier는 Bean 이름을 탐색할 때 사용되는 추가 구분자를 제공해줌(Bean 이름을 변경하는 것이 아님)
    ```java
    @Component("fooFormatter")
    public class FooFormatter implements Formatter {
        public String format() {
            return "foo";
        }
    }

    @Component("barFormatter")
    public class BarFormatter implements Formatter {
        public String format() {
            return "bar";
        }
    }

    public class FooService {
        @Autowired
        @Qualifier("fooFormatter")
        private Formatter formatter;
    }
    ```
- 다만, 유의할 점은 Qualifier는 문자열을 통해 설정되어 Compile 단계에서 타입 체크가 되지 않기 때문에, 따로 관리하는 것실무에서는 Qualifier를 별도로 관리하는 것이 좋음 
    - Custom Qualifier Annotation 선언
        ```java
        @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
        @Retention(RetentionPolicy.RUNTIME)
        @Documented
        @Qualifier("mainDiscountPolicy")
        public @interface MainDiscountPolicy {  }

        @Component
        @MainDiscountPolicy
        public class RateDiscountPolicy implements DiscountPolicy {}
        //생성자 자동 주입
        @Autowired
        public OrderServiceImpl(MemberRepository memberRepository,
                                @MainDiscountPolicy DiscountPolicy discountPolicy) {
            this.memberRepository = memberRepository;
            this.discountPolicy = discountPolicy;
        }
        ```
    - Bean 이름을 상수로 문자열을 정의해서 사용
        ```java
        @Configuration
        public class AppConfig {
            @Bean(name = BeanNames.MAIN_REPO)
            public UserRepository mainRepository() {
                return new MainUserRepository();
            }
        }

        public class BeanNames {
            public static final String MAIN_REPO = "mainRepository";
        }

        @Autowired
        @Qualifier(BeanNames.MAIN_REPO)
        private UserRepository userRepository;
        ```

## 4. @Primary & @Qualifier
- 정리하자면 @Primary는 두 개 이상의 같은 타입의 Bean이 존재할 때 하나의 구현체를 default 값으로 사용하게 하는 방법이며, @Qualifier는 두 개 이상의 같은 타입의 Bean이 존재할 때 특정 Bean을 주입하도록 하는 방법
- 따라서 같은 타입의 Bean이 여러 개 존재한다면, @Primary를 통해 default를 지정하고, 특정 상황에 사용할 Bean은 @Qualifier를 통해 의존성을 주입하여 사용하는 것이 실무에서 자주 사용됨 (ex. Main DB Connection & Sub DB Connection)
- 우선 순위는 @Qualifier가 @Primary 보다 우선함

### 참고
- https://moonong.tistory.com/96
- https://velog.io/@zihs0822/Qualifier%EC%99%80-Primary
- https://bestinu.tistory.com/58