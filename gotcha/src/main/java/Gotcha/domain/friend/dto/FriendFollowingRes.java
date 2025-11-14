package Gotcha.domain.friend.dto;

public record FriendFollowingRes(
        String roomId
) {
    public static FriendFollowingRes from(String roomId){
        return new FriendFollowingRes(roomId);
    }
}
