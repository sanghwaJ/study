# Ingress

## 1. Ingress란?
- k8s에서 클러스터 외부에서 내부 서비스로 HTTP/HTTPS 요청을 어떻게 라우팅할지 결정하는 컴포넌트
- 클러스터 외부에서 접근 가능한 URL을 사용할 수 있게 하며, 트래픽을 로드밸런싱하고, SSL 인증서 처리를 하며, 도메인 기반으로 가상 호스팅을 제공하기도 함
- Ingress는 전제 조건으로 Ingress Controller가 있어야 Ingress를 충족할 수 있음

### Ingress의 주요 기능
- 호스트 기반 라우팅
    - ex) foo.example.com인 경우 service-foo로, bar.example.com인 경우 service-bar로 라우팅
- 경로 기반 라우팅
    - ex) /api인 경우 API 서버로, /static인 경우 정적 파일 서버로 라우팅
- TLS/HTTPS 처리
- 로드밸런싱
    - 여러 pod로 트래픽 분산
- 리다이렉션, 헤더 추가, 인증 플러그인

## 2. Ingress Controller란?
- Ingress는 외부로부터 들어오는 요청에 대한 로드밸런싱, TLS/SSL 인증서 처리, 라우팅 등의 규칙을 정의해둔 자원이며, 실제로 이러한 규칙을 동작하게 하는 것은 Ingress Controller
- 보통의 클라우드 서비스는 다른 설정 없이 로드밸런서 서비스들과 연동해서 Ingress를 사용하지만, 클라우드를 사용하지 않고 직접 구축하는 경우에는 Ingress Controller와 Ingress를 직접 연동시켜야 하며, 이때 주로 Nginx가 사용됨

## 3. Ingress 적용

### ingress.yaml
```yaml
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: batch-visualizer-ingress
  namespace: ns-project
  annotations:
    kubernetes.io/ingress.class: nginx
    #ingress.kubernetes.io/rewrite-target: /
    #ingress.bluemix.net/rewrite-path: "serviceName=app-service rewrite=/"
spec:
  rules:
  - http:
      paths:
      - path: /auth/
        backend:
          serviceName: batch-visualizer-auth-service
          servicePort: 8082
      - path: /
        backend:
          serviceName: batch-visualizer-backend-provider-service
          servicePort: 8084
      - path: /batch/
        backend:
          serviceName: batch-visualizer-backend-migrator-service
          servicePort: 8081
```
- kind는 Ingress로 설정
- namespace는 Ingress에 묶일 서비스와 동일하게 설정
- paths에서 각각 서비스 포트 번호 지정하였고, 위와 같이 path를 호출하게 되면 서비스 호출 시 path에 맞는 각각의 port를 가진 서비스를 호출하게 됨

### Ingress Controller 적용
```shell
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/master/deploy/static/mandatory.yaml
```

### nginx 적용
```shell
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/master/deploy/static/provider/baremetal/service-nodeport.yaml
```

## 4. LoacBalancer와 Ingress 비교

---

### 출처
- https://twofootdog.tistory.com/23