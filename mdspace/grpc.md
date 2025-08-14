### 1. gRPC란?
- RPC(Remote Procedure Call)
	- 원격 프로시저(함수) 호출을 의미하며, 클라이언트가 네트워크를 통해 원격 서버에 있는 함수를 호출할 수 있도록 하는 기술로, 클라이언트가 서버에서 실행되는 서비스의 기능을 마치 로컬에서 호출하듯이 이용할 수 있음
	- RPC는 개발 언어에 세부 정보에 구애받지 않고 원격에 있는 프로시저(함수) 호출이 가능하여, 개발자는 고유의 프로그램 개발에만 집중할 수 있다는 장점이 있음
- gRPC
	- 구글에서 만든 RPC 플랫폼으로, 여러 프로토콜과 직렬화 형식이 사용이 가능한 RPC와 달리, 프로토콜은 HTTP 2.0, 데이터 직렬화 형식은 Protocol Buffers을 사용하여 이진 byte 형식으로 데이터를 전송
	- RPC는 단순 요청-응답 모델을 따르지만, gRPC는 클라이언트 스트리밍, 서버 스트리밍, 양방향 스트리밍과 같은 다양한 호출 패턴을 지원
### 2. gRPC의 구성 요소
- HTTP 2.0
	- 다중화 : 여러 개의 요청과 응답을 동시에 처리할 수 있음
	- 서버 푸시 : 서버가 클라이언트에 데이터를 푸시할 수 있음
	- 헤더 압축 : 데이터 전송에 필요한 오버헤드를 줄여 성능을 향상 시킬 수 있음
- Protocol Buffers
	- 빠른 직렬화 및 역직렬화 : 직렬화된 byte 형식으로 데이터를 전송하여 기존 XML이나 JSON보다 더 가볍고 빠름
	- 언어 및 플랫폼 독립성 : 다양한 언어에 대해 코드 생성을 지원함
	- 유연성 : 데이터 구조를 쉽게 업데이트 할 수 있음
- gRPC는 HTTP 2.0과 Protocol Buffers를 통해 높은 처리량과 낮은 지연 시간을 제공함
### 3. gRPC의 특징
- 고성능 : HTTP 2.0의 다중화 기능과 Protocol Buffers의 빠른 직렬화, 역직렬화 속도로 높은 성능이 특징
- 다양한 호출 패턴 지원 : 단순한 요청-응답 모델 이외에도 서버 스트리밍, 클라이언트 스트리밍, 양방향 스트리밍 등 다양한 호출 패턴을 지원함
- 언어 및 플랫폼 독립성 : 다양한 개발 언어와 플랫폼을 지원하는 코드를 자동으로 생성할 수 있어, 서로 다른 언어의 시스템 간이라도 통신이 용이함
- 보안성 : gRPC는 기본적으로 TLS를 사용하여 통신의 보안성과 데이터 전송 안전성을 제공함
- 간편한 API 정의 : Protocol Buffers 사용으로 API 정의 및 변경 구현이 용이함
- 위와 같은 특징들로 gRPC는 대규모 분산 시스템이나 MSA, 혹은 실시간 데이터 처리가 필요한 상황에서 유용하게 사용될 수 있음
	- ex) OCPP, 스트리밍, IoT와 같은 실시간 통신이 필요한 경우
### 4. gRPC의 사용
- .proto 파일을 통해 gRPC 서비스 정의
  ```java
	syntax = "proto3";

	package greeter;
	
	// 서비스 정의
	service Greeter {
	  rpc SayHello (HelloRequest) returns (HelloReply);
	}
	
	// 요청 메시지 정의
	message HelloRequest {
	  string name = 1;
	}
	
	// 응답 메시지 정의
	message HelloReply {
	  string message = 1;
	}
	```
- gRPC Service 구현
	```java
	@GrpcService
	public class GreeterService extends GreeterGrpc.GreeterImplBase {
	    @Override
	    public void sayHello(HelloRequest request, 
							    StreamObserver<HelloReply> responseObserver) {
	        String message = "Hello, " + request.getName();
	        HelloReply reply = HelloReply.newBuilder()
									        .setMessage(message).build();
	        responseObserver.onNext(reply);
	        responseObserver.onCompleted();
	    }
	}
	```
- gRPC Client 구현
	```java
	public class GreeterClient {
	    public static void main(String[] args) {
	        // gRPC 서버와 연결
	        ManagedChannel channel = ManagedChannelBuilder
								        .forAddress("localhost", 6565)
						                .usePlaintext() // TLS 사용 안함
						                .build();
	
	        GreeterGrpc.GreeterBlockingStub stub = GreeterGrpc
											        .newBlockingStub(channel);
	
	        // 요청 생성
	        HelloRequest request = HelloRequest.newBuilder()
								                .setName("World")
								                .build();
	
	        // 응답 수신
	        HelloReply reply = stub.sayHello(request);
	        System.out.println("Response from server: " + reply.getMessage());
	
	        // 채널 종료
	        channel.shutdown();
	    }
	}
	```

출처
https://velog.io/@dojun527/gRPC%EB%9E%80
https://leeseojune53.tistory.com/28