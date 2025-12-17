# 게임 코드 구조 설명

이 문서는 `was` 프로젝트 내 게임 로직의 새로운 코드 구조를 설명합니다. 기존의 분산된 서비스 기반 아키텍처에서 **상태 패턴(State Pattern)**을 활용한 중앙 집중식 상태 머신 기반 아키텍처로 리팩토링되었습니다.

## 1. 새로운 아키텍처 개요

게임의 복잡한 흐름과 상태 관리를 효율적으로 처리하기 위해, 게임의 각 단계를 독립적인 '상태(State)'로 정의하고, 이 상태들 간의 전이를 중앙에서 관리하는 방식을 도입했습니다. 이를 통해 코드의 응집도를 높이고 결합도를 낮춰 유지보수성과 확장성을 향상시켰습니다.

### 주요 구성 요소:

*   **`GameFlowManager` (서비스):**
    *   게임 로직의 중앙 관리자 역할을 합니다.
    *   각 게임방(`roomId`)별 `GameContext` 인스턴스를 관리합니다.
    *   게임 로직 수행에 필요한 모든 핵심 서비스(예: `GameRepository`, `AIClientService`, `GameBroadCaster` 등)를 주입받아 `GameContext`에 제공합니다.
    *   `GameContext`를 생성하고 관리하는 역할을 담당합니다.

*   **`GameContext` (게임 인스턴스별 컨텍스트):**
    *   각 게임방(`roomId`)마다 고유하게 생성되는 인스턴스입니다.
    *   현재 게임의 `GameState` (현재 상태)를 가지고 있습니다.
    *   `GameFlowManager`를 통해 필요한 서비스에 접근하며, 현재 `GameState`의 `handleEvent` 메소드를 호출하여 이벤트를 처리합니다.
    *   `setState()` 메소드를 통해 현재 게임의 상태를 변경할 수 있습니다.

*   **`GameState` (인터페이스):**
    *   모든 구체적인 게임 상태 클래스들이 구현해야 하는 인터페이스입니다.
    *   `handleEvent(GameContext context, GameEventType eventType, String... args)`: 해당 상태에서 특정 이벤트를 처리하는 메소드입니다.
    *   `getStatus()`: 현재 상태의 `GameStatus` (Enum)를 반환하는 메소드입니다.

*   **`AbstractGameState` (추상 클래스):**
    *   `GameState` 인터페이스를 구현하는 추상 클래스입니다.
    *   모든 구체적인 상태 클래스들이 공통적으로 사용하는 헬퍼 메소드(예: `getGameMetaByRoomId`)를 정의하여 코드 중복을 줄입니다.

*   **구체적인 `GameState` 클래스들:**
    *   게임의 각 단계를 나타내는 실제 상태 클래스들입니다.
    *   `PreGameState`: 게임 시작 전 초기 상태. `GAME_START` 이벤트를 처리하여 게임을 초기화하고 `DrawingPhaseState`로 전환합니다.
    *   `DrawingPhaseState`: 그리기 단계 상태. `ROUND_START` 및 `DRAWING_SUBMIT` 이벤트를 처리하고, 모든 그림이 제출되면 `GuessingState`로 전환합니다.
    *   `GuessingState`: 추측 단계 상태. `GUESS_START`, `GUESS_REQUEST`, `GUESS_SUBMIT` 이벤트를 처리하며, AI 또는 플레이어의 추측 로직을 포함합니다. 단어 추측이 완료되면 `RoundEndState` 또는 다음 추측 요청으로 이어집니다.
    *   `RoundEndState`: 라운드 종료 상태. `ROUND_END` 이벤트를 처리하며, 다음 라운드가 남아있으면 `DrawingPhaseState`로, 아니면 `GameEndState`로 전환합니다.
    *   `GameEndState`: 게임 종료 상태. `GAME_END` 이벤트를 처리하며, 최종 점수 계산 및 게임 데이터 정리를 담당합니다.

## 2. 이벤트 처리 흐름 (예시: 게임 시작)

1.  클라이언트에서 게임 시작 요청 (`RoomEventType.START`)이 발생하면 `GameStartHandler`가 이를 수신합니다.
2.  `GameStartHandler`는 `GameFlowManager`로부터 해당 `roomId`의 `GameContext`를 가져옵니다.
3.  `GameStartHandler`는 `GameContext.handleEvent(GameEventType.GAME_START, userUuid)`를 호출하여 게임 시작 이벤트를 전달합니다.
4.  `GameContext`는 현재 상태인 `PreGameState`의 `handleEvent` 메소드를 호출합니다.
5.  `PreGameState`는 게임 초기화 로직(게임 메타데이터 생성, 플레이어 및 라운드 정보 설정, AI 서버 통신 등)을 수행합니다.
6.  모든 초기화가 완료되면 `PreGameState`는 `context.setState(new DrawingPhaseState())`를 통해 `GameContext`의 상태를 `DrawingPhaseState`로 변경하고, `context.handleEvent(GameEventType.ROUND_START)`를 호출하여 다음 단계(라운드 시작)를 트리거합니다.

## 3. 리팩토링의 이점

*   **명확한 책임 분리:** 각 상태 클래스는 해당 상태에서의 로직만 책임지므로 코드를 이해하고 관리하기가 훨씬 쉬워졌습니다.
*   **응집도 향상:** 특정 게임 단계와 관련된 모든 로직이 해당 상태 클래스 내에 모여 있어 코드의 응집도가 높아졌습니다.
*   **유연한 확장성:** 새로운 게임 상태나 복잡한 규칙을 추가할 때, 기존 코드를 수정하는 대신 새로운 상태 클래스를 추가하고 상태 전이 로직만 변경하면 되므로 확장이 매우 용이합니다.
*   **테스트 용이성:** 각 상태 클래스를 독립적으로 테스트할 수 있어 단위 테스트 작성이 용이해졌습니다.
*   **비동기 처리의 통합:** `Mono`와 `Flux`를 활용한 리액티브 프로그래밍 패러다임을 상태 패턴에 자연스럽게 통합하여, 복잡한 비동기 흐름을 더욱 구조적으로 관리할 수 있게 되었습니다.

이 새로운 구조는 게임 로직의 복잡성을 효과적으로 관리하고, 향후 기능 추가 및 유지보수를 더욱 용이하게 할 것입니다.
