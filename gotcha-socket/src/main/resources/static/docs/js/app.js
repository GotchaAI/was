
    const schema = {
  "asyncapi": "2.6.0",
  "id": "gotcha:websocket-api",
  "defaultContentType": "application/json",
  "info": {
    "title": "Gotcha WebSocket API",
    "version": "1.0.0",
    "description": "이 문서는 Gotcha 게임 플랫폼의 실시간 WebSocket(STOMP) 통신 명세서입니다.\nSockJS를 통해 WebSocket 연결을 시도합니다.\n\n| 주체             | 동작       | 사용하는 STOMP 함수       | 경로 예시              |\n|------------------|------------|---------------------------|-------------------------|\n| 클라이언트 → 서버 | 메시지 보냄 | `stompClient.send()`      | `/pub/**`      |\n| 서버 → 클라이언트 | 메시지 보냄 | `stompClient.subscribe()` | `/sub/**`         |\n\n⭐본 서비스에서 사용하는 메시지 전송 경로는 총 4개로 구성됩니다. 로직에 따른 구독 경로를 달리하지 않고, dto로 로직을 구분합니다.\n\n[prefix 있는 채널들]\n- /pub/room/{roomId} : 대기방 관련 로직들 (대기방 내 채팅 포함)\n- /pub/chat/** : 채팅 관련 로직들 (전체 채팅 및 개인 채팅)\n- /pub/game/** : 게임 관련 로직들 \n\n[절대 경로 채널]\n- user/queue/errors : 에러 처리 채널\n"
  },
  "servers": {
    "production": {
      "url": "http://43.202.159.231:8080/ws-connect",
      "protocol": "ws",
      "description": "SockJS 기반 STOMP WebSocket 연결을 지원합니다. 기본적으로 WebSocket(ws) 사용, 실패 시 HTTP long-polling 등으로 fallback 됩니다.\n\n\n핸드쉐이크 시, 반드시 HTTP 헤더에 Authorization: Bearer {JWT 토큰} 를 포함해야 합니다.\n"
    }
  },
  "channels": {
    "/user/queue/errors": {
      "description": "사용자별 에러 메시지를 수신하는 WebSocket 구독 채널입니다.\n서버에서 발생한 예외 정보를 이 채널로 전달합니다.\n",
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
    "/pub/room/create": {
      "description": "클라이언트가 새 방을 생성 요청하는 채널",
      "publish": {
        "message": {
          "headers": {
            "type": "object",
            "properties": {},
            "x-parser-schema-id": "<anonymous-schema-6>"
          },
          "payload": {
            "type": "object",
            "required": [
              "title",
              "maxUser",
              "hasPassword",
              "aimode",
              "gameMode",
              "roundCount"
            ],
            "properties": {
              "title": {
                "type": "string",
                "description": "제목 (필수 입력)",
                "x-parser-schema-id": "<anonymous-schema-7>"
              },
              "maxUser": {
                "type": "integer",
                "description": "최대 인원수 (필수 입력)",
                "x-parser-schema-id": "<anonymous-schema-8>"
              },
              "hasPassword": {
                "type": "boolean",
                "description": "비밀번호 사용 여부",
                "x-parser-schema-id": "<anonymous-schema-9>"
              },
              "password": {
                "type": "string",
                "description": "문자열이 숫자 4자리로만 구성되어 있어야 한다",
                "x-parser-schema-id": "<anonymous-schema-10>"
              },
              "aimode": {
                "type": "string",
                "enum": [
                  "BASIC",
                  "ADVANCED"
                ],
                "description": "인공지능 난이도 (필수)",
                "x-parser-schema-id": "<anonymous-schema-11>"
              },
              "gameMode": {
                "type": "string",
                "enum": [
                  "TRICK_MYOMYO",
                  "LULU_ART_EXAM"
                ],
                "description": "묘묘 - 2인 / 루루 - 1인 게임 모드",
                "x-parser-schema-id": "<anonymous-schema-12>"
              },
              "roundCount": {
                "type": "integer",
                "minimum": 1,
                "maximum": 5,
                "description": "라운드 수 (1~5 사이)",
                "x-parser-schema-id": "<anonymous-schema-13>"
              }
            },
            "x-parser-schema-id": "CreateRoomRequest"
          },
          "x-parser-message-name": "<anonymous-message-1>"
        }
      }
    },
    "/sub/room/create/info": {
      "description": "클라이언트가 방 생성 응답을 받는 용도",
      "subscribe": {
        "message": {
          "headers": {
            "type": "object",
            "properties": {},
            "x-parser-schema-id": "<anonymous-schema-14>"
          },
          "payload": {
            "type": "object",
            "description": "방 생성/요청 응답용 Redis 메시지 (RoomMetadata 포함)",
            "properties": {
              "userId": {
                "type": "string",
                "description": "메시지를 보낸 사용자 UUID",
                "x-parser-schema-id": "<anonymous-schema-15>"
              },
              "topic": {
                "type": "string",
                "description": "Redis로 발행된 채널명",
                "x-parser-schema-id": "<anonymous-schema-16>"
              },
              "payload": {
                "type": "object",
                "properties": {
                  "id": {
                    "type": "string",
                    "description": "방 ID",
                    "x-parser-schema-id": "<anonymous-schema-17>"
                  },
                  "title": {
                    "type": "string",
                    "description": "방 제목",
                    "x-parser-schema-id": "<anonymous-schema-18>"
                  },
                  "owner": {
                    "type": "string",
                    "description": "방장 닉네임",
                    "x-parser-schema-id": "<anonymous-schema-19>"
                  },
                  "hasPassword": {
                    "type": "boolean",
                    "description": "비밀번호 사용 여부",
                    "x-parser-schema-id": "<anonymous-schema-20>"
                  },
                  "password": {
                    "type": "string",
                    "description": "비밀번호 (빈 문자열일 수 있음)",
                    "x-parser-schema-id": "<anonymous-schema-21>"
                  },
                  "max": {
                    "type": "integer",
                    "description": "최대 인원 수",
                    "x-parser-schema-id": "<anonymous-schema-22>"
                  },
                  "min": {
                    "type": "integer",
                    "description": "최소 인원 수",
                    "x-parser-schema-id": "<anonymous-schema-23>"
                  },
                  "aiLevel": {
                    "type": "string",
                    "enum": [
                      "BASIC",
                      "ADVANCED"
                    ],
                    "description": "AI 난이도",
                    "x-parser-schema-id": "<anonymous-schema-24>"
                  },
                  "gameMode": {
                    "type": "string",
                    "enum": [
                      "TRICK_MYOMYO",
                      "LULU_ART_EXAM"
                    ],
                    "description": "게임 모드",
                    "x-parser-schema-id": "<anonymous-schema-25>"
                  },
                  "ownerUuid": {
                    "type": "string",
                    "description": "이 방을 생성한 사용자의 UUID입니다.  \n클라이언트는 해당 값을 통해 수신된 방 정보가 본인이 생성한 것인지 식별할 수 있습니다.\n",
                    "x-parser-schema-id": "<anonymous-schema-26>"
                  }
                },
                "x-parser-schema-id": "RoomMetadata"
              }
            },
            "x-parser-schema-id": "RedisResponse_RoomMetadata"
          },
          "x-parser-message-name": "<anonymous-message-2>"
        }
      }
    },
    "/pub/room/{roomId}": {
      "description": "클라이언트가 대기방 내에서 발생 가능한 로직 요청을 전송하는 채널입니다.\n\n[사용 가능한 이벤트 타입]\n- CHAT: 대기방 채팅\n- READY: 준비 상태 변경\n- JOIN: 방 참가\n- EXIT: 방 퇴장\n- START: 게임 시작\n\n`eventType` 값에 따라 content의 의미는 다르게 해석됩니다.\n",
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
                  "START"
                ],
                "description": "클라이언트가 수행하고자 하는 이벤트의 종류입니다.\n",
                "x-parser-schema-id": "<anonymous-schema-27>"
              },
              "content": {
                "type": "string",
                "description": "이벤트에 따라 의미가 달라지는 콘텐츠입니다.\n- CHAT: 채팅 메시지\n- READY: 사용자의 준비 상태\n- JOIN : 방 참가\n- EXIT : 방 퇴장\n- START : 게임 시작\n",
                "x-parser-schema-id": "<anonymous-schema-28>"
              }
            },
            "x-parser-schema-id": "RoomReq"
          }
        }
      }
    },
    "/sub/room/event/{roomId}": {
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
                    "x-parser-schema-id": "<anonymous-schema-29>"
                  },
                  "topic": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-30>"
                  },
                  "payload": {
                    "type": "object",
                    "properties": {
                      "eventType": {
                        "type": "string",
                        "enum": [
                          "CHAT"
                        ],
                        "x-parser-schema-id": "<anonymous-schema-32>"
                      },
                      "eventAt": {
                        "type": "string",
                        "format": "date-time",
                        "x-parser-schema-id": "<anonymous-schema-33>"
                      },
                      "data": {
                        "type": "object",
                        "properties": {
                          "nickname": {
                            "type": "string",
                            "x-parser-schema-id": "<anonymous-schema-34>"
                          },
                          "content": {
                            "type": "string",
                            "x-parser-schema-id": "<anonymous-schema-35>"
                          },
                          "chatType": {
                            "type": "string",
                            "enum": [
                              "ROOM"
                            ],
                            "x-parser-schema-id": "<anonymous-schema-36>"
                          },
                          "sentAt": {
                            "type": "string",
                            "format": "date-time",
                            "x-parser-schema-id": "<anonymous-schema-37>"
                          }
                        },
                        "x-parser-schema-id": "RoomChatMessage"
                      }
                    },
                    "x-parser-schema-id": "<anonymous-schema-31>"
                  }
                },
                "x-parser-schema-id": "RedisResponse_RoomChat"
              }
            },
            {
              "name": "JoinEvent",
              "summary": "JOIN 이벤트 - 대기방 내 기존 유저 목록 수신",
              "payload": {
                "type": "object",
                "properties": {
                  "userId": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-38>"
                  },
                  "topic": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-39>"
                  },
                  "payload": {
                    "type": "object",
                    "properties": {
                      "eventType": {
                        "type": "string",
                        "enum": [
                          "JOIN"
                        ],
                        "x-parser-schema-id": "<anonymous-schema-41>"
                      },
                      "eventAt": {
                        "type": "string",
                        "format": "date-time",
                        "x-parser-schema-id": "<anonymous-schema-42>"
                      },
                      "data": {
                        "type": "array",
                        "items": {
                          "type": "object",
                          "properties": {
                            "userUuid": {
                              "type": "string",
                              "x-parser-schema-id": "<anonymous-schema-44>"
                            },
                            "nickname": {
                              "type": "string",
                              "x-parser-schema-id": "<anonymous-schema-45>"
                            },
                            "ready": {
                              "type": "boolean",
                              "x-parser-schema-id": "<anonymous-schema-46>"
                            }
                          },
                          "x-parser-schema-id": "RoomUserInfo"
                        },
                        "x-parser-schema-id": "<anonymous-schema-43>"
                      }
                    },
                    "x-parser-schema-id": "<anonymous-schema-40>"
                  }
                },
                "x-parser-schema-id": "RedisResponse_RoomJoin"
              }
            },
            {
              "name": "ReadyEvent",
              "summary": "READY 이벤트 - 준비 상태 변경"
            },
            {
              "name": "ExitEvent",
              "summary": "EXIT 이벤트 - 퇴장 정보"
            },
            {
              "name": "StartEvent",
              "summary": "START 이벤트 - 게임 시작 알림"
            }
          ]
        }
      }
    },
    "/pub/chat/all": {
      "description": "클라이언트가 전체 채팅 메시지를 전송하는 채널",
      "publish": {
        "message": {
          "headers": {
            "type": "object",
            "properties": {},
            "x-parser-schema-id": "<anonymous-schema-47>"
          },
          "payload": {
            "type": "object",
            "description": "채팅 메시지 전송 요청 객체입니다.\n",
            "properties": {
              "content": {
                "type": "string",
                "description": "메시지 내용",
                "x-parser-schema-id": "<anonymous-schema-48>"
              }
            },
            "required": [
              "content"
            ],
            "x-parser-schema-id": "RedisReq_AllChat"
          },
          "x-parser-message-name": "<anonymous-message-3>"
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
                "x-parser-schema-id": "<anonymous-schema-49>"
              },
              "topic": {
                "type": "string",
                "description": "Redis로 발행된 전체 채팅 채널명",
                "x-parser-schema-id": "<anonymous-schema-50>"
              },
              "payload": {
                "type": "object",
                "properties": {
                  "nickname": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-51>"
                  },
                  "content": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-52>"
                  },
                  "chatType": {
                    "type": "string",
                    "enum": [
                      "ALL"
                    ],
                    "x-parser-schema-id": "<anonymous-schema-53>"
                  },
                  "sentAt": {
                    "type": "string",
                    "format": "date-time",
                    "x-parser-schema-id": "<anonymous-schema-54>"
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
          "headers": {
            "type": "object",
            "properties": {},
            "x-parser-schema-id": "<anonymous-schema-55>"
          },
          "payload": {
            "type": "object",
            "description": "채팅 메시지 전송 요청 객체입니다.\n",
            "properties": {
              "receiverUuid": {
                "type": "string",
                "nullable": true,
                "description": "- 개인 채팅 시, `receiverUuid'는 필수입니다.\n",
                "x-parser-schema-id": "<anonymous-schema-56>"
              },
              "content": {
                "type": "string",
                "description": "메시지 내용",
                "x-parser-schema-id": "<anonymous-schema-57>"
              }
            },
            "required": [
              "content"
            ],
            "x-parser-schema-id": "RedisReq_PrivateChat"
          },
          "x-parser-message-name": "<anonymous-message-4>"
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
                "x-parser-schema-id": "<anonymous-schema-58>"
              },
              "topic": {
                "type": "string",
                "description": "Redis로 발행된 개인 채팅 채널명 (`/sub/chat/private/{receiverUuid}`)",
                "x-parser-schema-id": "<anonymous-schema-59>"
              },
              "payload": {
                "type": "object",
                "properties": {
                  "nickname": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-60>"
                  },
                  "content": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-61>"
                  },
                  "chatType": {
                    "type": "string",
                    "enum": [
                      "PRIVATE"
                    ],
                    "x-parser-schema-id": "<anonymous-schema-62>"
                  },
                  "sentAt": {
                    "type": "string",
                    "format": "date-time",
                    "x-parser-schema-id": "<anonymous-schema-63>"
                  }
                },
                "x-parser-schema-id": "PrivateChatMessage"
              }
            },
            "x-parser-schema-id": "RedisRes_PrivateChat"
          }
        }
      }
    }
  },
  "components": {
    "schemas": {
      "ExceptionRes": "$ref:$.channels./user/queue/errors.subscribe.message.payload",
      "CreateRoomRequest": "$ref:$.channels./pub/room/create.publish.message.payload",
      "RoomReq": "$ref:$.channels./pub/room/{roomId}.publish.message.payload",
      "RedisResponse_RoomMetadata": "$ref:$.channels./sub/room/create/info.subscribe.message.payload",
      "RoomMetadata": "$ref:$.channels./sub/room/create/info.subscribe.message.payload.properties.payload",
      "RedisResponse_RoomChat": "$ref:$.channels./sub/room/event/{roomId}.subscribe.message.oneOf[0].payload",
      "RedisResponse_RoomJoin": "$ref:$.channels./sub/room/event/{roomId}.subscribe.message.oneOf[1].payload",
      "AllChatMessage": "$ref:$.channels./sub/chat/all.subscribe.message.payload.properties.payload",
      "PrivateChatMessage": "$ref:$.channels./sub/chat/private/{receiverId}.subscribe.message.payload.properties.payload",
      "RoomChatMessage": "$ref:$.channels./sub/room/event/{roomId}.subscribe.message.oneOf[0].payload.properties.payload.properties.data",
      "RoomUserInfo": "$ref:$.channels./sub/room/event/{roomId}.subscribe.message.oneOf[1].payload.properties.payload.properties.data.items",
      "RedisReq_PrivateChat": "$ref:$.channels./pub/chat/private.publish.message.payload",
      "RedisReq_AllChat": "$ref:$.channels./pub/chat/all.publish.message.payload",
      "RedisRes_AllChat": "$ref:$.channels./sub/chat/all.subscribe.message.payload",
      "RedisRes_PrivateChat": "$ref:$.channels./sub/chat/private/{receiverId}.subscribe.message.payload"
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
  