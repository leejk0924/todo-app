# Todo API

회원 가입/로그인 없이 할 일(Todo)을 관리하는 REST API 서버입니다.

## 실행 방법

- JDK 21 필요
- DB: H2 인메모리 DB를 사용하며, 별도 설치나 준비 과정이 필요 없습니다. 애플리케이션이 시작될 때 자동으로 생성됩니다.

프로젝트 루트(`todo/`)에서 아래 명령 하나로 실행합니다.

```bash
./gradlew bootRun
```

- 기본 포트: `8080`
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI 문서(JSON): http://localhost:8080/v3/api-docs
- H2 콘솔: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:tododb`, 사용자명: `sa`, 비밀번호: 없음)

테스트 실행:

```bash
./gradlew test
```

## API 명세

Base URL: `/api/todos`

| 메서드 | 경로 | 설명 | 요청 본문 | 응답 본문 | 상태 코드 |
|---|---|---|---|---|---|
| POST | `/api/todos` | 할 일 생성 | `{title, description}` | `TodoResponse` | 201 (Location 헤더 포함), 400 |
| GET | `/api/todos` | 목록 조회 | - | `TodoResponse[]` | 200 |
| GET | `/api/todos/{id}` | 단건 조회 | - | `TodoResponse` | 200, 404 |
| PUT | `/api/todos/{id}` | 수정 | `{title, description}` | `TodoResponse` | 200, 400, 404 |
| DELETE | `/api/todos/{id}` | 삭제 | - | (본문 없음) | 204, 404 |
| PATCH | `/api/todos/{id}/complete` | 완료 처리 | - | `TodoResponse` | 200, 404 |
| PATCH | `/api/todos/{id}/reopen` | 완료 취소(재개) | - | `TodoResponse` | 200, 404 |

### `TodoResponse`

```json
{
  "id": 1,
  "title": "장보기",
  "description": "우유, 계란 사기",
  "status": "PENDING",
  "createdAt": "2026-09-28T01:08:15.026869",
  "updatedAt": "2026-09-28T01:08:15.026869"
}
```

- `status`: `PENDING`(예정) / `DOING`(진행중) / `COMPLETED`(완료) 중 하나

### 오류 응답 모양

모든 오류는 [RFC 9457 (Problem Details for HTTP APIs)](https://www.rfc-editor.org/rfc/rfc9457) 형식을 따르며, 어떤 오류든 아래와 같은 한 가지 모양으로 응답합니다.

```json
{
  "type": "urn:problem-type:{domain}:{slug}",
  "title": "HTTP 상태 문구",
  "status": 400,
  "detail": "사람이 읽을 수 있는 구체적인 오류 사유",
  "instance": "/api/todos/1",
  "errorCode": "INVALID_TITLE"
}
```

- `type`/`title`/`status`/`detail`/`instance`: RFC 9457 표준 필드
- `errorCode`: 클라이언트가 메시지를 파싱하지 않고 분기 처리할 수 있도록 추가한 확장 필드

| errorCode | 상태 코드 | 발생 조건 |
|---|---|---|
| `INVALID_TITLE` | 400 | 제목이 비어 있거나 공백뿐인 경우 |
| `TITLE_TOO_LONG` | 400 | 제목이 100자를 넘는 경우 |
| `TODO_NOT_FOUND` | 404 | 존재하지 않는 id를 조회/수정/삭제/완료처리 하는 경우 |
| `INTERNAL_SERVER_ERROR` | 500 | 예상하지 못한 서버 오류 |

## 설계 설명

### 주소(URL)와 상태 코드

- 리소스를 명사(`/api/todos`)로 표현하고, 개별 리소스는 `/api/todos/{id}`로 식별했습니다.
- 완료/미완료 전환은 리소스 전체를 덮어쓰는 게 아니라 상태 전이 그 자체를 의미하므로, `PUT` 수정 API에 끼워 넣지 않고 `PATCH /api/todos/{id}/complete`, `PATCH /api/todos/{id}/reopen` 이라는 별도 액션 엔드포인트로 분리했습니다. 이렇게 하면 클라이언트가 완료 처리를 위해 title/description까지 항상 같이 보낼 필요가 없고, 의도가 URL에 명시적으로 드러납니다.
- 생성은 `201 Created`와 함께 생성된 리소스의 위치를 `Location` 헤더로 반환합니다.
- 삭제는 반환할 본문이 없으므로 `204 No Content`를 사용합니다.
- 존재하지 않는 리소스에 대한 조회/수정/삭제/상태 변경은 예외 없이 전부 `404`로 응답합니다.
- 검증 실패(빈 제목, 100자 초과)는 `400`과 함께 `errorCode`로 구체적인 사유를 알려줍니다.

### 오류 응답을 RFC 9457로 통일한 이유

프로젝트마다 제각각인 오류 응답 형식 대신 표준화된 RFC 9457을 채택해 모든 예외(도메인 검증 실패, 404, 예상 못한 서버 오류)가 하나의 `ApiControllerAdvice`를 거쳐 항상 같은 모양으로 나가도록 했습니다. `errorCode`는 표준에서 허용하는 확장 필드로 추가했습니다.

### DB로 H2를 고른 이유

과제 요구사항상 "저장소를 받은 사람이 명령 하나로 서버를 띄울 수 있어야" 하므로, 별도 설치나 컨테이너 없이 애플리케이션 시작과 동시에 준비되는 인메모리 DB인 H2를 선택했습니다.

### 계층 구조

```
controller  (HTTP, DTO 변환)
service     (유스케이스, 트랜잭션 경계)
repository  (포트 인터페이스 + JPA 어댑터, JPA Entity ↔ 도메인 변환)
domain      (Rich Domain Model — 자기 자신의 불변식을 스스로 지킴)
global      (예외 처리, 설정 등 횡단 관심사)
```

- 요청/응답에는 JPA 엔티티(`TodoEntity`)를 직접 노출하지 않고, `TodoCreateRequest`/`TodoUpdateRequest`/`TodoResponse` DTO를 사용합니다.
- 도메인 모델(`Todo`)과 JPA 엔티티(`TodoEntity`)를 분리해 영속성 관심사(auditing, 컬럼 매핑)와 비즈니스 규칙(제목 검증, 상태 전이)을 독립적으로 유지합니다. `TodoMapper`가 둘을 상호 변환합니다.

## 실행 결과

### 정상 흐름: 생성 → 목록 조회 → 완료 처리 → 삭제

**1) 생성**

```bash
curl -i -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -d '{"title":"장보기","description":"우유, 계란 사기"}'
```

```
HTTP/1.1 201
Location: /api/todos/1
Content-Type: application/json

