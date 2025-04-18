package com.ptitB22CN539.LaptopShop.WebSocket.Controller;

import com.ptitB22CN539.LaptopShop.WebSocket.MessageEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    @MessageMapping(value = "/send")
    @SendTo(value = "/topic/messages")
    public MessageEntity sendMessage(@Payload MessageEntity message) {
        return message;
    }
}
