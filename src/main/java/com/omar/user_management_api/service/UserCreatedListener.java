package com.omar.user_management_api.service;

import com.omar.user_management_api.service.UserServiceImpl.UserCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserCreatedListener {
    private static final Logger log = LoggerFactory.getLogger(UserCreatedListener.class);

    @EventListener
    public void onUserCreated(UserCreatedEvent event) {
        log.info("User created with id {}", event.userId());
    }
}


