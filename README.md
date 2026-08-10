
---

## 🔑 Core Considerations

### 1. 통신 방식
- 매칭 결과를 UE5 클라이언트에 전달하기 위해 **REST API만으로는 부족**
- 선택지 정리:
  - **WebSocket** (`global/config/WebSocketConfig.java`) → 실시간 푸시 알림
  - **Polling** → 클라이언트가 주기적으로 매칭 상태 확인

### 2. 상태 관리
- 매칭 큐는 **속도가 중요**하므로 RDB 대신 **Redis 같은 인메모리 저장소** 사용
- Redis를 통해 빠른 조회/삭제/매칭 연산 수행

### 3. 세션 할당
- 매칭 성립 후 UE5 Dedicated Server를 어떻게 띄울지 결정 필요
  - **Agones** (Kubernetes 기반 게임 서버 오케스트레이션)
  - **직접 관리** (서버 프로세스 직접 실행 및 모니터링)
- 선택한 방식에 따라 `SessionAllocator` 구현이 달라질 예정

---

## ⚙️ Flow Overview

1. **클라이언트 요청**
   - `MatchRequest` (게임모드, 지역, MMR 등) → `MatchmakingController`
2. **큐 등록**
   - `MatchQueue` 엔트리 생성 → Redis 저장
3. **매칭 로직**
   - `MatchmakingService`가 조건에 맞는 상대 탐색
   - 매칭 성립 시 `GameSession` 생성
4. **세션 할당**
   - `SessionAllocator`가 UE5 Dedicated Server 배정
5. **결과 통지**
   - `MatchResponse` (서버IP, 포트, 세션ID) 반환
   - `MatchFoundEvent` → WebSocket으로 클라이언트 알림

---

## 🚀 Next Steps

- [ ] Redis 기반 큐 관리 구현
- [ ] WebSocketConfig 설정 및 클라이언트 통신 테스트
- [ ] SessionAllocator 구현 방식 결정 (Agones vs 직접 관리)
- [ ] 매칭 알고리즘 (MMR, 지역, 대기시간 등) 세부 설계
