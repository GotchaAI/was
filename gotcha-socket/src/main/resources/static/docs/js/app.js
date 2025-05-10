
    const schema = {
  "asyncapi": "2.6.0",
  "id": "urn:gotcha:websocket-api",
  "defaultContentType": "application/json",
  "info": {
    "title": "Gotcha WebSocket API",
    "version": "1.0.0",
    "description": "이 문서는 Gotcha 게임 플랫폼의 실시간 WebSocket(STOMP) 통신 명세서입니다.\nSockJS를 통해 WebSocket 연결을 시도합니다.\n",
    "contact": {
      "name": "Gotcha Dev Team",
      "email": "gotcha@example.com"
    },
    "license": {
      "name": "MIT",
      "url": "https://opensource.org/licenses/MIT"
    }
  },
  "servers": {
    "production": {
      "url": "http://43.202.159.231:8080/ws-connect",
      "protocol": "ws",
      "description": "SockJS 기반 STOMP WebSocket 연결을 지원합니다. 기본적으로 WebSocket(ws) 사용, 실패 시 HTTP long-polling 등으로 fallback 됩니다.\n"
    }
  },
  "channels": {
    "pub/room/enter/{roomId}": {
      "description": "클라이언트가 방 입장을 요청하는 채널",
      "parameters": {
        "roomId": {
          "description": "입장할 방의 ID",
          "schema": {
            "type": "string",
            "x-parser-schema-id": "roomId"
          }
        }
      },
      "publish": {
        "message": {
          "headers": {
            "type": "object",
            "properties": {
              "user-id": {
                "type": "string",
                "description": "요청하는 사용자의 ID",
                "x-parser-schema-id": "<anonymous-schema-2>"
              }
            },
            "x-parser-schema-id": "<anonymous-schema-1>"
          },
          "payload": {
            "type": "object",
            "description": "입력 페이로드 없음",
            "x-parser-schema-id": "<anonymous-schema-3>"
          },
          "x-parser-message-name": "<anonymous-message-1>"
        }
      }
    },
    "pub/room/leave/{roomId}": {
      "description": "클라이언트가 방 퇴장(나감)을 요청하는 채널",
      "parameters": {
        "roomId": {
          "description": "퇴장할 방의 ID",
          "schema": {
            "type": "string",
            "x-parser-schema-id": "roomId"
          }
        }
      },
      "publish": {
        "message": {
          "headers": {
            "type": "object",
            "properties": {
              "user-id": {
                "type": "string",
                "description": "요청하는 사용자의 ID",
                "x-parser-schema-id": "<anonymous-schema-5>"
              }
            },
            "x-parser-schema-id": "<anonymous-schema-4>"
          },
          "payload": {
            "type": "object",
            "description": "입력 페이로드 없음",
            "x-parser-schema-id": "<anonymous-schema-6>"
          },
          "x-parser-message-name": "<anonymous-message-2>"
        }
      }
    },
    "pub/room/create": {
      "description": "클라이언트가 새 방을 생성 요청하는 채널",
      "publish": {
        "message": {
          "headers": {
            "type": "object",
            "properties": {
              "user-id": {
                "type": "string",
                "description": "요청하는 사용자의 ID",
                "x-parser-schema-id": "<anonymous-schema-8>"
              }
            },
            "x-parser-schema-id": "<anonymous-schema-7>"
          },
          "payload": {
            "type": "object",
            "properties": {
              "roomId": {
                "type": "string",
                "x-parser-schema-id": "<anonymous-schema-9>"
              },
              "title": {
                "type": "string",
                "x-parser-schema-id": "<anonymous-schema-10>"
              },
              "hasPassword": {
                "type": "boolean",
                "x-parser-schema-id": "<anonymous-schema-11>"
              },
              "password": {
                "type": "string",
                "x-parser-schema-id": "<anonymous-schema-12>"
              },
              "gameMode": {
                "type": "object",
                "properties": {
                  "name": {
                    "type": "string",
                    "x-parser-schema-id": "<anonymous-schema-14>"
                  },
                  "maxPlayers": {
                    "type": "integer",
                    "x-parser-schema-id": "<anonymous-schema-15>"
                  },
                  "minPlayers": {
                    "type": "integer",
                    "x-parser-schema-id": "<anonymous-schema-16>"
                  }
                },
                "x-parser-schema-id": "<anonymous-schema-13>"
              }
            },
            "x-parser-schema-id": "CreateRoomRequest"
          },
          "x-parser-message-name": "<anonymous-message-3>"
        }
      }
    },
    "pub/room/update": {
      "description": "클라이언트가 방 속성을 수정 요청하는 채널",
      "publish": {
        "message": {
          "headers": {
            "type": "object",
            "properties": {
              "user-id": {
                "type": "string",
                "description": "요청하는 사용자의 ID",
                "x-parser-schema-id": "<anonymous-schema-18>"
              }
            },
            "x-parser-schema-id": "<anonymous-schema-17>"
          },
          "payload": {
            "type": "object",
            "properties": {
              "roomId": {
                "type": "string",
                "x-parser-schema-id": "<anonymous-schema-19>"
              },
              "field": {
                "type": "string",
                "description": "변경할 필드명 (e.g. \\\"OWNER\\\", \\\"TITLE\\\" 등)",
                "x-parser-schema-id": "<anonymous-schema-20>"
              },
              "value": {
                "type": "string",
                "description": "변경할 값",
                "x-parser-schema-id": "<anonymous-schema-21>"
              }
            },
            "x-parser-schema-id": "UpdateRoomFieldRequest"
          },
          "x-parser-message-name": "<anonymous-message-4>"
        }
      }
    },
    "/sub/room/init/info/{roomId}": {
      "description": "서버가 방 초기 정보(메타 + 참가자 목록) 브로드캐스트",
      "parameters": {
        "roomId": {
          "description": "해당 방의 ID",
          "schema": {
            "type": "string",
            "x-parser-schema-id": "roomId"
          }
        }
      },
      "subscribe": {
        "message": {
          "payload": {
            "type": "object",
            "properties": {
              "roomId": {
                "type": "string",
                "x-parser-schema-id": "<anonymous-schema-23>"
              },
              "title": {
                "type": "string",
                "x-parser-schema-id": "<anonymous-schema-24>"
              },
              "owner": {
                "type": "string",
                "x-parser-schema-id": "<anonymous-schema-25>"
              },
              "hasPassword": {
                "type": "boolean",
                "x-parser-schema-id": "<anonymous-schema-26>"
              },
              "password": {
                "type": "string",
                "x-parser-schema-id": "<anonymous-schema-27>"
              },
              "max": {
                "type": "integer",
                "x-parser-schema-id": "<anonymous-schema-28>"
              },
              "min": {
                "type": "integer",
                "x-parser-schema-id": "<anonymous-schema-29>"
              },
              "aiLevel": {
                "type": "string",
                "x-parser-schema-id": "<anonymous-schema-30>"
              },
              "gameMode": {
                "type": "string",
                "x-parser-schema-id": "<anonymous-schema-31>"
              },
              "users": {
                "type": "array",
                "items": {
                  "type": "string",
                  "x-parser-schema-id": "<anonymous-schema-33>"
                },
                "x-parser-schema-id": "<anonymous-schema-32>"
              },
              "joinedUserId": {
                "type": "string",
                "x-parser-schema-id": "<anonymous-schema-34>"
              }
            },
            "x-parser-schema-id": "<anonymous-schema-22>"
          },
          "x-parser-message-name": "<anonymous-message-5>"
        }
      }
    },
    "/sub/room/leave/{roomId}": {
      "description": "서버가 방 퇴장 알림 브로드캐스트",
      "parameters": {
        "roomId": {
          "description": "해당 방의 ID",
          "schema": {
            "type": "string",
            "x-parser-schema-id": "roomId"
          }
        }
      },
      "subscribe": {
        "message": {
          "payload": {
            "type": "object",
            "properties": {
              "userId": {
                "type": "string",
                "x-parser-schema-id": "<anonymous-schema-36>"
              }
            },
            "x-parser-schema-id": "<anonymous-schema-35>"
          },
          "x-parser-message-name": "<anonymous-message-6>"
        }
      }
    },
    "/sub/room/create": {
      "description": "서버가 새 방 생성 알림 브로드캐스트",
      "subscribe": {
        "message": {
          "payload": {
            "type": "object",
            "properties": {
              "title": {
                "type": "string",
                "x-parser-schema-id": "<anonymous-schema-37>"
              },
              "owner": {
                "type": "string",
                "x-parser-schema-id": "<anonymous-schema-38>"
              },
              "hasPassword": {
                "type": "boolean",
                "x-parser-schema-id": "<anonymous-schema-39>"
              },
              "password": {
                "type": "string",
                "x-parser-schema-id": "<anonymous-schema-40>"
              },
              "max": {
                "type": "integer",
                "x-parser-schema-id": "<anonymous-schema-41>"
              },
              "min": {
                "type": "integer",
                "x-parser-schema-id": "<anonymous-schema-42>"
              },
              "aiLevel": {
                "type": "string",
                "x-parser-schema-id": "<anonymous-schema-43>"
              },
              "gameMode": {
                "type": "string",
                "x-parser-schema-id": "<anonymous-schema-44>"
              }
            },
            "x-parser-schema-id": "RoomMetadata"
          },
          "x-parser-message-name": "<anonymous-message-7>"
        }
      }
    },
    "/sub/room/update/{roomId}": {
      "description": "서버가 방 정보 변경 알림 브로드캐스트",
      "parameters": {
        "roomId": {
          "description": "해당 방의 ID",
          "schema": {
            "type": "string",
            "x-parser-schema-id": "roomId"
          }
        }
      },
      "subscribe": {
        "message": {
          "payload": "$ref:$.channels./sub/room/create.subscribe.message.payload",
          "x-parser-message-name": "<anonymous-message-8>"
        }
      }
    }
  },
  "components": {
    "schemas": {
      "CreateRoomRequest": "$ref:$.channels.pub/room/create.publish.message.payload",
      "UpdateRoomFieldRequest": "$ref:$.channels.pub/room/update.publish.message.payload",
      "RoomMetadata": "$ref:$.channels./sub/room/create.subscribe.message.payload"
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
  