package com.Projects.Chat.config;


import com.Projects.Chat.Chat.ChatMessage;
import com.Projects.Chat.Chat.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    //injecting a dependency for indicating other users that a user has left the chat
    private final SimpMessagingTemplate messagingTemplate;


    //we are making this event listener in order to indicate the users when they are disconected with the server or getting out of the
    //chat
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event)
    {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) sha.getSessionAttributes().get("username");
        if (username != null) {

            log.info("User " + username + " disconnected");
            var chatMessage = ChatMessage
                    .builder().type(MessageType.LEAVE ).build();

            messagingTemplate.convertAndSend("/topic/public" + username, chatMessage);
            //to inform all the participants or users in the app that a user has left the chat
            //we need to implement a dependency
        }
    }
}
