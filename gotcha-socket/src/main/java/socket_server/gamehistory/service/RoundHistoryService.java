package socket_server.gamehistory.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import socket_server.gamehistory.repository.RoundHistoryRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoundHistoryService {

    private final RoundHistoryRepository roundHistoryRepository;



}
