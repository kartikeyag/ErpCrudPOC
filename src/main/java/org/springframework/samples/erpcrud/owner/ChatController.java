package org.springframework.samples.erpcrud.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.erpcrud.model.ChatForm;
import org.springframework.samples.erpcrud.service.ChatGeneratorService;
import org.springframework.samples.erpcrud.service.MessageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.UUID;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final MessageService messageService;
    private final ChatGeneratorService chatGeneratorService;

    @GetMapping
    public String getChatPage(ChatForm chatForm, Model model) {
        model.addAttribute("chatMessages", this.messageService.getChatMessages());
        return "chat/chat";
    }

    @PostMapping
    public String postChatMessage(ChatForm chatForm, Model model, Principal principal) {
        String conversationId = getConversationId(chatForm);
        if(principal.getName()!=null)
        chatForm.setUsername(principal.getName());
        this.messageService.addMessage(chatForm);
        String chatResponse = chatGeneratorService.generate(chatForm.getMessageText(),conversationId);
        chatForm.setUsername("Agent");
        chatForm.setMessageText(chatResponse);
        chatForm.setConversationId(conversationId);
        this.messageService.addMessage(chatForm);
        chatForm.setMessageText("");
        chatForm.setConversationId(conversationId);
        model.addAttribute("chatMessages", this.messageService.getChatMessages());
        return "redirect:/chat";
    }
    public String getConversationId(ChatForm chatForm){
        if(StringUtils.isEmpty(chatForm.getConversationId())){
            return UUID.randomUUID().toString();
        }
        return  chatForm.getConversationId();
    }

    @ModelAttribute("allMessageTypes")
    public String[] allMessageTypes () {
        return new String[] { "Say", "Shout", "Whisper" };
    }

}
