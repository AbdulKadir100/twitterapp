package com.kadir.abdul.Twitter_App.service.Imlp;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubscriberProducerRepository producerRepository;

    private final Logger LOG = LoggerFactory.getLogger(MessageServiceImpl.class);

    public MessageServiceImpl(MessageRepository messageRepository, UserRepository userRepository,
            SubscriberProducerRepository producerRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.producerRepository = producerRepository;
    }

    @Async
    public CompletableFuture<User> validateUserExists(Long userId) {

        Optional<User> opUser = userRepository.findById(userId);

        if (!opUser.isPresent()) {
            LOG.info("User not found"+ opUser.get());
        }

        return CompletableFuture.supplyAsync(() -> userRepository.findById(userId))
                .thenApply(optionalUser -> {
                    if (optionalUser.isPresent()) {
                        return optionalUser.get();
                    } else {
                        throw new RuntimeException("User with ID " + userId + " not found");
                    }
                });
    }

    @Async
    private CompletableFuture<User> optionalToCompletableFuture(Optional<User> optionalUser) {
        return optionalUser
                .map(CompletableFuture::completedFuture) // If present, wrap the User in a CompletableFuture
                .orElseGet(() -> CompletableFuture.failedFuture(new RuntimeException("User not found")));
    }

    /**
     * Processes a Future of messages to create a list of MessageResponses.
     * Each message is mapped to a UserResponse and then used to build a
     * MessageResponse.
     * The list of MessageResponses is wrapped in a ResponseEntity with appropriate
     * status and message.
     *
     * @param List<Messages> A list of Message objects.
     * @return A CompletableFuture of ResponseEntity containing a list of
     *         MessageResponses.
     */
    @Async
    private CompletableFuture<ResponseEntity<ApiResponse<List<MessageResponse>>>> producerOutcome(
            List<Message> messages) {

        // Convert each Message to a CompletableFuture<MessageResponse>
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

        // Combine all futures into one future that holds a list of MessageResponse
        CompletableFuture<List<MessageResponse>> combinedFuture = CompletableFuture.allOf(
                futureList.toArray(new CompletableFuture[0]))
                .thenApply(v -> futureList.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()));

        return combinedFuture.thenApply(messageResponses -> ResponseEntity
                .ok(new ApiResponse<>(MessageUtil.SUCCESS, HttpStatus.OK.value(), messageResponses)));
    }

    /**
     * Retrieves messages associated with a specific user ID after validating its
     * existence.
     * This method combines the validation and message retrieval steps.
     *
     * @param userId The ID of the user to retrieve messages for.
     * @return A CompletableFuture of ResponseEntity containing a list of
     *         MessageResponses.
     */
    @Async
    @Override
    public CompletableFuture<ResponseEntity<ApiResponse<List<MessageResponse>>>> getUserMessages(Long userId) {
        return validateUserExists(userId)
                .thenCompose(user -> messageRepository.listMessageByProducerId(userId))
                .thenCompose(message -> producerOutcome(message));
    }

    /**
     * Retrieves messages associated with a subscriber after validating the user's
     * existence.
     * This method combines the validation and message retrieval steps.
     *
     * @param userId The ID of the subscriber to retrieve messages for.
     * @return A Future of ResponseEntity containing a list of MessageResponses.
     */
    @Async
    @Override
    public CompletableFuture<ResponseEntity<ApiResponse<List<MessageResponse>>>> getMessageBySubscriberId(
            Long subscriberId) {
        return validateUserExists(subscriberId) // CompletableFuture<User>
                .thenCompose(user -> producerRepository.listProducerBySubscriber(subscriberId) // CompletableFuture<List<Integer>>
                        .thenCompose(producerIds -> messageRepository.listMessageByProducerIdIn(producerIds) // CompletableFuture<List<Message>>
                                .thenCompose(messages -> producerOutcome(messages)) // CompletableFuture<ResponseEntity<ApiResponse<List<MessageResponse>>>>
                                .exceptionally(ex -> handleFailure(ex))));
    }

    private ResponseEntity<ApiResponse<List<MessageResponse>>> handleFailure(Throwable ex) {
        ApiResponse<List<MessageResponse>> errorResponse = new ApiResponse<>(HttpStatus.EXPECTATION_FAILED.toString(),
                HttpStatus.EXPECTATION_FAILED.value(), null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Publishes a message for a specific user after validating the user's
     * existence.
     *
     * @param request The PublishMessageRequest containing user ID and message
     *                content.
     * @return A Future of ResponseEntity containing a success message.
     */
    @Async
    @Override
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> publishMessage(PublishMesssageRequest request) {
        Long userId = request.getUserId();
        String message = request.getMessage();

        return validateUserExists(userId)
                .thenCompose(user -> CompletableFuture.supplyAsync(() -> messageRepository.save(
                        Message.builder()
                                .contents(message)
                                .uid(userId)
                                .build())))
                .thenApply(savedMessage -> ResponseEntity.ok(
                        new ApiResponse<>(MessageUtil.SUCCESS, HttpStatus.OK.value(),
                                "Message published successfully")));
    }

}
