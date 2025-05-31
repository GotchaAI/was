//package socket_server.domain.lobby.handler;
//
//import gotcha_domain.auth.SecurityUserDetails;
//import org.springframework.stereotype.Component;
//import socket_server.domain.room.dto.RoomReq;
//import socket_server.domain.room.handler.RoomEventHandler;
//import socket_server.domain.room.model.RoomEventType;
//import socket_server.domain.room.service.RoomUserService;
//
//@Component
//public class JoinRoomHandler implements RoomEventHandler {
//    private final RoomUserService roomUserService;
//
//    public JoinRoomHandler(RoomUserService roomUserService) {
//        this.roomUserService = roomUserService;
//    }
//
//    @Override
//    public RoomEventType getEventType() {
//        return RoomEventType.JOIN;
//    }
//
//    @Override
//    public void handle(String roomId, SecurityUserDetails userDetails, RoomReq request) {
//        roomUserService.joinAndBroadcast(roomId, userDetails.getUuid(), userDetails.getNickname() ,request.content());
//    }
//}