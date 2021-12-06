package com.example.demo.restservice;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

import io.grpc.StatusRuntimeException;
import io.grpc.example.demo.UserInfoGrpc;
import io.grpc.example.demo.UserInfoRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserInfoController {

    private final AtomicLong counter = new AtomicLong();
    private final UserInfoGrpc.UserInfoBlockingStub userInfoService;

    UserInfoController(UserInfoGrpc.UserInfoBlockingStub userInfoService) {
        this.userInfoService = userInfoService;
    }

    @PutMapping("/userinfo")
    public ResponseEntity<Void> updateUserInfo(@RequestBody UserInfo userInfo) {

        UserInfoRequest userInfoRequest = UserInfoRequest.newBuilder().
                setTraceId(counter.incrementAndGet()).
                setUserId(userInfo.getUserId()).
                setFirstName(userInfo.getFirstName()).
                setLastName(userInfo.getLastName()).
                setDob(userInfo.getDob()).
                setEmail(userInfo.getEmail()).
                setPhoneNumber(userInfo.getPhoneNumber()).
                setTimestamp(Instant.now().getEpochSecond()).
                build();

        try {
            userInfoService.updateUserInfo(userInfoRequest);
            System.out.println("Successfully updated ID " + counter);
        } catch (StatusRuntimeException e) {
            System.out.println("Error " + e.getStatus() + ": Update failed for ID " + counter);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}