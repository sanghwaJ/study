# Java - Casting (업캐스팅 & 다운캐스팅)

## 1. 캐스팅이란?

변수가 원하는 정보를 다 가지고 있는 것.

```Java
int a = 0.1 // (1) 에러 발생 X
int b = (int) true // (2) 에러 발생 O, boolean은 int로 캐스팅 불가
```

(1)은 double형이지만, int로 될 정보를 가지고 있음.

(2)의 경우, true는 int형이 될 정보를 가지고 있지 않음.

<br/>

## 2. 캐스팅이 필요한 이유

1. 다형성 : 오버라이딩된 함수를 분리해서 활용할 수 있다. 
   
   => [참고 (추상화와 다형성)](java_AbstractionAndPolymorphism.md)

2. 상속 : 캐스팅을 통해 범용적인 프로그래밍이 가능하다.


