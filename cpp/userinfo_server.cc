/*
 *
 * Copyright 2015 gRPC authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

#include <iostream>
#include <memory>
#include <string>
#include <time.h>
#include <sstream>
#include <mutex>
#include <unordered_map>

#include <grpcpp/ext/proto_server_reflection_plugin.h>
#include <grpcpp/grpcpp.h>
#include <grpcpp/health_check_service_interface.h>

#ifdef BAZEL_BUILD
#include "examples/protos/userinfo.grpc.pb.h"
#else
#include "userinfo.grpc.pb.h"
#endif

using grpc::Server;
using grpc::ServerBuilder;
using grpc::ServerContext;
using grpc::Status;
using userinfo::UserInfo;
using userinfo::UserInfoReply;
using userinfo::UserInfoRequest;

// Internal data representation of UserInfo.
class UserData {
private:   
    std::string m_firstName;
    std::string m_lastName;
    std::string m_dob;
    std::string m_email;
    std::string m_phoneNumber;

public:
    UserData(const UserInfoRequest* userInfoRequest) : 
        m_firstName(userInfoRequest->firstname()),
        m_lastName(userInfoRequest->lastname()),
        m_dob(userInfoRequest->dob()),
        m_email(userInfoRequest->email()),
        m_phoneNumber(userInfoRequest->phonenumber()) {
    }
};

// Logic and data behind the server's behavior.
class UserInfoServiceImpl final : public UserInfo::Service {
private:
  std::mutex m_updateMutex;
  std::unordered_map< uint64_t, std::shared_ptr<UserData> > m_userIdToInfoMap;

public:
  Status UpdateUserInfo(ServerContext* context, const UserInfoRequest* request,
      UserInfoReply* reply) override {
    std::ostringstream oss;
    const int bufferlen = 20;
    char buffer[bufferlen];
    struct tm timeinfo;
    std::time_t secsSinceEpoch = request->timestamp();
    localtime_s(&timeinfo, &secsSinceEpoch);
    std::strftime(buffer, bufferlen, "%C%y/%m/%d %H:%M:%S", &timeinfo);
    oss << buffer << ": Processed request #" << request->traceid() << " for user ID " << request->userid();

    std::lock_guard<std::mutex> lock(m_updateMutex);
    m_userIdToInfoMap[request->userid()] = std::make_shared<UserData>(request);
    std::cout << oss.str() << std::endl;

    return Status::OK;
  }
};

void RunServer() {
  std::string server_address("0.0.0.0:50051");
  UserInfoServiceImpl service;

  grpc::EnableDefaultHealthCheckService(true);
  grpc::reflection::InitProtoReflectionServerBuilderPlugin();
  ServerBuilder builder;
  // Listen on the given address without any authentication mechanism.
  builder.AddListeningPort(server_address, grpc::InsecureServerCredentials());
  // Register "service" as the instance through which we'll communicate with
  // clients. In this case it corresponds to an *synchronous* service.
  builder.RegisterService(&service);
  // Finally assemble the server.
  std::unique_ptr<Server> server(builder.BuildAndStart());
  std::cout << "Server listening on " << server_address << std::endl;

  // Wait for the server to shutdown. Note that some other thread must be
  // responsible for shutting down the server for this call to ever return.
  server->Wait();
}

int main(int argc, char** argv) {
  RunServer();

  return 0;
}
