
    const schema = {
  "asyncapi": "2.6.0",
  "id": "gotcha:websocket-api",
  "defaultContentType": "application/json",
  "info": {
    "title": "Gotcha WebSocket API",
    "version": "1.0.0",
    "description": "이 문서는 Gotcha 게임 플랫폼의 실시간 WebSocket(STOMP) 통신 명세서입니다.\nSockJS를 통해 WebSocket 연결을 시도합니다.\n\n| 주체             | 동작       | 사용하는 STOMP 함수       | 경로 예시              |\n|------------------|------------|---------------------------|-------------------------|\n| 클라이언트 → 서버 | 메시지 보냄 | `stompClient.send()`      | `/pub/**`      |\n| 서버 → 클라이언트 | 메시지 보냄 | `stompClient.subscribe()` | `/sub/**`         |\n\n⭐[도메인 채널 prefix]\n-\n\n본 서비스는 총 4개의 메시지 전송 경로(prefix)를 사용하며, 구체적인 로직 분기는 경로가 아닌 DTO 필드로 구분됩니다. \n\n- /pub/room/{roomId} : 대기방 관련 로직들 (대기방 내 채팅 포함)\n- /pub/chat/** : 채팅 관련 로직들 (전체 채팅 및 개인 채팅)\n- /pub/game/{roomId} : 게임 관련 로직들 \n- /pub/lobby/** : 로비 관련 로직들 \n\n⭐[에러 채널]\n-\n\n- 대기방 에러 채널 : /user/room/errors/{userUuid}\n- 로비 에러 채널 : /user/lobby/errors/{userUuid}\n- 채팅 에러 채널 : /user/chat/errors/{userUuid}\n- 게임 에러 채널 : /user/game/errors/{userUuid}\n- 기본 에러 채널 : /user/queue/errors/{userUuid}\n"
  },
  "servers": {
    "production": {
      "url": "http://43.203.244.35:8080/ws-connect",
      "protocol": "ws",
      "description": "SockJS 기반 STOMP WebSocket 연결을 지원합니다. 기본적으로 WebSocket(ws) 사용, 실패 시 HTTP long-polling 등 으로 fallback 됩니다.\n핸드쉐이크 시, 반드시 HTTP 헤더에 Authorization: Bearer {JWT 토큰} 를 포함해야 합니다.\n"
    }
  },
  "channels": {
    "/user/queue/errors/{userUuid}": {
      "description": "도메인 에러 외 서버에서 발생된 에러 메시지를 수신하는 WebSocket 구독 채널입니다.\n",
      "parameters": {
        "userUuid": {
          "description": "에러 메시지를 수신할 사용자의 UUID",
          "schema": {
            "type": "string",
            "x-parser-schema-id": "userUuid"
          }
        }
      },
      "subscribe": {
        "message": {
          "name": "ExceptionResponse",
          "summary": "에러 응답 메시지",
          "payload": {
            "type": "object",
            "description": "API 예외 응답 구조",
            "properties": {
              "code": {
                "type": "string",
                "description": "에러 코드",
                "x-parser-schema-id": "<anonymous-schema-1>"
              },
              "status": {
                "type": "integer",
                "format": "int32",
                "description": "HTTP 상태 코드",
                "x-parser-schema-id": "<anonymous-schema-2>"
              },
              "message": {
                "type": "string",
                "description": "에러 메시지",
                "x-parser-schema-id": "<anonymous-schema-3>"
              },
              "fields": {
                "type": "object",
                "description": "필드별 상세 오류 메시지 (있을 경우)",
                "additionalProperties": {
                  "type": "string",
                  "x-parser-schema-id": "<anonymous-schema-5>"
                },
                "x-parser-schema-id": "<anonymous-schema-4>"
              }
            },
            "required": [
              "code",
              "status",
              "message"
            ],
            "x-parser-schema-id": "ExceptionRes"
          }
        }
      }
    },
    "/user/room/errors/{userUuid}": {
      "description": "대기방에서 발생한 에러 메시지를 수신하는 WebSocket 구독 채널입니다. (대기방 채팅에서 발생된 에러도 포함됩니다.)\n",
      "parameters": {
        "userUuid": {
          "description": "에러 메시지를 수신할 사용자의 UUID",
          "schema": {
            "type": "string",
            "x-parser-schema-id": "userUuid"
          }
        }
      },
      "subscribe": {
        "message": {
          "name": "ExceptionResponse",
          "summary": "에러 응답 메시지",
          "payload": "$ref:$.channels./user/queue/errors/{userUuid}.subscribe.message.payload"
        }
      }
    },
    "/user/game/errors/{userUuid}": {
      "description": "게임에서 발생한 에러 메시지를 수신하는 WebSocket 구독 채널입니다.\n",
      "parameters": {
        "userUuid": {
          "description": "에러 메시지를 수신할 사용자의 UUID",
          "schema": {
            "type": "string",
            "x-parser-schema-id": "userUuid"
          }
        }
      },
      "subscribe": {
        "message": {
          "name": "ExceptionResponse",
          "summary": "에러 응답 메시지",
          "payload": "$ref:$.channels./user/queue/errors/{userUuid}.subscribe.message.payload"
        }
      }
    },
    "/user/lobby/errors/{userUuid}": {
      "description": "로비에서 발생한 에러 메시지를 수신하는 WebSocket 구독 채널입니다.\n",
      "parameters": {
        "userUuid": {
          "description": "에러 메시지를 수신할 사용자의 UUID",
          "schema": {
            "type": "string",
            "x-parser-schema-id": "userUuid"
          }
        }
      },
      "subscribe": {
        "message": {
          "name": "ExceptionResponse",
          "summary": "에러 응답 메시지",
          "payload": "$ref:$.channels./user/queue/errors/{userUuid}.subscribe.message.payload"
        }
      }
    },
    "/user/chat/errors/{userUuid}": {
      "description": "채팅(전체체팅 및 개인 채팅)에서 발생한 에러 메시지를 수신하는 WebSocket 구독 채널입니다.\n",
      "parameters": {
        "userUuid": {
          "description": "에러 메시지를 수신할 사용자의 UUID",
          "schema": {
            "type": "string",
            "x-parser-schema-id": "userUuid"
          }
        }
      },
      "subscribe": {
        "message": {
          "name": "ExceptionResponse",
          "summary": "에러 응답 메시지",
          "payload": "$ref:$.channels./user/queue/errors/{userUuid}.subscribe.message.payload"
        }
      }
    },
    "/pub/lobby/create": {
      "description": "클라이언트가 새 방을 생성 요청하는 채널",
      "publish": {
        "message": {
          "payload": {
            "type": "object",
            "required": [
              "title",
              "maxUser",
              "hasPassword",
              "difficulty",
              "gameType",
              "roundCount"
            ],
            "properties": {
              "title": {
                "type": "string",
                "description": "제목 (필수 입력)",
                "x-parser-schema-id": "<anonymous-schema-6>"
              },
              "maxUser": {
                "type": "integer",
                "description": "최대 인원수 (필수 입력)",
                "x-parser-schema-id": "<anonymous-schema-7>"
              },
              "hasPassword": {
                "type": "boolean",
                "description": "비밀번호 사용 여부",
                "x-parser-schema-id": "<anonymous-schema-8>"
              },
              "password": {
                "type": "string",
                "description": "문자열이 숫자 4자리로만 구성되어 있어야 한다",
                "x-parser-schema-id": "<anonymous-schema-9>"
              },
              "difficulty": {
                "type": "string",
                "enum": [
                  "BASIC",
                  "ADVANCED"
                ],
                "description": "인공지능 난이도 (필수)",
                "x-parser-schema-id": "<anonymous-schema-10>"
              },
              "gameType": {
                "type": "string",
                "enum": [
                  "TRICK_MYOMYO",
                  "LULU_ART_EXAM"
                ],
                "description": "묘묘 - 2인 / 루루 - 1인 게임 모드",
                "x-parser-schema-id": "<anonymous-schema-11>"
              },
              "roundCount": {
                "type": "integer",
                "minimum": 1,
                "maximum": 5,
                "description": "라운드 수 (1~5 사이)",
                "x-parser-schema-id": "<anonymous-schema-12>"
              }
            },
            "x-parser-schema-id": "CreateRoomRequest"
          },
          "x-parser-message-name": "<anonymous-message-1>"
        }
      }
    },
    "/sub/lobby/create/{userUuid}": {
      "description": "클라이언트가 새 방을 생성 응답하는 채널",
      "parameters": {
        "userUuid": {
          "description": "사용자의 UUID",
          "schema": {
            "type": "string",
            "x-parser-schema-id": "userUuid"
          }
        }
      },
      "subscribe": {
        "message": {
          "payload": {
            "type": "object",
            "description": "방 ID를 반환하는 응답 객체",
            "properties": {
              "roomId": {
                "type": "string",
                "description": "생성된 방의 고유 ID",
                "x-parser-schema-id": "<anonymous-schema-13>"
              }
            },
            "required": [
              "roomId"
            ],
            "x-parser-schema-id": "RoomIdRes"
          },
          "x-parser-message-name": "<anonymous-message-2>"
        }
      }
    },
    "/pub/lobby/join/{roomId}": {
      "description": "클라이언트가 방에 참가 요청하는 채널",
      "parameters": {
        "roomId": {
          "description": "대기방의 고유 식별자",
          "schema": {
            "type": "string",
            "x-parser-schema-id": "roomId"
          }
        }
      },
      "publish": {
        "message": {
          "payload": "$ref:$.channels./pub/lobby/create.publish.message.payload",
          "x-parser-message-name": "<anonymous-message-3>"
        }
      }
    },
    "/sub/lobby/join/{userUuid}": {
      "description": "클라이언트가 방에 참가 응답 채널",
      "parameters": {
        "userUuid": {
          "description": "사용자의 UUID",
          "schema": {
            "type": "string",
            "x-parser-schema-id": "userUuid"
          }
        }
      },
      "subscribe": {
        "message": {
          "payload": "$ref:$.channels./sub/lobby/create/{userUuid}.subscribe.message.payload",
          "x-parser-message-name": "<anonymous-message-4>"
        }
      }
    },
    "/sub/lobby/list/event": {
      "description": "클라이언트가 대기방의 목록을 업데이트 하기 위해 이벤트 정보와 대기방의 요약 정보를 받아오는 채널",
      "subscribe": {
        "message": {
          "oneOf": [
            {
              "name": "Room Create",
              "summary": "새로운 방 생성됨",
              "payload": {
                "type": "object",
                "properties": {
                  "userId": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-14>"
                  },
                  "topic": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-15>"
                  },
                  "payload": {
                    "type": "object",
                    "properties": {
                      "eventType": {
                        "type": "string",
                        "enum": [
                          "CREATE"
                        ],
                        "x-parser-schema-id": "<anonymous-schema-17>"
                      },
                      "eventAt": {
                        "type": "string",
                        "format": "date-time",
                        "x-parser-schema-id": "<anonymous-schema-18>"
                      },
                      "data": {
                        "type": "object",
                        "properties": {
                          "roomId": {
                            "type": "string",
                            "description": "방 ID",
                            "x-parser-schema-id": "<anonymous-schema-19>"
                          },
                          "title": {
                            "type": "string",
                            "description": "방 제목",
                            "x-parser-schema-id": "<anonymous-schema-20>"
                          },
                          "owner": {
                            "type": "string",
                            "description": "방장 닉네임",
                            "x-parser-schema-id": "<anonymous-schema-21>"
                          },
                          "hasPassword": {
                            "type": "boolean",
                            "description": "비밀번호 사용 여부",
                            "x-parser-schema-id": "<anonymous-schema-22>"
                          },
                          "maxUser": {
                            "type": "integer",
                            "description": "최대 인원 수",
                            "x-parser-schema-id": "<anonymous-schema-23>"
                          },
                          "currentUser": {
                            "type": "integer",
                            "description": "현재 인원 수",
                            "x-parser-schema-id": "<anonymous-schema-24>"
                          }
                        },
                        "x-parser-schema-id": "RoomSummaryRes"
                      }
                    },
                    "x-parser-schema-id": "<anonymous-schema-16>"
                  }
                },
                "x-parser-schema-id": "RedisResponse_RoomList_Create"
              }
            },
            {
              "name": "Room Update",
              "summary": "방 정보 수정됨",
              "payload": {
                "type": "object",
                "properties": {
                  "userId": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-25>"
                  },
                  "topic": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-26>"
                  },
                  "payload": {
                    "type": "object",
                    "properties": {
                      "eventType": {
                        "type": "string",
                        "enum": [
                          "UPDATE"
                        ],
                        "x-parser-schema-id": "<anonymous-schema-28>"
                      },
                      "eventAt": {
                        "type": "string",
                        "format": "date-time",
                        "x-parser-schema-id": "<anonymous-schema-29>"
                      },
                      "data": "$ref:$.channels./sub/lobby/list/event.subscribe.message.oneOf[0].payload.properties.payload.properties.data"
                    },
                    "x-parser-schema-id": "<anonymous-schema-27>"
                  }
                },
                "x-parser-schema-id": "RedisResponse_RoomList_Update"
              }
            },
            {
              "name": "Room Delete",
              "summary": "방 삭제됨",
              "payload": {
                "type": "object",
                "properties": {
                  "userId": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-30>"
                  },
                  "topic": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-31>"
                  },
                  "payload": {
                    "type": "object",
                    "properties": {
                      "eventType": {
                        "type": "string",
                        "enum": [
                          "DELETE"
                        ],
                        "x-parser-schema-id": "<anonymous-schema-33>"
                      },
                      "eventAt": {
                        "type": "string",
                        "format": "date-time",
                        "x-parser-schema-id": "<anonymous-schema-34>"
                      },
                      "data": "$ref:$.channels./sub/lobby/list/event.subscribe.message.oneOf[0].payload.properties.payload.properties.data"
                    },
                    "x-parser-schema-id": "<anonymous-schema-32>"
                  }
                },
                "x-parser-schema-id": "RedisResponse_RoomList_Delete"
              }
            }
          ]
        }
      }
    },
    "/pub/room/{roomId}": {
      "description": "클라이언트가 대기방 내에서 발생 가능한 로직 요청을 전송하는 채널입니다.\n\n[사용 가능한 이벤트 타입]\n- CHAT: 대기방 채팅\n- READY: 준비\n- UNREADY: 준비 해제\n- EXIT: 방 퇴장\n- UPDATE: 방 정보 수정\n- START: 게임 시작\n- OWNER_CHANGE: 방장 권한 위임\n- KICK: 플레이어 강퇴\n\n`eventType` 값에 따라 content의 의미는 다르게 해석됩니다.\n",
      "parameters": {
        "roomId": {
          "description": "대상 대기방의 고유 ID",
          "schema": {
            "type": "string",
            "x-parser-schema-id": "roomId"
          }
        }
      },
      "publish": {
        "message": {
          "name": "RoomEventRequest",
          "summary": "대기방 내 요청 이벤트 전송",
          "contentType": "application/json",
          "payload": {
            "type": "object",
            "required": [
              "eventType"
            ],
            "properties": {
              "eventType": {
                "type": "string",
                "enum": [
                  "CHAT",
                  "READY",
                  "JOIN",
                  "EXIT",
                  "UNREADY",
                  "START",
                  "OWNER_CHANGE",
                  "KICK"
                ],
                "description": "클라이언트가 수행하고자 하는 이벤트의 종류입니다.\n",
                "x-parser-schema-id": "<anonymous-schema-35>"
              },
              "content": {
                "type": "string",
                "description": "이벤트에 따라 의미가 달라지는 콘텐츠입니다.\n- CHAT: 채팅 메시지\n- JOIN: 비밀번호가 있는 대기방의 경우 비밀번호, 비밀번호가 없다면 안보내도 됩니다.\n- KICK: 강퇴하고자 하는 사용자의 UUID\n- UPDATE: 방의 정보(title, hasPassword, password, difficulty, roundCount)를 json문자열 형태로 보냅니다.\n- OWNER_CHANGE: 방장 권한을 위임하고자 하는 사용자의 UUID\n- 나머지는 content를 사용하지 않습니다.\n",
                "x-parser-schema-id": "<anonymous-schema-36>"
              }
            },
            "x-parser-schema-id": "RoomReq"
          }
        }
      }
    },
    "/sub/room/{roomId}": {
      "description": "클라이언트가 대기방 내에서 발생 가능한 로직 처리 결과를 응답 받는 채널입니다.\n",
      "parameters": {
        "roomId": {
          "description": "대기방 고유 ID",
          "schema": {
            "type": "string",
            "x-parser-schema-id": "roomId"
          }
        }
      },
      "subscribe": {
        "message": {
          "oneOf": [
            {
              "name": "ChatEvent",
              "summary": "CHAT 이벤트 - 채팅 메시지 수신",
              "payload": {
                "type": "object",
                "properties": {
                  "userId": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-37>"
                  },
                  "topic": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-38>"
                  },
                  "payload": {
                    "type": "object",
                    "properties": {
                      "eventType": {
                        "type": "string",
                        "enum": [
                          "CHAT"
                        ],
                        "x-parser-schema-id": "<anonymous-schema-40>"
                      },
                      "eventAt": {
                        "type": "string",
                        "format": "date-time",
                        "x-parser-schema-id": "<anonymous-schema-41>"
                      },
                      "data": {
                        "type": "object",
                        "properties": {
                          "nickname": {
                            "type": "string",
                            "x-parser-schema-id": "<anonymous-schema-42>"
                          },
                          "content": {
                            "type": "string",
                            "x-parser-schema-id": "<anonymous-schema-43>"
                          },
                          "chatType": {
                            "type": "string",
                            "enum": [
                              "ROOM"
                            ],
                            "x-parser-schema-id": "<anonymous-schema-44>"
                          },
                          "sentAt": {
                            "type": "string",
                            "format": "date-time",
                            "x-parser-schema-id": "<anonymous-schema-45>"
                          }
                        },
                        "x-parser-schema-id": "RoomChatMessage"
                      }
                    },
                    "x-parser-schema-id": "<anonymous-schema-39>"
                  }
                },
                "x-parser-schema-id": "RedisResponse_RoomChat"
              }
            },
            {
              "name": "ReadyEvent",
              "summary": "READY 이벤트 - 준비",
              "payload": {
                "type": "object",
                "properties": {
                  "userId": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-46>"
                  },
                  "topic": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-47>"
                  },
                  "payload": {
                    "type": "object",
                    "properties": {
                      "eventType": {
                        "type": "string",
                        "enum": [
                          "READY"
                        ],
                        "x-parser-schema-id": "<anonymous-schema-49>"
                      },
                      "eventAt": {
                        "type": "string",
                        "format": "date-time",
                        "x-parser-schema-id": "<anonymous-schema-50>"
                      },
                      "data": {
                        "type": "string",
                        "description": "레디를 한 사용자의 UUID",
                        "x-parser-schema-id": "<anonymous-schema-51>"
                      }
                    },
                    "x-parser-schema-id": "<anonymous-schema-48>"
                  }
                },
                "x-parser-schema-id": "RedisResponse_RoomReady"
              }
            },
            {
              "name": "UnReadyEvent",
              "summary": "UnREADY 이벤트 - 준비 해제",
              "payload": {
                "type": "object",
                "properties": {
                  "userId": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-52>"
                  },
                  "topic": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-53>"
                  },
                  "payload": {
                    "type": "object",
                    "properties": {
                      "eventType": {
                        "type": "string",
                        "enum": [
                          "UNREADY"
                        ],
                        "x-parser-schema-id": "<anonymous-schema-55>"
                      },
                      "eventAt": {
                        "type": "string",
                        "format": "date-time",
                        "x-parser-schema-id": "<anonymous-schema-56>"
                      },
                      "data": {
                        "type": "string",
                        "description": "레디 취소를 한 사용자의 UUID",
                        "x-parser-schema-id": "<anonymous-schema-57>"
                      }
                    },
                    "x-parser-schema-id": "<anonymous-schema-54>"
                  }
                },
                "x-parser-schema-id": "RedisResponse_RoomUnReady"
              }
            },
            {
              "name": "ExitEvent",
              "summary": "EXIT 이벤트 - 퇴장 정보",
              "payload": {
                "type": "object",
                "properties": {
                  "userId": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-58>"
                  },
                  "topic": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-59>"
                  },
                  "payload": {
                    "type": "object",
                    "properties": {
                      "eventType": {
                        "type": "string",
                        "enum": [
                          "EXIT"
                        ],
                        "x-parser-schema-id": "<anonymous-schema-61>"
                      },
                      "eventAt": {
                        "type": "string",
                        "format": "date-time",
                        "x-parser-schema-id": "<anonymous-schema-62>"
                      },
                      "data": {
                        "type": "string",
                        "description": "퇴장한 사용자의 UUID",
                        "x-parser-schema-id": "<anonymous-schema-63>"
                      }
                    },
                    "x-parser-schema-id": "<anonymous-schema-60>"
                  }
                },
                "x-parser-schema-id": "RedisResponse_RoomExit"
              }
            },
            {
              "name": "StartEvent",
              "summary": "START 이벤트 - 게임 시작 알림(게임 메타데이터 포함)",
              "payload": {
                "type": "object",
                "properties": {
                  "userId": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-64>"
                  },
                  "topic": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-65>"
                  },
                  "payload": {
                    "type": "object",
                    "properties": {
                      "eventType": {
                        "type": "string",
                        "enum": [
                          "START"
                        ],
                        "x-parser-schema-id": "<anonymous-schema-67>"
                      },
                      "eventAt": {
                        "type": "string",
                        "format": "date-time",
                        "x-parser-schema-id": "<anonymous-schema-68>"
                      },
                      "data": {
                        "type": "object",
                        "description": "게임 시작 시 게임 메타 데이터와 게임 시작 시 AI 메시지를 함께 보내주기 위한 구조",
                        "properties": {
                          "gameData": {
                            "type": "object",
                            "properties": {
                              "roomId": {
                                "type": "string",
                                "description": "게임 방의 고유 식별자",
                                "example": "9827",
                                "x-parser-schema-id": "<anonymous-schema-69>"
                              },
                              "gameType": {
                                "type": "string",
                                "description": "게임의 타입",
                                "enum": [
                                  "TRICK_MYOMYO",
                                  "LULU_ART_EXAM"
                                ],
                                "example": "TRICK_MYOMYO",
                                "x-parser-schema-id": "<anonymous-schema-70>"
                              },
                              "difficulty": {
                                "type": "string",
                                "description": "게임 난이도",
                                "enum": [
                                  "BASIC",
                                  "ADVANCED"
                                ],
                                "example": "BASIC",
                                "x-parser-schema-id": "<anonymous-schema-71>"
                              },
                              "gameStatus": {
                                "type": "string",
                                "description": "게임의 현재 상태",
                                "enum": [
                                  "GAME_STARTED",
                                  "ROUND_STARTED",
                                  "DRAWING_PHASE",
                                  "GUESSING_PHASE",
                                  "ROUND_ENDED",
                                  "GAME_ENDED"
                                ],
                                "x-parser-schema-id": "<anonymous-schema-72>"
                              },
                              "currentRound": {
                                "type": "integer",
                                "description": "현재 라운드 번호 (0이면 아직 아무 라운드 시작하지 않음, 1부터 시작)",
                                "example": 0,
                                "x-parser-schema-id": "<anonymous-schema-73>"
                              },
                              "totalRounds": {
                                "type": "integer",
                                "description": "전체 라운드 수",
                                "example": 3,
                                "x-parser-schema-id": "<anonymous-schema-74>"
                              },
                              "aiScore": {
                                "type": "integer",
                                "description": "AI의 현재 점수",
                                "example": 0,
                                "x-parser-schema-id": "<anonymous-schema-75>"
                              },
                              "scores": {
                                "type": "object",
                                "description": "유저별 점수, useruuid - score 형태의 Map",
                                "additionalProperties": {
                                  "type": "integer",
                                  "description": "획득한 점수",
                                  "x-parser-schema-id": "<anonymous-schema-76>"
                                },
                                "example": {
                                  "AI": 0,
                                  "z2E63KqK": 3,
                                  "l3lwXGrb": 6
                                },
                                "x-parser-schema-id": "Score"
                              },
                              "gamePlayers": {
                                "type": "array",
                                "description": "게임에 참여 중인 플레이어 목록",
                                "items": {
                                  "type": "object",
                                  "properties": {
                                    "playerUuid": {
                                      "type": "string",
                                      "description": "플레이어의 UUID",
                                      "example": "l3lwXGrb",
                                      "x-parser-schema-id": "<anonymous-schema-78>"
                                    },
                                    "nickname": {
                                      "type": "string",
                                      "description": "플레이어의 닉네임",
                                      "example": "test1",
                                      "x-parser-schema-id": "<anonymous-schema-79>"
                                    }
                                  },
                                  "x-parser-schema-id": "GamePlayer"
                                },
                                "x-parser-schema-id": "<anonymous-schema-77>"
                              },
                              "rounds": {
                                "type": "array",
                                "description": "게임의 라운드 정보 목록",
                                "items": {
                                  "type": "object",
                                  "properties": {
                                    "roundIndex": {
                                      "type": "integer",
                                      "description": "라운드 번호 (0부터 시작)",
                                      "example": 0,
                                      "x-parser-schema-id": "<anonymous-schema-81>"
                                    },
                                    "drawingEndTime": {
                                      "type": "string",
                                      "format": "date-time",
                                      "nullable": true,
                                      "description": "그리기 종료 시간 (ISO 8601)",
                                      "example": "null",
                                      "x-parser-schema-id": "<anonymous-schema-82>"
                                    },
                                    "roundWinner": {
                                      "type": "string",
                                      "nullable": true,
                                      "description": "라운드 승리자 (playerUuid 또는 'AI' 또는 'players')",
                                      "example": "null",
                                      "x-parser-schema-id": "<anonymous-schema-83>"
                                    },
                                    "currentWordIndex": {
                                      "type": "integer",
                                      "description": "현재 추측중인 Word 인덱스",
                                      "example": 0,
                                      "x-parser-schema-id": "<anonymous-schema-84>"
                                    },
                                    "words": {
                                      "type": "array",
                                      "description": "라운드에서 그릴/맞출 단어 목록",
                                      "items": {
                                        "type": "object",
                                        "properties": {
                                          "wordIndex": {
                                            "type": "integer",
                                            "description": "단어 인덱스",
                                            "example": 0,
                                            "x-parser-schema-id": "<anonymous-schema-86>"
                                          },
                                          "word": {
                                            "type": "string",
                                            "description": "제시어",
                                            "example": "bush",
                                            "x-parser-schema-id": "<anonymous-schema-87>"
                                          },
                                          "drawerUuid": {
                                            "type": "string",
                                            "description": "그림을 그린 플레이어의 UUID",
                                            "example": "l3lwXGrb",
                                            "x-parser-schema-id": "<anonymous-schema-88>"
                                          },
                                          "submitted": {
                                            "type": "boolean",
                                            "description": "해당 그림이 제출되었는지 여부",
                                            "example": false,
                                            "x-parser-schema-id": "<anonymous-schema-89>"
                                          },
                                          "imageURL": {
                                            "type": "string",
                                            "description": "해당 그림의 URL",
                                            "example": "https://gotchaai-image-bucket.s3.ap-northeast-2.amazonaws.com/l3lwXGrb/546258da-8e18-4792-8b35-5b8f8c92bcec.png",
                                            "x-parser-schema-id": "<anonymous-schema-90>"
                                          },
                                          "aiGuesses": {
                                            "type": "array",
                                            "description": "AI 플레이어의 추측 목록",
                                            "items": {
                                              "type": "object",
                                              "properties": {
                                                "guesserUuid": {
                                                  "type": "string",
                                                  "description": "추측을 시도한 플레이어의 UUID(AI일 경우 'AI')",
                                                  "example": "z2E63KqK",
                                                  "x-parser-schema-id": "<anonymous-schema-92>"
                                                },
                                                "guessWord": {
                                                  "type": "string",
                                                  "description": "플레이어의 추측 단어",
                                                  "example": "tree",
                                                  "x-parser-schema-id": "<anonymous-schema-93>"
                                                },
                                                "attempts": {
                                                  "type": "integer",
                                                  "description": "플레이어의 추측 시도 수",
                                                  "example": 1,
                                                  "x-parser-schema-id": "<anonymous-schema-94>"
                                                },
                                                "correct": {
                                                  "type": "boolean",
                                                  "description": "플레이어의 추측 정답 여부",
                                                  "example": true,
                                                  "x-parser-schema-id": "<anonymous-schema-95>"
                                                }
                                              },
                                              "x-parser-schema-id": "Guess"
                                            },
                                            "x-parser-schema-id": "<anonymous-schema-91>"
                                          },
                                          "playerGuesses": {
                                            "type": "array",
                                            "description": "사용자의 추측 목록",
                                            "items": "$ref:$.channels./sub/room/{roomId}.subscribe.message.oneOf[4].payload.properties.payload.properties.data.properties.gameData.properties.rounds.items.properties.words.items.properties.aiGuesses.items",
                                            "x-parser-schema-id": "<anonymous-schema-96>"
                                          },
                                          "aiPredictions": {
                                            "type": "array",
                                            "description": "AI 모델의 분류 결과(상위 3개)",
                                            "items": {
                                              "type": "object",
                                              "properties": {
                                                "predicted": {
                                                  "type": "string",
                                                  "description": "AI의 추측 단어",
                                                  "example": "apple",
                                                  "x-parser-schema-id": "<anonymous-schema-98>"
                                                },
                                                "confidence": {
                                                  "type": "number",
                                                  "format": "float",
                                                  "description": "AI 추측의 신뢰도 (0~1)",
                                                  "example": 0.85,
                                                  "x-parser-schema-id": "<anonymous-schema-99>"
                                                }
                                              },
                                              "x-parser-schema-id": "AIPrediction"
                                            },
                                            "x-parser-schema-id": "<anonymous-schema-97>"
                                          }
                                        },
                                        "x-parser-schema-id": "Word"
                                      },
                                      "x-parser-schema-id": "<anonymous-schema-85>"
                                    }
                                  },
                                  "x-parser-schema-id": "Round"
                                },
                                "x-parser-schema-id": "<anonymous-schema-80>"
                              },
                              "winner": {
                                "type": "string",
                                "description": "게임 최종 우승자 (playerUuid 또는 'AI' 또는 'players')",
                                "nullable": true,
                                "example": "AI",
                                "x-parser-schema-id": "<anonymous-schema-100>"
                              }
                            },
                            "x-parser-schema-id": "Game"
                          },
                          "aiSays": {
                            "type": "string",
                            "description": "게임 시작 시 AI의 메시지",
                            "x-parser-schema-id": "<anonymous-schema-101>"
                          }
                        },
                        "x-parser-schema-id": "AISaysRes"
                      }
                    },
                    "x-parser-schema-id": "<anonymous-schema-66>"
                  }
                },
                "x-parser-schema-id": "RedisResponse_GameStart"
              }
            },
            {
              "name": "Room Update",
              "summary": "UPDATE 이벤트 - 방정보 및 유저 리스트 반환",
              "payload": "$ref:$.channels./sub/lobby/list/event.subscribe.message.oneOf[1].payload"
            }
          ]
        }
      }
    },
    "/pub/chat/all": {
      "description": "클라이언트가 전체 채팅 메시지를 전송하는 채널",
      "publish": {
        "message": {
          "payload": {
            "type": "object",
            "description": "채팅 메시지 전송 요청 객체입니다.\n",
            "properties": {
              "content": {
                "type": "string",
                "description": "메시지 내용",
                "x-parser-schema-id": "<anonymous-schema-102>"
              }
            },
            "required": [
              "content"
            ],
            "x-parser-schema-id": "RedisReq_AllChat"
          },
          "x-parser-message-name": "<anonymous-message-5>"
        }
      }
    },
    "/sub/chat/all": {
      "description": "클라이언트가 전체 채팅 메시지를 수신하는 채널",
      "subscribe": {
        "message": {
          "name": "AllChatMessage",
          "summary": "전체 채팅 메시지 수신",
          "payload": {
            "type": "object",
            "description": "- 전체 채팅 메시지를 포함하는 Redis 전송 구조\n- 응답 시 ChatType은 ALL, ROOM, PRIVATE중 하나로 전송됩니다.\n",
            "properties": {
              "userId": {
                "type": "string",
                "nullable": true,
                "description": "null (전체 채팅이므로 사용자 구분 없음)",
                "x-parser-schema-id": "<anonymous-schema-103>"
              },
              "topic": {
                "type": "string",
                "description": "Redis로 발행된 전체 채팅 채널명",
                "x-parser-schema-id": "<anonymous-schema-104>"
              },
              "payload": {
                "type": "object",
                "properties": {
                  "nickname": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-105>"
                  },
                  "content": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-106>"
                  },
                  "chatType": {
                    "type": "string",
                    "enum": [
                      "ALL"
                    ],
                    "x-parser-schema-id": "<anonymous-schema-107>"
                  },
                  "sentAt": {
                    "type": "string",
                    "format": "date-time",
                    "x-parser-schema-id": "<anonymous-schema-108>"
                  }
                },
                "x-parser-schema-id": "AllChatMessage"
              }
            },
            "x-parser-schema-id": "RedisRes_AllChat"
          }
        }
      }
    },
    "/pub/chat/private": {
      "description": "클라이언트가 특정 사용자에게 개인 채팅(귓속말) 메시지를 전송하는 채널입니다. `receiverUuid`에 수신 대상 사용자의 UUID를 지정하여 메시지를 전송합니다",
      "publish": {
        "message": {
          "payload": {
            "type": "object",
            "description": "채팅 메시지 전송 요청 객체입니다.\n",
            "properties": {
              "receiverUuid": {
                "type": "string",
                "nullable": true,
                "description": "- 개인 채팅 시, `receiverUuid'는 필수입니다.\n",
                "x-parser-schema-id": "<anonymous-schema-109>"
              },
              "content": {
                "type": "string",
                "description": "메시지 내용",
                "x-parser-schema-id": "<anonymous-schema-110>"
              }
            },
            "required": [
              "content"
            ],
            "x-parser-schema-id": "RedisReq_PrivateChat"
          },
          "x-parser-message-name": "<anonymous-message-6>"
        }
      }
    },
    "/sub/chat/private/{receiverId}": {
      "description": "개인 채팅 메시지를 수신하는 구독 채널입니다.",
      "parameters": {
        "receiverId": {
          "description": "ID of the message receiver",
          "schema": {
            "type": "string",
            "x-parser-schema-id": "receiverId"
          }
        }
      },
      "subscribe": {
        "message": {
          "name": "PrivateMessage",
          "summary": "개인 채팅 메시지 수신",
          "payload": {
            "type": "object",
            "description": "특정 사용자에게 전송되는 개인 채팅 메시지 응답 구조입니다.",
            "properties": {
              "userId": {
                "type": "string",
                "description": "수신 대상 사용자의 UUID입니다.",
                "x-parser-schema-id": "<anonymous-schema-111>"
              },
              "topic": {
                "type": "string",
                "description": "Redis로 발행된 개인 채팅 채널명 (`/sub/chat/private/{receiverUuid}`)",
                "x-parser-schema-id": "<anonymous-schema-112>"
              },
              "payload": {
                "type": "object",
                "properties": {
                  "nickname": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-113>"
                  },
                  "content": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-114>"
                  },
                  "chatType": {
                    "type": "string",
                    "enum": [
                      "PRIVATE"
                    ],
                    "x-parser-schema-id": "<anonymous-schema-115>"
                  },
                  "sentAt": {
                    "type": "string",
                    "format": "date-time",
                    "x-parser-schema-id": "<anonymous-schema-116>"
                  }
                },
                "x-parser-schema-id": "PrivateChatMessage"
              }
            },
            "x-parser-schema-id": "RedisRes_PrivateChat"
          }
        }
      }
    },
    "/pub/game/{roomId}": {
      "description": "클라이언트가 게임 내 이벤트를 전송하는 채널입니다. `eventType` 필드에 따라 서버가 게임 로직을 분기 처리합니다.",
      "parameters": {
        "roomId": {
          "description": "게임이 진행 중인 방의 고유 식별자",
          "schema": {
            "type": "string",
            "x-parser-schema-id": "roomId"
          }
        }
      },
      "publish": {
        "message": {
          "oneOf": [
            {
              "name": "DrawingSubmitEvent",
              "summary": "DRAWING_SUBMIT - 그림 URL 전송",
              "payload": {
                "type": "object",
                "description": "그림 제출 요청",
                "properties": {
                  "imageURL": {
                    "type": "string",
                    "format": "uri",
                    "description": "업로드된 그림의 URL",
                    "x-parser-schema-id": "<anonymous-schema-117>"
                  }
                },
                "required": [
                  "imageURL"
                ],
                "x-parser-schema-id": "DrawingSubmitReq"
              }
            },
            {
              "name": "GuessSubmitEvent",
              "summary": "GUESS_SUBMIT - 플레이어의 정답 시도 제출",
              "payload": "$ref:$.channels./sub/room/{roomId}.subscribe.message.oneOf[4].payload.properties.payload.properties.data.properties.gameData.properties.rounds.items.properties.words.items.properties.aiGuesses.items"
            }
          ]
        }
      }
    },
    "/sub/game/{roomId}": {
      "description": "게임 흐름 관련 메시지를 수신하는 구독 채널입니다.\n\n[ 발생 가능한 메시지 타입 ]\n\n- `ROUND_START`: 라운드의 시작을 알립니다. \n  - `payload`의 `data` 필드에는 해당 라운드의 메타정보(사용자 별 그려야 하는 제시어)를 알리기 위해 `Round` 타입이 전송됩니다.\n- `GUESS_START`: 추측 단계의 시작을 알립니다. \n  - `payload`의 `data` 필드에는 추측을 위한 정보들인 제시어 list가 전송됩니다. \n  - 클라이언트에서는 `drawerUuid` 필드와 `imageURL` 필드를 확인해 그림을 그린 플레이어와 그림을 맞출 플레이어를 구분할 수 있습니다.\n- `GUESS_REQUEST`: 추측 요청 브로드캐스트입니다. \n  - `payload`의 `data` 필드에는 추측을 위한 메타정보인 `guess` 데이터가 전송됩니다. \n  - `guesserUuid` 필드가 AI인 경우 잠시 후 AI 추측결과가 `GUESS_SUBMIT`으로 함께 브로드캐스트 됩니다. \n  - `guesserUuid` 필드가 AI가 아닌 경우 클라이언트에서 해당 `guesserUuid`의 `GUESS_SUBMIT`을 기다리게 됩니다.  \n  - 또한 `endTime` 필드가 이벤트 발행 시점의 30초 후로 설정되며, 클라이언트에서는 이 시간 안에 `GUESS_SUBMIT`을 보내주어야 합니다. \n- `GUESS_SUBMIT`:  추측 데이터 제출 브로드캐스트입니다. \n  - 해당 턴의 추측 데이터 제출 시 이를 브로드캐스트 합니다. \n  - `payload`의 `data` 필드에는 `Guess` 데이터가 전송됩니다. \n- `GUESS_RESULT`: 추측 결과를 클라이언트에 브로드캐스트합니다.\n  - `payload`의 `data` 필드에는 `guess` 데이터가 정답 여부와 함께 전송됩니다.\n- `SCORE_UPDATE`: 추측 성공 시 점수 현황을 업데이트 해주고, 이를 브로드캐스트 해줍니다. \n  - 추측 실패 시에는 전송되지 않습니다. \n  - `payload`의 `data` 필드에는 `Score` 데이터가 전송됩니다. \n- `ROUND_END`: 해당 라운드의 종료를 알립니다.\n  - 라운드 종료 시 해당 라운드의 결과를 포함한 라운드 데이터를 브로드캐스트 해줍니다. \n  - `payload`의 `data` 필드에는 `round` 데이터가 전송됩니다. \n- `GAME_END`: 게임 종료 시 해당 게임의 결과를 포함한 게임 데이터를 브로드캐스트 해줍니다. \n  - `payload`의 `data` 필드에는 `game` 데이터가 전송됩니다. \n",
      "parameters": {
        "roomId": {
          "description": "현재 게임이 진행 중인 방의 ID",
          "schema": {
            "type": "string",
            "x-parser-schema-id": "roomId"
          }
        }
      },
      "subscribe": {
        "message": {
          "oneOf": [
            {
              "name": "ROUND_START",
              "payload": {
                "type": "object",
                "properties": {
                  "userId": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-118>"
                  },
                  "topic": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-119>"
                  },
                  "payload": {
                    "type": "object",
                    "properties": {
                      "eventType": {
                        "type": "string",
                        "enum": [
                          "ROUND_START"
                        ],
                        "x-parser-schema-id": "<anonymous-schema-121>"
                      },
                      "aiSays": {
                        "type": "string",
                        "description": "게임 진행 중 AI에게서 오는 메시지입니다. AI에서 메시지가 오지 않는 이벤트 경우 \"null\" 값으로 오게 됩니다.",
                        "example": "\"으.. 잠깐 오류가 났네. 다시 해볼게!\"",
                        "x-parser-schema-id": "<anonymous-schema-122>"
                      },
                      "data": {
                        "type": "object",
                        "properties": {
                          "roundIndex": {
                            "type": "integer",
                            "x-parser-schema-id": "<anonymous-schema-124>"
                          },
                          "drawingEndTime": {
                            "type": "string",
                            "x-parser-schema-id": "<anonymous-schema-125>"
                          },
                          "currentWordIndex": {
                            "type": "integer",
                            "x-parser-schema-id": "<anonymous-schema-126>"
                          },
                          "roundWinner": {
                            "type": "string",
                            "nullable": true,
                            "description": "해당 라운드의 Winner를 나타내는 필드입니다. ROUND_START 이벤트 발생 시에는 null로 반환됩니다.",
                            "x-parser-schema-id": "<anonymous-schema-127>"
                          }
                        },
                        "example": {
                          "roundIndex": 1,
                          "drawingEndTime": "2025-05-28T21:01:24.4270926",
                          "currentWordIndex": 0,
                          "roundWinner": null
                        },
                        "x-parser-schema-id": "<anonymous-schema-123>"
                      },
                      "endTime": {
                        "type": "string",
                        "nullable": true,
                        "example": null,
                        "x-parser-schema-id": "<anonymous-schema-128>"
                      },
                      "eventAt": {
                        "type": "string",
                        "example": "2025-05-28T21:00:56.30379",
                        "x-parser-schema-id": "<anonymous-schema-129>"
                      }
                    },
                    "x-parser-schema-id": "<anonymous-schema-120>"
                  }
                },
                "x-parser-schema-id": "RedisResponse_RoundStart"
              }
            },
            {
              "name": "GUESS_START",
              "payload": {
                "type": "object",
                "properties": {
                  "userId": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-130>"
                  },
                  "topic": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-131>"
                  },
                  "payload": {
                    "type": "object",
                    "properties": {
                      "eventType": {
                        "type": "string",
                        "enum": [
                          "GUESS_START"
                        ],
                        "x-parser-schema-id": "<anonymous-schema-133>"
                      },
                      "data": {
                        "type": "array",
                        "items": {
                          "type": "object",
                          "properties": {
                            "wordIndex": {
                              "type": "integer",
                              "x-parser-schema-id": "<anonymous-schema-136>"
                            },
                            "word": {
                              "type": "string",
                              "x-parser-schema-id": "<anonymous-schema-137>"
                            },
                            "drawerUuid": {
                              "type": "string",
                              "x-parser-schema-id": "<anonymous-schema-138>"
                            },
                            "imageURL": {
                              "type": "string",
                              "x-parser-schema-id": "<anonymous-schema-139>"
                            },
                            "submitted": {
                              "type": "boolean",
                              "x-parser-schema-id": "<anonymous-schema-140>"
                            }
                          },
                          "x-parser-schema-id": "<anonymous-schema-135>"
                        },
                        "example": [
                          {
                            "wordIndex": 0,
                            "word": "syringe",
                            "drawerUuid": "l3lwXGrb",
                            "imageURL": "l3lwXGrb/9228e506-059c-4a0f-9874-65d68aff372b.png",
                            "submitted": true
                          },
                          {
                            "wordIndex": 1,
                            "word": "cooler",
                            "drawerUuid": "z2E63KqK",
                            "imageURL": "z2E63KqK/39caa311-8195-4f39-94d1-84ae4710b474.png",
                            "submitted": true
                          }
                        ],
                        "x-parser-schema-id": "<anonymous-schema-134>"
                      },
                      "eventAt": {
                        "type": "string",
                        "example": "2025-05-27T20:28:36.4511966",
                        "x-parser-schema-id": "<anonymous-schema-141>"
                      }
                    },
                    "x-parser-schema-id": "<anonymous-schema-132>"
                  }
                },
                "x-parser-schema-id": "RedisResponse_GuessStart"
              }
            },
            {
              "name": "GUESS_REQUEST",
              "payload": {
                "type": "object",
                "properties": {
                  "userId": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-142>"
                  },
                  "topic": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-143>"
                  },
                  "payload": {
                    "type": "object",
                    "properties": {
                      "eventType": {
                        "type": "string",
                        "enum": [
                          "GUESS_REQUEST"
                        ],
                        "x-parser-schema-id": "<anonymous-schema-145>"
                      },
                      "data": {
                        "type": "object",
                        "properties": {
                          "guesserUuid": {
                            "type": "string",
                            "x-parser-schema-id": "<anonymous-schema-147>"
                          },
                          "guessWord": {
                            "type": "string",
                            "nullable": true,
                            "x-parser-schema-id": "<anonymous-schema-148>"
                          },
                          "attempts": {
                            "type": "integer",
                            "x-parser-schema-id": "<anonymous-schema-149>"
                          },
                          "correct": {
                            "type": "boolean",
                            "nullable": true,
                            "x-parser-schema-id": "<anonymous-schema-150>"
                          }
                        },
                        "example": {
                          "guesserUuid": "AI",
                          "guessWord": null,
                          "attempts": 1,
                          "correct": null
                        },
                        "x-parser-schema-id": "<anonymous-schema-146>"
                      },
                      "aiSays": {
                        "type": "string",
                        "nullable": true,
                        "example": null,
                        "x-parser-schema-id": "<anonymous-schema-151>"
                      },
                      "endTime": {
                        "type": "string",
                        "nullable": true,
                        "example": null,
                        "x-parser-schema-id": "<anonymous-schema-152>"
                      },
                      "eventAt": {
                        "type": "string",
                        "example": "2025-05-31T22:17:36.8926635",
                        "x-parser-schema-id": "<anonymous-schema-153>"
                      }
                    },
                    "x-parser-schema-id": "<anonymous-schema-144>"
                  }
                },
                "x-parser-schema-id": "RedisResponse_GuessRequest"
              }
            },
            {
              "name": "GUESS_SUBMIT",
              "payload": {
                "type": "object",
                "properties": {
                  "userId": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-154>"
                  },
                  "topic": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-155>"
                  },
                  "payload": {
                    "type": "object",
                    "properties": {
                      "eventType": {
                        "type": "string",
                        "enum": [
                          "GUESS_SUBMIT"
                        ],
                        "x-parser-schema-id": "<anonymous-schema-157>"
                      },
                      "data": {
                        "type": "object",
                        "properties": {
                          "guesserUuid": {
                            "type": "string",
                            "x-parser-schema-id": "<anonymous-schema-159>"
                          },
                          "guessWord": {
                            "type": "string",
                            "x-parser-schema-id": "<anonymous-schema-160>"
                          },
                          "attempts": {
                            "type": "integer",
                            "x-parser-schema-id": "<anonymous-schema-161>"
                          },
                          "correct": {
                            "type": "boolean",
                            "nullable": true,
                            "x-parser-schema-id": "<anonymous-schema-162>"
                          }
                        },
                        "example": {
                          "guesserUuid": "AI",
                          "guessWord": "book",
                          "attempts": 1,
                          "correct": null
                        },
                        "x-parser-schema-id": "<anonymous-schema-158>"
                      },
                      "aiSays": {
                        "type": "string",
                        "example": "으.. 잠깐 오류가 났네. 다시 해볼게!",
                        "x-parser-schema-id": "<anonymous-schema-163>"
                      },
                      "endTime": {
                        "type": "string",
                        "nullable": true,
                        "example": null,
                        "x-parser-schema-id": "<anonymous-schema-164>"
                      },
                      "eventAt": {
                        "type": "string",
                        "example": "2025-05-28T21:02:18.3712128",
                        "x-parser-schema-id": "<anonymous-schema-165>"
                      }
                    },
                    "x-parser-schema-id": "<anonymous-schema-156>"
                  }
                },
                "x-parser-schema-id": "RedisResponse_GuessSubmit"
              }
            },
            {
              "name": "GUESS_RESULT",
              "payload": {
                "type": "object",
                "properties": {
                  "userId": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-166>"
                  },
                  "topic": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-167>"
                  },
                  "payload": {
                    "type": "object",
                    "properties": {
                      "eventType": {
                        "type": "string",
                        "enum": [
                          "GUESS_RESULT"
                        ],
                        "x-parser-schema-id": "<anonymous-schema-169>"
                      },
                      "data": {
                        "type": "object",
                        "properties": {
                          "guesserUuid": {
                            "type": "string",
                            "x-parser-schema-id": "<anonymous-schema-171>"
                          },
                          "guessWord": {
                            "type": "string",
                            "x-parser-schema-id": "<anonymous-schema-172>"
                          },
                          "attempts": {
                            "type": "integer",
                            "x-parser-schema-id": "<anonymous-schema-173>"
                          },
                          "correct": {
                            "type": "boolean",
                            "x-parser-schema-id": "<anonymous-schema-174>"
                          }
                        },
                        "example": {
                          "guesserUuid": "AI",
                          "guessWord": "book",
                          "attempts": 1,
                          "correct": false
                        },
                        "x-parser-schema-id": "<anonymous-schema-170>"
                      },
                      "aiSays": {
                        "type": "string",
                        "example": "으.. 잠깐 오류가 났네. 다시 해볼게!",
                        "x-parser-schema-id": "<anonymous-schema-175>"
                      },
                      "endTime": {
                        "type": "string",
                        "nullable": true,
                        "example": null,
                        "x-parser-schema-id": "<anonymous-schema-176>"
                      },
                      "eventAt": {
                        "type": "string",
                        "example": "2025-05-28T21:02:20.4475533",
                        "x-parser-schema-id": "<anonymous-schema-177>"
                      }
                    },
                    "x-parser-schema-id": "<anonymous-schema-168>"
                  }
                },
                "x-parser-schema-id": "RedisResponse_GuessResult"
              }
            },
            {
              "name": "SCORE_UPDATE",
              "payload": {
                "type": "object",
                "properties": {
                  "userId": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-178>"
                  },
                  "topic": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-179>"
                  },
                  "payload": {
                    "type": "object",
                    "properties": {
                      "eventType": {
                        "type": "string",
                        "enum": [
                          "SCORE_UPDATE"
                        ],
                        "x-parser-schema-id": "<anonymous-schema-181>"
                      },
                      "data": {
                        "type": "object",
                        "additionalProperties": {
                          "type": "integer",
                          "x-parser-schema-id": "<anonymous-schema-183>"
                        },
                        "example": {
                          "AI": 0,
                          "z2E63KqK": 3,
                          "l3lwXGrb": 3
                        },
                        "x-parser-schema-id": "<anonymous-schema-182>"
                      },
                      "aiSays": {
                        "type": "string",
                        "nullable": true,
                        "example": null,
                        "x-parser-schema-id": "<anonymous-schema-184>"
                      },
                      "endTime": {
                        "type": "string",
                        "nullable": true,
                        "example": null,
                        "x-parser-schema-id": "<anonymous-schema-185>"
                      },
                      "eventAt": {
                        "type": "string",
                        "example": "2025-05-28T21:02:39.2287028",
                        "x-parser-schema-id": "<anonymous-schema-186>"
                      }
                    },
                    "x-parser-schema-id": "<anonymous-schema-180>"
                  }
                },
                "x-parser-schema-id": "RedisResponse_ScoreUpdate"
              }
            },
            {
              "name": "ROUND_END",
              "payload": {
                "type": "object",
                "properties": {
                  "userId": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-187>"
                  },
                  "topic": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-188>"
                  },
                  "payload": {
                    "type": "object",
                    "properties": {
                      "eventType": {
                        "type": "string",
                        "enum": [
                          "ROUND_END"
                        ],
                        "x-parser-schema-id": "<anonymous-schema-190>"
                      },
                      "data": {
                        "type": "object",
                        "properties": {
                          "roundIndex": {
                            "type": "integer",
                            "x-parser-schema-id": "<anonymous-schema-192>"
                          },
                          "drawingEndTime": {
                            "type": "string",
                            "x-parser-schema-id": "<anonymous-schema-193>"
                          },
                          "roundWinner": {
                            "type": "string",
                            "x-parser-schema-id": "<anonymous-schema-194>"
                          },
                          "currentWordIndex": {
                            "type": "integer",
                            "x-parser-schema-id": "<anonymous-schema-195>"
                          },
                          "words": {
                            "type": "array",
                            "items": {
                              "type": "object",
                              "properties": {
                                "wordIndex": {
                                  "type": "integer",
                                  "x-parser-schema-id": "<anonymous-schema-198>"
                                },
                                "word": {
                                  "type": "string",
                                  "x-parser-schema-id": "<anonymous-schema-199>"
                                },
                                "drawerUuid": {
                                  "type": "string",
                                  "x-parser-schema-id": "<anonymous-schema-200>"
                                },
                                "submitted": {
                                  "type": "boolean",
                                  "x-parser-schema-id": "<anonymous-schema-201>"
                                },
                                "imageURL": {
                                  "type": "string",
                                  "x-parser-schema-id": "<anonymous-schema-202>"
                                },
                                "aiGuesses": {
                                  "type": "array",
                                  "items": {
                                    "type": "object",
                                    "properties": {
                                      "guesserUuid": {
                                        "type": "string",
                                        "x-parser-schema-id": "<anonymous-schema-205>"
                                      },
                                      "guessWord": {
                                        "type": "string",
                                        "x-parser-schema-id": "<anonymous-schema-206>"
                                      },
                                      "attempts": {
                                        "type": "integer",
                                        "x-parser-schema-id": "<anonymous-schema-207>"
                                      },
                                      "correct": {
                                        "type": "boolean",
                                        "x-parser-schema-id": "<anonymous-schema-208>"
                                      }
                                    },
                                    "x-parser-schema-id": "<anonymous-schema-204>"
                                  },
                                  "x-parser-schema-id": "<anonymous-schema-203>"
                                },
                                "playerGuesses": {
                                  "type": "array",
                                  "items": {
                                    "type": "object",
                                    "properties": {
                                      "guesserUuid": {
                                        "type": "string",
                                        "x-parser-schema-id": "<anonymous-schema-211>"
                                      },
                                      "guessWord": {
                                        "type": "string",
                                        "x-parser-schema-id": "<anonymous-schema-212>"
                                      },
                                      "attempts": {
                                        "type": "integer",
                                        "x-parser-schema-id": "<anonymous-schema-213>"
                                      },
                                      "correct": {
                                        "type": "boolean",
                                        "x-parser-schema-id": "<anonymous-schema-214>"
                                      }
                                    },
                                    "x-parser-schema-id": "<anonymous-schema-210>"
                                  },
                                  "x-parser-schema-id": "<anonymous-schema-209>"
                                },
                                "aiPredictions": {
                                  "type": "array",
                                  "items": {
                                    "type": "object",
                                    "properties": {
                                      "predicted": {
                                        "type": "string",
                                        "x-parser-schema-id": "<anonymous-schema-217>"
                                      },
                                      "confidence": {
                                        "type": "number",
                                        "format": "float",
                                        "x-parser-schema-id": "<anonymous-schema-218>"
                                      }
                                    },
                                    "x-parser-schema-id": "<anonymous-schema-216>"
                                  },
                                  "x-parser-schema-id": "<anonymous-schema-215>"
                                }
                              },
                              "x-parser-schema-id": "<anonymous-schema-197>"
                            },
                            "x-parser-schema-id": "<anonymous-schema-196>"
                          },
                          "scores": {
                            "type": "object",
                            "additionalProperties": {
                              "type": "integer",
                              "x-parser-schema-id": "<anonymous-schema-220>"
                            },
                            "x-parser-schema-id": "<anonymous-schema-219>"
                          }
                        },
                        "example": {
                          "roundIndex": 2,
                          "drawingEndTime": "2025-05-28T21:02:38.3805662",
                          "roundWinner": "PLAYER",
                          "currentWordIndex": 2,
                          "words": [
                            {
                              "wordIndex": 0,
                              "word": "drums",
                              "drawerUuid": "l3lwXGrb",
                              "submitted": true,
                              "imageURL": "https://gotchaai-image-bucket.s3.ap-northeast-2.amazonaws.com/l3lwXGrb/546258da-8e18-4792-8b35-5b8f8c92bcec.png",
                              "aiGuesses": [
                                {
                                  "guesserUuid": "AI",
                                  "guessWord": "book",
                                  "attempts": 1,
                                  "correct": false
                                }
                              ],
                              "playerGuesses": [
                                {
                                  "guesserUuid": "z2E63KqK",
                                  "guessWord": "drums",
                                  "attempts": 1,
                                  "correct": true
                                }
                              ],
                              "aiPredictions": [
                                {
                                  "predicted": "book",
                                  "confidence": 75.47914385795593
                                },
                                {
                                  "predicted": "calendar",
                                  "confidence": 11.874808371067047
                                },
                                {
                                  "predicted": "cat",
                                  "confidence": 8.319798856973648
                                }
                              ]
                            },
                            {
                              "wordIndex": 1,
                              "word": "sword",
                              "drawerUuid": "z2E63KqK",
                              "submitted": true,
                              "imageURL": "https://gotchaai-image-bucket.s3.ap-northeast-2.amazonaws.com/l3lwXGrb/546258da-8e18-4792-8b35-5b8f8c92bcec.png",
                              "aiGuesses": [
                                {
                                  "guesserUuid": "AI",
                                  "guessWord": "cat",
                                  "attempts": 1,
                                  "correct": false
                                }
                              ],
                              "playerGuesses": [
                                {
                                  "guesserUuid": "l3lwXGrb",
                                  "guessWord": "sword",
                                  "attempts": 1,
                                  "correct": true
                                }
                              ],
                              "aiPredictions": [
                                {
                                  "predicted": "cat",
                                  "confidence": 91.84954762458801
                                },
                                {
                                  "predicted": "calendar",
                                  "confidence": 6.590797007083893
                                },
                                {
                                  "predicted": "book",
                                  "confidence": 0.9185523726046085
                                }
                              ]
                            }
                          ],
                          "scores": {
                            "z2E63KqK": 3,
                            "l3lwXGrb": 3
                          }
                        },
                        "x-parser-schema-id": "<anonymous-schema-191>"
                      },
                      "aiSays": {
                        "type": "string",
                        "example": "으.. 잠깐 오류가 났네. 다시 해볼게!",
                        "x-parser-schema-id": "<anonymous-schema-221>"
                      },
                      "endTime": {
                        "type": "string",
                        "nullable": true,
                        "example": null,
                        "x-parser-schema-id": "<anonymous-schema-222>"
                      },
                      "eventAt": {
                        "type": "string",
                        "example": "2025-05-28T21:02:41.1232855",
                        "x-parser-schema-id": "<anonymous-schema-223>"
                      }
                    },
                    "x-parser-schema-id": "<anonymous-schema-189>"
                  }
                },
                "x-parser-schema-id": "RedisResponse_RoundEnd"
              }
            },
            {
              "name": "GAME_END",
              "payload": {
                "type": "object",
                "properties": {
                  "userId": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-224>"
                  },
                  "topic": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-225>"
                  },
                  "payload": {
                    "type": "object",
                    "properties": {
                      "eventType": {
                        "type": "string",
                        "enum": [
                          "GAME_END"
                        ],
                        "x-parser-schema-id": "<anonymous-schema-227>"
                      },
                      "data": {
                        "type": "object",
                        "properties": {
                          "roomId": {
                            "type": "string",
                            "x-parser-schema-id": "<anonymous-schema-229>"
                          },
                          "gameType": {
                            "type": "string",
                            "x-parser-schema-id": "<anonymous-schema-230>"
                          },
                          "difficulty": {
                            "type": "string",
                            "x-parser-schema-id": "<anonymous-schema-231>"
                          },
                          "gameStatus": {
                            "type": "string",
                            "nullable": true,
                            "x-parser-schema-id": "<anonymous-schema-232>"
                          },
                          "currentRound": {
                            "type": "integer",
                            "x-parser-schema-id": "<anonymous-schema-233>"
                          },
                          "totalRounds": {
                            "type": "integer",
                            "x-parser-schema-id": "<anonymous-schema-234>"
                          },
                          "aiScore": {
                            "type": "integer",
                            "x-parser-schema-id": "<anonymous-schema-235>"
                          },
                          "scores": {
                            "type": "object",
                            "additionalProperties": {
                              "type": "integer",
                              "x-parser-schema-id": "<anonymous-schema-237>"
                            },
                            "x-parser-schema-id": "<anonymous-schema-236>"
                          },
                          "gamePlayers": {
                            "type": "array",
                            "nullable": true,
                            "items": {
                              "type": "object",
                              "x-parser-schema-id": "<anonymous-schema-239>"
                            },
                            "x-parser-schema-id": "<anonymous-schema-238>"
                          },
                          "rounds": {
                            "type": "array",
                            "items": {
                              "type": "object",
                              "properties": {
                                "roundIndex": {
                                  "type": "integer",
                                  "x-parser-schema-id": "<anonymous-schema-242>"
                                },
                                "drawingEndTime": {
                                  "type": "string",
                                  "x-parser-schema-id": "<anonymous-schema-243>"
                                },
                                "roundWinner": {
                                  "type": "string",
                                  "nullable": true,
                                  "x-parser-schema-id": "<anonymous-schema-244>"
                                },
                                "currentWordIndex": {
                                  "type": "integer",
                                  "x-parser-schema-id": "<anonymous-schema-245>"
                                },
                                "words": {
                                  "type": "array",
                                  "items": {
                                    "type": "object",
                                    "properties": {
                                      "wordIndex": {
                                        "type": "integer",
                                        "x-parser-schema-id": "<anonymous-schema-248>"
                                      },
                                      "word": {
                                        "type": "string",
                                        "x-parser-schema-id": "<anonymous-schema-249>"
                                      },
                                      "drawerUuid": {
                                        "type": "string",
                                        "x-parser-schema-id": "<anonymous-schema-250>"
                                      },
                                      "submitted": {
                                        "type": "boolean",
                                        "x-parser-schema-id": "<anonymous-schema-251>"
                                      },
                                      "imageURL": {
                                        "type": "string",
                                        "x-parser-schema-id": "<anonymous-schema-252>"
                                      },
                                      "aiGuesses": {
                                        "type": "array",
                                        "items": {
                                          "type": "object",
                                          "properties": {
                                            "guesserUuid": {
                                              "type": "string",
                                              "x-parser-schema-id": "<anonymous-schema-255>"
                                            },
                                            "guessWord": {
                                              "type": "string",
                                              "x-parser-schema-id": "<anonymous-schema-256>"
                                            },
                                            "attempts": {
                                              "type": "integer",
                                              "x-parser-schema-id": "<anonymous-schema-257>"
                                            },
                                            "correct": {
                                              "type": "boolean",
                                              "x-parser-schema-id": "<anonymous-schema-258>"
                                            }
                                          },
                                          "x-parser-schema-id": "<anonymous-schema-254>"
                                        },
                                        "x-parser-schema-id": "<anonymous-schema-253>"
                                      },
                                      "playerGuesses": {
                                        "type": "array",
                                        "items": {
                                          "type": "object",
                                          "properties": {
                                            "guesserUuid": {
                                              "type": "string",
                                              "x-parser-schema-id": "<anonymous-schema-261>"
                                            },
                                            "guessWord": {
                                              "type": "string",
                                              "x-parser-schema-id": "<anonymous-schema-262>"
                                            },
                                            "attempts": {
                                              "type": "integer",
                                              "x-parser-schema-id": "<anonymous-schema-263>"
                                            },
                                            "correct": {
                                              "type": "boolean",
                                              "x-parser-schema-id": "<anonymous-schema-264>"
                                            }
                                          },
                                          "x-parser-schema-id": "<anonymous-schema-260>"
                                        },
                                        "x-parser-schema-id": "<anonymous-schema-259>"
                                      },
                                      "aiPredictions": {
                                        "type": "array",
                                        "items": {
                                          "type": "object",
                                          "properties": {
                                            "predicted": {
                                              "type": "string",
                                              "x-parser-schema-id": "<anonymous-schema-267>"
                                            },
                                            "confidence": {
                                              "type": "number",
                                              "format": "float",
                                              "x-parser-schema-id": "<anonymous-schema-268>"
                                            }
                                          },
                                          "x-parser-schema-id": "<anonymous-schema-266>"
                                        },
                                        "x-parser-schema-id": "<anonymous-schema-265>"
                                      }
                                    },
                                    "x-parser-schema-id": "<anonymous-schema-247>"
                                  },
                                  "x-parser-schema-id": "<anonymous-schema-246>"
                                },
                                "scores": {
                                  "type": "object",
                                  "additionalProperties": {
                                    "type": "integer",
                                    "x-parser-schema-id": "<anonymous-schema-270>"
                                  },
                                  "x-parser-schema-id": "<anonymous-schema-269>"
                                }
                              },
                              "x-parser-schema-id": "<anonymous-schema-241>"
                            },
                            "x-parser-schema-id": "<anonymous-schema-240>"
                          },
                          "winner": {
                            "type": "string",
                            "x-parser-schema-id": "<anonymous-schema-271>"
                          }
                        },
                        "example": {
                          "roomId": "6483",
                          "gameType": "TRICK_MYOMYO",
                          "difficulty": "BASIC",
                          "gameStatus": null,
                          "currentRound": 3,
                          "totalRounds": 3,
                          "aiScore": 0,
                          "scores": {
                            "AI": 0,
                            "z2E63KqK": 3,
                            "l3lwXGrb": 6
                          },
                          "gamePlayers": null,
                          "rounds": [
                            {
                              "roundIndex": 1,
                              "drawingEndTime": "2025-05-28T21:01:24.4270926",
                              "roundWinner": null,
                              "currentWordIndex": 2,
                              "words": [
                                {
                                  "wordIndex": 0,
                                  "word": "sandwich",
                                  "drawerUuid": "l3lwXGrb",
                                  "submitted": true,
                                  "imageURL": "https://gotchaai-image-bucket.s3.ap-northeast-2.amazonaws.com/l3lwXGrb/546258da-8e18-4792-8b35-5b8f8c92bcec.png",
                                  "aiGuesses": [
                                    {
                                      "guesserUuid": "AI",
                                      "guessWord": "cat",
                                      "attempts": 1,
                                      "correct": false
                                    }
                                  ],
                                  "playerGuesses": [
                                    {
                                      "guesserUuid": "z2E63KqK",
                                      "guessWord": "laptop",
                                      "attempts": 3,
                                      "correct": false
                                    }
                                  ],
                                  "aiPredictions": [
                                    {
                                      "predicted": "cat",
                                      "confidence": 99.4355320930481
                                    },
                                    {
                                      "predicted": "book",
                                      "confidence": 0.40926095098257065
                                    },
                                    {
                                      "predicted": "calendar",
                                      "confidence": 0.06800925475545228
                                    }
                                  ]
                                }
                              ],
                              "scores": {
                                "l3lwXGrb": 3
                              }
                            }
                          ],
                          "winner": "DRAW"
                        },
                        "x-parser-schema-id": "<anonymous-schema-228>"
                      },
                      "aiSays": {
                        "type": "string",
                        "example": "으.. 잠깐 오류가 났네. 다시 해볼게!",
                        "x-parser-schema-id": "<anonymous-schema-272>"
                      },
                      "endTime": {
                        "type": "string",
                        "nullable": true,
                        "example": null,
                        "x-parser-schema-id": "<anonymous-schema-273>"
                      },
                      "eventAt": {
                        "type": "string",
                        "example": "2025-05-28T21:02:41.1232855",
                        "x-parser-schema-id": "<anonymous-schema-274>"
                      }
                    },
                    "x-parser-schema-id": "<anonymous-schema-226>"
                  }
                },
                "x-parser-schema-id": "RedisResponse_GameEnd"
              }
            }
          ]
        }
      }
    }
  },
  "components": {
    "schemas": {
      "ExceptionRes": "$ref:$.channels./user/queue/errors/{userUuid}.subscribe.message.payload",
      "CreateRoomRequest": "$ref:$.channels./pub/lobby/create.publish.message.payload",
      "RoomReq": "$ref:$.channels./pub/room/{roomId}.publish.message.payload",
      "RedisResponse_RoomMetadata": {
        "type": "object",
        "description": "방 생성/요청 응답용 Redis 메시지 (RoomMetadata 포함)",
        "properties": {
          "userId": {
            "type": "string",
            "description": "메시지를 보낸 사용자 UUID",
            "x-parser-schema-id": "<anonymous-schema-275>"
          },
          "topic": {
            "type": "string",
            "description": "Redis로 발행된 채널명",
            "x-parser-schema-id": "<anonymous-schema-276>"
          },
          "payload": {
            "type": "object",
            "properties": {
              "id": {
                "type": "string",
                "description": "방 ID",
                "x-parser-schema-id": "<anonymous-schema-277>"
              },
              "title": {
                "type": "string",
                "description": "방 제목",
                "x-parser-schema-id": "<anonymous-schema-278>"
              },
              "owner": {
                "type": "string",
                "description": "방장 닉네임",
                "x-parser-schema-id": "<anonymous-schema-279>"
              },
              "hasPassword": {
                "type": "boolean",
                "description": "비밀번호 사용 여부",
                "x-parser-schema-id": "<anonymous-schema-280>"
              },
              "password": {
                "type": "string",
                "description": "비밀번호 (빈 문자열일 수 있음)",
                "x-parser-schema-id": "<anonymous-schema-281>"
              },
              "max": {
                "type": "integer",
                "description": "최대 인원 수",
                "x-parser-schema-id": "<anonymous-schema-282>"
              },
              "min": {
                "type": "integer",
                "description": "최소 인원 수",
                "x-parser-schema-id": "<anonymous-schema-283>"
              },
              "aiLevel": {
                "type": "string",
                "enum": [
                  "BASIC",
                  "ADVANCED"
                ],
                "description": "AI 난이도",
                "x-parser-schema-id": "<anonymous-schema-284>"
              },
              "gameMode": {
                "type": "string",
                "enum": [
                  "TRICK_MYOMYO",
                  "LULU_ART_EXAM"
                ],
                "description": "게임 모드",
                "x-parser-schema-id": "<anonymous-schema-285>"
              },
              "ownerUuid": {
                "type": "string",
                "description": "이 방을 생성한 사용자의 UUID입니다.  \n클라이언트는 해당 값을 통해 수신된 방 정보가 본인이 생성한 것인지 식별할 수 있습니다.\n",
                "x-parser-schema-id": "<anonymous-schema-286>"
              }
            },
            "x-parser-schema-id": "RoomMetadata"
          }
        },
        "x-parser-schema-id": "RedisResponse_RoomMetadata"
      },
      "RoomSummaryRes": "$ref:$.channels./sub/lobby/list/event.subscribe.message.oneOf[0].payload.properties.payload.properties.data",
      "RoomMetadata": "$ref:$.components.schemas.RedisResponse_RoomMetadata.properties.payload",
      "RedisResponse_RoomList_Create": "$ref:$.channels./sub/lobby/list/event.subscribe.message.oneOf[0].payload",
      "RedisResponse_RoomList_Update": "$ref:$.channels./sub/lobby/list/event.subscribe.message.oneOf[1].payload",
      "RedisResponse_RoomList_Delete": "$ref:$.channels./sub/lobby/list/event.subscribe.message.oneOf[2].payload",
      "RedisResponse_RoomChat": "$ref:$.channels./sub/room/{roomId}.subscribe.message.oneOf[0].payload",
      "RedisResponse_RoomJoin": {
        "type": "object",
        "properties": {
          "userId": {
            "type": "string",
            "x-parser-schema-id": "<anonymous-schema-287>"
          },
          "topic": {
            "type": "string",
            "x-parser-schema-id": "<anonymous-schema-288>"
          },
          "payload": {
            "type": "object",
            "properties": {
              "eventType": {
                "type": "string",
                "enum": [
                  "JOIN"
                ],
                "x-parser-schema-id": "<anonymous-schema-290>"
              },
              "eventAt": {
                "type": "string",
                "format": "date-time",
                "x-parser-schema-id": "<anonymous-schema-291>"
              },
              "data": {
                "type": "array",
                "items": {
                  "type": "object",
                  "properties": {
                    "userUuid": {
                      "type": "string",
                      "x-parser-schema-id": "<anonymous-schema-293>"
                    },
                    "nickname": {
                      "type": "string",
                      "x-parser-schema-id": "<anonymous-schema-294>"
                    },
                    "ready": {
                      "type": "boolean",
                      "x-parser-schema-id": "<anonymous-schema-295>"
                    }
                  },
                  "x-parser-schema-id": "RoomUserInfo"
                },
                "x-parser-schema-id": "<anonymous-schema-292>"
              }
            },
            "x-parser-schema-id": "<anonymous-schema-289>"
          }
        },
        "x-parser-schema-id": "RedisResponse_RoomJoin"
      },
      "RedisResponse_RoomReady": "$ref:$.channels./sub/room/{roomId}.subscribe.message.oneOf[1].payload",
      "RedisResponse_RoomUnReady": "$ref:$.channels./sub/room/{roomId}.subscribe.message.oneOf[2].payload",
      "RedisResponse_RoomExit": "$ref:$.channels./sub/room/{roomId}.subscribe.message.oneOf[3].payload",
      "RedisResponse_GameStart": "$ref:$.channels./sub/room/{roomId}.subscribe.message.oneOf[4].payload",
      "AISaysRes": "$ref:$.channels./sub/room/{roomId}.subscribe.message.oneOf[4].payload.properties.payload.properties.data",
      "RedisResponse_RoundStart": "$ref:$.channels./sub/game/{roomId}.subscribe.message.oneOf[0].payload",
      "RedisResponse_GuessStart": "$ref:$.channels./sub/game/{roomId}.subscribe.message.oneOf[1].payload",
      "RedisResponse_GuessRequest": "$ref:$.channels./sub/game/{roomId}.subscribe.message.oneOf[2].payload",
      "RedisResponse_GameEnd": "$ref:$.channels./sub/game/{roomId}.subscribe.message.oneOf[7].payload",
      "RedisResponse_RoundEnd": "$ref:$.channels./sub/game/{roomId}.subscribe.message.oneOf[6].payload",
      "RedisResponse_ScoreUpdate": "$ref:$.channels./sub/game/{roomId}.subscribe.message.oneOf[5].payload",
      "RedisResponse_GuessResult": "$ref:$.channels./sub/game/{roomId}.subscribe.message.oneOf[4].payload",
      "RedisResponse_GuessSubmit": "$ref:$.channels./sub/game/{roomId}.subscribe.message.oneOf[3].payload",
      "AllChatMessage": "$ref:$.channels./sub/chat/all.subscribe.message.payload.properties.payload",
      "PrivateChatMessage": "$ref:$.channels./sub/chat/private/{receiverId}.subscribe.message.payload.properties.payload",
      "RoomChatMessage": "$ref:$.channels./sub/room/{roomId}.subscribe.message.oneOf[0].payload.properties.payload.properties.data",
      "RoomUserInfo": "$ref:$.components.schemas.RedisResponse_RoomJoin.properties.payload.properties.data.items",
      "RedisReq_PrivateChat": "$ref:$.channels./pub/chat/private.publish.message.payload",
      "RedisReq_AllChat": "$ref:$.channels./pub/chat/all.publish.message.payload",
      "RedisRes_AllChat": "$ref:$.channels./sub/chat/all.subscribe.message.payload",
      "RedisRes_PrivateChat": "$ref:$.channels./sub/chat/private/{receiverId}.subscribe.message.payload",
      "Game": "$ref:$.channels./sub/room/{roomId}.subscribe.message.oneOf[4].payload.properties.payload.properties.data.properties.gameData",
      "GamePlayer": "$ref:$.channels./sub/room/{roomId}.subscribe.message.oneOf[4].payload.properties.payload.properties.data.properties.gameData.properties.gamePlayers.items",
      "Round": "$ref:$.channels./sub/room/{roomId}.subscribe.message.oneOf[4].payload.properties.payload.properties.data.properties.gameData.properties.rounds.items",
      "Word": "$ref:$.channels./sub/room/{roomId}.subscribe.message.oneOf[4].payload.properties.payload.properties.data.properties.gameData.properties.rounds.items.properties.words.items",
      "Guess": "$ref:$.channels./sub/room/{roomId}.subscribe.message.oneOf[4].payload.properties.payload.properties.data.properties.gameData.properties.rounds.items.properties.words.items.properties.aiGuesses.items",
      "AIPrediction": "$ref:$.channels./sub/room/{roomId}.subscribe.message.oneOf[4].payload.properties.payload.properties.data.properties.gameData.properties.rounds.items.properties.words.items.properties.aiPredictions.items",
      "Score": "$ref:$.channels./sub/room/{roomId}.subscribe.message.oneOf[4].payload.properties.payload.properties.data.properties.gameData.properties.scores",
      "RoomIdRes": "$ref:$.channels./sub/lobby/create/{userUuid}.subscribe.message.payload",
      "DrawingSubmitReq": "$ref:$.channels./pub/game/{roomId}.publish.message.oneOf[0].payload"
    }
  },
  "x-parser-spec-parsed": true,
  "x-parser-api-version": 3,
  "x-parser-spec-stringified": true
};
    const config = {"show":{"sidebar":true},"sidebar":{"showOperations":"byDefault"}};
    const appRoot = document.getElementById('root');
    AsyncApiStandalone.render(
        { schema, config, }, appRoot
    );
  