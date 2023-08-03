package com.ssafy.crit.challenge.service;

import com.ssafy.crit.auth.entity.User;
import com.ssafy.crit.challenge.entity.Challenge;
import com.ssafy.crit.challenge.repository.ChallengeRepository;
import com.ssafy.crit.challenge.repository.ChallengeUserRepository;
import com.ssafy.crit.common.exception.BadRequestException;
import io.openvidu.java.client.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeUserRepository challengeUserRepository;

    /**
     * 챌린지(WebRTC) 입장을 제공하는 서비스
     * 230803 조경호
     * */
    public String initializeSession(User user, Map<String, Object> params, OpenVidu openVidu) throws Exception {
        // 세션 아이디와 유저 정보를 가지고 해당 유저가 챌린지에 해당하는지 확인
        String customSessionId = String.valueOf(params.get("customSessionId"));
        isInChallenge(customSessionId, user);


        // 인가 된 경우 세션 만들어서 세션 아이디 돌려주기
        SessionProperties properties = SessionProperties.fromJson(params).build();
        Session session = openVidu.createSession(properties);
        return session.getSessionId();
    }

    public String createConnection(User user, OpenVidu openVidu, String sessionId, Map<String, Object> params)
            throws Exception{
        // 세션 아이디와 유저 정보를 가지고 해당 유저가 챌린지에 해당하는지 확인
        log.debug("isInChallenge Not OK");
        isInChallenge(sessionId, user);
        log.debug("isInChallenge OK");
        Session session = openVidu.getActiveSession(sessionId); // OpenVidu 미디어 서버에서 세션이 존재하는 지 확인
        if (session == null) {
            throw new BadRequestException("세션을 찾을 수 없습니다.");
        }

        ConnectionProperties properties = ConnectionProperties.fromJson(params).build();
        Connection connection = session.createConnection(properties); // connection 만들기
        return connection.getToken(); // 토큰 반환
    }


    // 유저와 세션아이디를 넘겨주면 챌린지 아이디를 뽑아서 유저가 해당 챌린지에 들어갈 수 있는지 확인
    private void isInChallenge(String sessionId, User user) {
        
        // _ 뒤에 challengeId를 추출
        String[] sessionInfo = sessionId.split("_"); 
        Long challengeId = Long.parseLong(sessionInfo[sessionInfo.length - 1]); 
        
        log.debug("challenge_id : {}", challengeId);
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new BadRequestException("챌린지를 찾을 수 없습니다.")
        ); // 챌린지가 존재하지 않는 경우

        // 유저가 챌린지에 참여 중인지 확인하기
        challengeUserRepository.findByChallengeAndUser(challenge, user).orElseThrow(
                () -> new BadRequestException("챌린지에 참여 중이지 않습니다.")
        );
    }

}
