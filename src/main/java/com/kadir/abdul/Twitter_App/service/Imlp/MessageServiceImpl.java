package com.kadir.abdul.Twitter_App.service.Imlp;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.kadir.abdul.Twitter_App.dto.MessageResponse;
import com.kadir.abdul.Twitter_App.dto.PublishMesssageRequest;
import com.kadir.abdul.Twitter_App.dto.UserResponse;
import com.kadir.abdul.Twitter_App.entity.Message;
import com.kadir.abdul.Twitter_App.entity.User;
import com.kadir.abdul.Twitter_App.repository.MessageRepository;
import com.kadir.abdul.Twitter_App.repository.SubscriberProducerRepository;
import com.kadir.abdul.Twitter_App.repository.UserRepository;
import com.kadir.abdul.Twitter_App.response.ApiResponse;
import com.kadir.abdul.Twitter_App.service.MessageService;
import com.kadir.abdul.Twitter_App.utils.MessageUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private MessageRepository messageRepository;
    private UserRepository userRepository;
    private SubscriberProducerRepository producerRepository;


    private CompletableFuture<User> validateUserExists(Long userId) {
        return userRepository.findById(userId)
                .thenCompose(optionalUser -> {
                    if (optionalUser.isPresent()) {
                        return CompletableFuture.completedFuture(optionalUser.get());
                    } else {
                        return CompletableFuture.failedFuture(
                            new RecordNotFoundException("User with ID " + userId + " not found")
                        );
                    }
                });
    }
    
    @Async
    private CompletableFuture<User> optionalToCompletableFuture(Optional<User> optionalUser) {
        return optionalUser
                .map(CompletableFuture::completedFuture) // If present, wrap the User in a CompletableFuture
                .orElseGet(() -> CompletableFuture.failedFuture(new RuntimeException("User not found"))); 
    }

    @Async
    private CompletableFuture<ResponseEntity<ApiResponse<List<MessageResponse>>>> producerOutcome(
            CompletableFuture<List<Message>> messageFuture) {
        return messageFuture.thenCompose(messages -> {

            List<CompletableFuture<MessageResponse>> futureList = messages.stream()
                    .map(message -> {

                        Optional<User> userFuture = userRepository.findById(message.getUid());

                        CompletableFuture<User> userCompletableFuture = optionalToCompletableFuture(userFuture);

                        return userCompletableFuture.thenApply(user -> {
                            UserResponse userResponse = UserResponse.builder()
                                    .uid(user.getUid())
                                    .uName(user.getUName())
                                    .build();
                            return MessageResponse.builder()
                                    .contents(message.getContents())
                                    .mid(message.getMid())
                                    .postedBy(userResponse)
                                    .build();
                        });
                    })
                    .collect(Collectors.toList());

            CompletableFuture<List<MessageResponse>> combinedFuture = CompletableFuture.allOf(
                    futureList.toArray(new CompletableFuture[0]))
                    .thenApply(v -> futureList.stream()
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList()));

            return combinedFuture.thenApply(messageResponses -> ResponseEntity
                    .ok(new ApiResponse<>(MessageUtil.SUCCESS, HttpStatus.OK.value(), messageResponses)));
        });
    }

    @Async
    @Override
    public CompletableFuture<ResponseEntity<ApiResponse<List<MessageResponse>>>> getUserMessages(Long userId) {
        return null;
    }

    @Async
    @Override
    public CompletableFuture<ResponseEntity<ApiResponse<List<MessageResponse>>>> getMessageBySubscriberId(
            Long subscriberId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMessageBySubscriberId'");
    }

    @Async
    @Override
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> publishMessage(PublishMesssageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'publishMessage'");
    }

}
