package com.example.demo.restservice;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

import io.grpc.StatusRuntimeException;
import io.grpc.example.demo.UserInfoGrpc;
import io.grpc.example.demo.UserInfoRequest;
import org.springframework.web.bind.annotation.*;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;


@RestController
public class UserInfoController {

    private final AtomicLong counter = new AtomicLong();
    private final String grpcServerTarget = "localhost:50051";
    private final UserInfoGrpc.UserInfoBlockingStub userInfoService;

    UserInfoController() {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(grpcServerTarget).usePlaintext().build();
        userInfoService = UserInfoGrpc.newBlockingStub(channel);
    }

    @PutMapping("/userinfo")
    public UserInfo updateUserInfo(@RequestBody UserInfo userInfo) {

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
            System.out.println("Error: Update failed for ID " + counter);
        }

        return userInfo;
    }
}