{"id":1,"title":"장보기","description":"우유, 계란 사기","status":"PENDING","createdAt":"2026-09-28T01:08:15.026869","updatedAt":"2026-09-28T01:08:15.026869"}
```

**2) 목록 조회**

```bash
curl -i http://localhost:8080/api/todos
```

```
HTTP/1.1 200
Content-Type: application/json

[{"id":1,"title":"장보기","description":"우유, 계란 사기","status":"PENDING","createdAt":"2026-09-28T01:08:15.026869","updatedAt":"2026-09-28T01:08:15.026869"}]
```

**3) 완료 처리**

```bash
curl -i -X PATCH http://localhost:8080/api/todos/1/complete
```

```
HTTP/1.1 200
Content-Type: application/json

{"id":1,"title":"장보기","description":"우유, 계란 사기","status":"COMPLETED","createdAt":"2026-09-28T01:08:15.026869","updatedAt":"2026-09-28T01:08:17.290142"}
```

**4) 삭제**

```bash
curl -i -X DELETE http://localhost:8080/api/todos/1
```

```
HTTP/1.1 204
```

### 오류 응답 예시

**존재하지 않는 id 조회 (404)**

```bash
curl -i http://localhost:8080/api/todos/1
```

```
HTTP/1.1 404
Content-Type: application/problem+json

{"detail":"존재하지 않는 할 일입니다.","instance":"/api/todos/1","status":404,"title":"Not Found","type":"urn:problem-type:todo:todo-not-found","errorCode":"TODO_NOT_FOUND"}
```

**빈 제목으로 생성 (400)**

```bash
curl -i -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -d '{"title":"   ","description":"설명"}'
```

```
HTTP/1.1 400
Content-Type: application/problem+json

{"detail":"제목은 비어 있을 수 없습니다.","instance":"/api/todos","status":400,"title":"Bad Request","type":"urn:problem-type:todo:invalid-title","errorCode":"INVALID_TITLE"}
```

## 테스트

도메인/영속성/서비스/컨트롤러 계층 전부에 대해 단위 테스트와 통합 테스트(`@SpringBootTest` + `MockMvc`)를 작성했습니다. 정상 흐름뿐 아니라 400(검증 실패), 404(존재하지 않는 리소스) 케이스도 함께 검증합니다.

```bash
./gradlew test
```
