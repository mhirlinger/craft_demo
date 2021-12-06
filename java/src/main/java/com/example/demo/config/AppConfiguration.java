package com.example.demo.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.example.demo.UserInfoGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {
    private final String grpcServerTarget = "localhost:50051";

    @Bean
    public UserInfoGrpc.UserInfoBlockingStub userInfoService() {
        System.out.println("Creating connection: " + grpcServerTarget);
        ManagedChannel channel = ManagedChannelBuilder.forTarget(grpcServerTarget).usePlaintext().build();
        return UserInfoGrpc.newBlockingStub(channel);
    }
}