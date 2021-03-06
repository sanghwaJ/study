# Java - JVM(Java Virtual Machine) & Java 컴파일

## 1. JVM이란?

- 시스템 메모리를 관리하면서 Java 기반 어플리케이션을 위해 이식 가능한 실행 환경을 제공
- 어떤 기기 상에서 실행되고 있는 프로세스, 특히 Java 어플리케이션에 대한 리소스를 대표하고 통제하는 서버

## 2. JVM의 기능
- Java 프로그램이 어느 기기나 운영체제 상에서도 실행 될 수 있도록 함
- 프로그램 메모리를 관리하고 최적화 하는 것

## 3. JVM의 메모리 관리
- JVM 실행에 있어 가장 일반적인 상호작용은 힙과 스택의 메모리 사용을 확인하는 것
- 실행과정
  - 프로그램이 실행되면 OS로부터 프로그램이 필요로하는 메모리를 할당 받고, JVM은 이 메모리를 용도에 따라 여러 영역으로 나누고 관리함
  - Java 컴파일러(JAVAC)가 Java 소스코드(.java)를 읽고 Java 바이트코드(.class)로 변환 시킴
  - 변경된 바이트코드(.class)를 클래스로더를 통해 JVM 메모리 영역으로 로딩
    - 클래스로더 : 런타임시 처음으로 바이트코드를 참조할 때, 해당 클래스를 로드하고 메모리 영역에 배치(동적 배치)시키는 역할을 함
  - 로딩된 바이트코드(.class)는 실행 엔진(Execution Engine)을 통해 해석됨
  - 해석된 바이트코드(.class)는 메모리 영역에 배치되어 실질적인 수행이 이뤄지고, 이러한 실행 과정에서 JVM은 필요에 따라 스레드 동기화, GC와 같은 메모리 관리를 수행함

## 4. Java의 컴파일 과정
- Java 컴파일러가 Java 소스파일(.java)을 컴파일한다.
- 컴파일된 바이트코드(.class)를 JVM의 클래스로더에 전달한다.
- 클래스로더는 동적 로딩을 통해 필요한 클래스들을 로딩 및 링크하여 JVM의 메모리에 올린다.
- 실행 엔진(Execution Engine)은 JVM 메모리에 올라온 바이트코드(.class)를 명령어 단위로 하나씩 가져와 실행하며, 이 때 실행 엔진은 인터프리터, JIT 컴파일러 두 가지 방식으로 변경한다.
  - 인터프리터 : 바이트코드 명령어를 하나씩 해석하고 실행, 하나 하나의 실행은 빠르지만 전체 실행 속도는 느림
  - JIT 컴파일러(Just-In-Time Compiler) : 바이트코드 전체를 컴파일하여 바이너리코드로 변경하고, 그 이후에는 메소드를 더이상 인터프리팅 하지 않고 바이너리코드로 직접 실행하는 방식. 바이트코드 전체가 컴파일된 바이너리코드를 실행하는 것이라 인터프리터보다 빠르다.