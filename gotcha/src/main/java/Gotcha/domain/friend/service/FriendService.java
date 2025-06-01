package Gotcha.domain.friend.service;

import Gotcha.domain.friend.dto.FriendReq;
import Gotcha.domain.friend.dto.FriendRequestRes;
import Gotcha.domain.friend.dto.FriendRes;
import Gotcha.domain.friend.exception.FriendExceptionCode;
import Gotcha.domain.friend.repository.FriendRepository;
import Gotcha.domain.friend.repository.FriendRequestRepository;
import gotcha_common.exception.CustomException;
import gotcha_domain.friend.Friend;
import gotcha_domain.friend.FriendRequest;
import gotcha_domain.user.User;
import gotcha_user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import socket_server.domain.friend.dto.FriendEventType;
import socket_server.domain.friend.dto.FriendSummaryRes;
import socket_server.domain.friend.service.FriendSocketService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final FriendRepository friendRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final UserService userService;
    private final FriendSocketService friendSocketService;

    @Transactional(readOnly = true)
    public List<FriendRes> getFriends(Long userId) {
        User user = userService.findUserByUserId(userId);
        return friendRepository.findAllByUserId(userId).stream()
                .map(friend -> friend.getOther(user))
                .distinct()
                .map(FriendRes::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FriendRequestRes> getFriendRequestResToMe(Long userId) {
        List<FriendRequest> requests = friendRequestRepository.findWithFromUserByToUserId(userId);

        return requests.stream()
                .map(FriendRequestRes::from)
                .toList();
    }

    @Transactional
    public void requestFriend(Long userId, FriendReq friendReq) {
        User fromUser = userService.findUserByUserId(userId);
        User toUser = userService.findUserByNickname(friendReq.nickname());

        if (fromUser.getId().equals(toUser.getId())) {
            throw new CustomException(FriendExceptionCode.SELF_REQUEST_NOT_ALLOWED);
        }

        if (friendRepository.existsFriendRelationBetween(fromUser.getId(), toUser.getId())) {
            throw new CustomException(FriendExceptionCode.ALREADY_FRIENDS);
        }

        if (friendRequestRepository.existsMutualRequest(fromUser, toUser)) {
            throw new CustomException(FriendExceptionCode.FRIEND_REQUEST_ALREADY_EXISTS);
        }

        FriendRequest request = FriendRequest.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .build();

        friendRequestRepository.save(request);

        friendSocketService.sendFriendAlert(fromUser.getUuid(), toUser.getUuid(), FriendSummaryRes.from(request), FriendEventType.REQUEST);
    }

    @Transactional
    public void acceptFriend(Long userId, Long friendRequestId) {
        FriendRequest friendRequest = friendRequestRepository.findById(friendRequestId)
                .orElseThrow(() -> new CustomException(FriendExceptionCode.FRIEND_REQUEST_NOT_FOUND));

        User toUser = userService.findUserByUserId(userId);
        User fromUser = friendRequest.getFromUser();

        if (!friendRequest.getToUser().equals(toUser)) {
            throw new CustomException(FriendExceptionCode.INVALID_REQUEST_ACCESS);
        }

        if (friendRepository.existsFriendRelationBetween(toUser.getId(), fromUser.getId())) {
            throw new CustomException(FriendExceptionCode.ALREADY_FRIENDS);
        }

        Friend friend = new Friend(fromUser, toUser);
        friendRepository.save(friend);

        friendRequestRepository.delete(friendRequest);

        FriendSummaryRes friendSummaryRes = FriendSummaryRes.from(friendRequest);

        friendSocketService.sendFriendAlert(toUser.getUuid(), fromUser.getUuid(), friendSummaryRes, FriendEventType.ACCEPT);
    }

    @Transactional
    public void rejectFriend(Long userId, Long friendRequestId) {
        FriendRequest friendRequest = friendRequestRepository.findById(friendRequestId)
                .orElseThrow(() -> new CustomException(FriendExceptionCode.FRIEND_REQUEST_NOT_FOUND));

        User toUser = userService.findUserByUserId(userId);

        if (!friendRequest.getToUser().equals(toUser)) {
            throw new CustomException(FriendExceptionCode.INVALID_REQUEST_ACCESS);
        }

        friendRequestRepository.delete(friendRequest);


        FriendSummaryRes friendSummaryRes = FriendSummaryRes.from(friendRequest);

        friendSocketService.sendFriendAlert(toUser.getUuid(), friendRequest.getFromUser().getUuid(), friendSummaryRes, FriendEventType.REJECT);
    }

    @Transactional
    public void deleteFriend(Long userId, String friendUuid) {
        User user = userService.findUserByUserId(userId);
        User friend = userService.findUserByUuid(friendUuid);

        Friend relation = friendRepository
                .findFriendRelation(user.getId(), friend.getId());

        if (relation == null) {
            throw new CustomException(FriendExceptionCode.NOT_FRIEND);
        }

        friendRepository.delete(relation);

        friendSocketService.sendFriendAlert(user.getUuid(), friendUuid, user.getUuid(), FriendEventType.DELETE);
    }
}
