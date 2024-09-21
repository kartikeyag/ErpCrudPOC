package org.springframework.samples.erpcrud.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatForm {
    private String username;
    private String messageText;
    private String messageType;
    private String conversationId;
}
