# 프로그래밍 언어 별 특징 (C, C++, Java, Python)

## 1. C
- 포인터를 사용하는 저수준 언어이자, 절차지향 프로그래밍 언어

### 1-1. C의 장점
- 다양한 하드웨어로의 이식성이 좋다.
- 절차지향 프로그래밍 언어로 코드가 복잡하지 않아 유지보수가 쉽다.
- 저급 언어의 특징을 가지고 있으므로, 어셈블리어 수준으로 하드웨어를 제어할 수 있다.
- 코드가 간결하며, 완성된 프로그램의 크기(용량)가 작고 실행 속도가 빠르다.

### 1-2. C의 단점
- 저수준 언어의 특징을 가지고 있어, Java나 Python에 비해 습득이 어렵다.
- 시스템 자원을 직접 제어해야 하므로, 프로그래밍을 하는데 주의를 기울여야 한다.

### ※ 저수준 언어 & 고수준 언어
- 저수준 언어
  - 컴퓨터가 이해하기 쉽게 작성된 프로그래밍 언어로, 기계어와 어셈블리어 들이 있음
  - 실행속도가 빠른 장점이 있음
- 고수준 언어
  - 컴파일러나 인터프리터에 의해 기계가 번역할 수 있는 언어로, 번역이 되어야 실행이 되기 때문에 저수준 언어보다 상대적으로 실행속도가 느림
  - 학습하고, 코드를 작성하는 것은 저수준 언어에 비해 효율적

## 2. C++
- C에 객체지향 기능을 추가한 프로그래밍 언어
- C를 기반으로 만들었기 대문에, C로 작성된 프로그램의 호환성을 유지한다는 장점이 있다.

### 2-1. C++의 장점
- 객체지향 언어로, 구조화된 프로그래밍이 가능
- 사용법이 쉬우면서도, 어셈블리어에 크게 떨어지지 않는 제어 능력
- 이식성과 유연성이 뛰어나 많은 기종에서 큰 수정 없이 사용 가능

### 2-2. C++의 단점
- 기능이 다양하고 성능이 좋지만, 그만큼 양이 방대하여 학습 난이도가 높음
- C를 기반으로 만들어졌기 때문에, C의 선 이해가 필요

### ※ 객체지향 프로그래밍 언어의 특징
- 객체와 캡슐화
  - Class : 캡슐의 역할을 하고, 객체를 정의함
  - 객체 : Class라는 틀에서 생성된 Instance
- 상속성
  - 부모 Class에서 선언된 변수나 함수를 자식 Class로 상속하여 자식 Class에서 사용
  - 구현된 코드의 재사용성을 높이고, 소프트웨어 생산성을 높임
- 다형성
  - 하나의 객체가 여러가지 타입으로 표현될 수 있는 것 
  - 부모 Class 타입의 참조변수로 자식 Class 타입의 인스턴스를 참조할 수 있도록 구현, 하나의 타입으로 다양한 실행 결과를 얻을 수 있으며, 객체를 부품화하여 유지보수를 용이하게 함
- 추상화
  - Class의 공통된 속성을 뽑아내어 구현하는 것
  - 공통된 부분의 코드 작성을 줄여 비효율적인 코드를 줄이고, 유지보수 측면에서도 유리

## 3. Java
- C에 객체지향 기능을 추가한 C++과 달리, 처음부터 객체지향 프로그래밍 언어로 개발 
- 다만, Java는 원시 타입을 객체로 취급하지 않기 때문에 순수 객체지향은 아니며, 모든 것을 객체로 취급하는 Python, Ruby와 같은 프로그래밍 언어들을 순수 객체지향 언어라고 함
- 포인터 배제, Garbage Collector 내장, 고수준 객체지향 부분에 포커싱

### 3-1. Java의 특징
- JVM(Java Virtual Machine)을 통해 컴파일(.java => .class)하여 각 플랫폼에 독립적인 언어로, 즉, JVM 위에서 자바 프로그램이 실행되기 때문에, CPU나 OS와 무관하게 동일하게 프로그램이 작동됨
- Garbage Collector 기능으로 메모리를 자동으로 관리
- 멀티쓰레드 기능으로 하나의 프로그램에서 여러 쓰레드를 동시에 실행할 수 있음
- 동적 로딩 : 어플리케이션이 실행 될 때, 모든 객체가 생성되지 않고 객체가 필요한 시점에 클래스를 동적으로 생성하는 것. 동적 로딩은 클래스가 일부 변경되었을 때 다시 컴파일하지 않아도 되는 이점이 있음

### 3-2. Java의 장점
- 수많은 레퍼런스가 있어 학습의 용이
- C나 C++에 비해 코드 생산성이 높고, 가독성이 높음
- OS와 무관하게 JVM 위에서 실행되기 때문에 호환성이 좋음
- 포인터 연산자와 메모리 직접 접근 함수를 지원하지 않아 안정성이 높음

### 3-3. Java의 단점
- C나 C++에 비해 연산 속도가 느림 (Python, JavaScript 보단 빠름)
- JVM이 완벽이 로딩되어야 프로그램이 시작하기 때문에 실행에 시간이 오래 걸림
- 예외처리를 꼭 선언해주어야 하며, 에러 발생 시 컴파일조차 진행되지 않음
- 다른 프로그래밍 언어에 비해 코드 길이가 긴 편

## 4. Python
- 객체지향 기능의 인터프리터 언어로, 컴파일-실행-디버그라는 기존의 절차에서 벗어나 작성 뒤 바로 테스트를 할 수 있음

### 4-1. Python의 특징
- 순수 객체지향 언어로, 원시 타입이 존재하지 않고, 클래스나 함수를 포함한 모든 것을 객체로 취급함
- 단순한 문법 구조와 동시에 엄격한 문법 구조를 가지고 있음 (ex. 들여쓰기)
- 뛰어난 코드 생산성으로 개발 기간 단축에 초점을 두고 있음
- 동적으로 데이터 타입 결정
- 가비지 컬렉터 기능으로 메모리를 자동 관리

### 4-2. Python의 장점
- 쉬운 문법으로 높은 코드 생산성, 빠른 학습, 범용성에 장점
- 코드가 깔끔하여 유지보수나 팀워크에 유리
- Numpy, Pandas와 같은 라이브러리로 과학/공학 분야에서 뛰어난 성능
- 많은 수의 라이브러리와, 다른 언어의 라이브러리 또한 쉽게 접근이 가능하여 높은 확장성을 가짐

### 4-3. Python의 단점
- 다른 프로그래밍 언어에 비해 속도가 많이 느림
- 멀티쓰레딩이 불가함 (이를 해결하기 위해 다양한 라이브러리를 사용하는 등의 방법이 고안되고 있음)
- 패키지 설치나 관리가 불편함