package com.bank.notification.listener;

import com.bank.common.event.TransactionCompletedEvent;
import com.bank.notification.entity.Notification;
import com.bank.notification.repository.NotificationRepository;
import com.bank.notification.service.EmailNotificationService;
import com.bank.user.dto.response.UserResponse;
import com.bank.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransactionEventListener {
    private final NotificationRepository notificationRepository;
    private final EmailNotificationService emailNotificationService;
    private final UserService userService;

    public TransactionEventListener(NotificationRepository notificationRepository,
                                    EmailNotificationService emailNotificationService,
                                    UserService userService){
        this.notificationRepository = notificationRepository;
        this.emailNotificationService = emailNotificationService;
        this.userService = userService;
    }

    @Async
    @EventListener
    public void handleTransactionCompleted(TransactionCompletedEvent event){
        try{
            UserResponse user = userService.getUserById(event.getInitiatedByUserId());

            String title = buildTitle(event);
            String message = buildMessage(event);

            Notification notification = new Notification();
            notification.setUserId(event.getInitiatedByUserId());
            notification.setTitle(title);
            notification.setType("TRANSACTION");
            notification.setMessage(message);
            notificationRepository.save(notification);

            emailNotificationService.sendTransactionEmail(user.getEmail(),title, message);
        } catch(Exception e){
            log.error("Failed to process notification for transaction {}: {}",
                    event.getTransactionReference(), e.getMessage());
        }
    }

    public String buildTitle(TransactionCompletedEvent event){
        return switch(event.getTransactionType()){
            case "DEPOSIT" -> "Deposit Successful";
            case "WITHDRAWAL" -> "Withdrawal Successful";
            case "TRANSFER" -> "Transfer " + (event.getStatus().equals("SUCCESS") ? "Successful" : event.getStatus());
            default -> "Transaction Update";
        };
    }

    private String buildMessage(TransactionCompletedEvent event) {
        return String.format("Your %s of amount %s (Ref: %s) is now %s.",
                event.getTransactionType(), event.getAmount(),
                event.getTransactionReference(), event.getStatus());
    }
}
