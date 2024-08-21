package com.example.application.views.channel;

import com.example.application.chat.ChatService;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

@Route(value = "channel")
public class ChannelView extends VerticalLayout
        implements HasUrlParameter<String> {

    private final ChatService chatService;
    private final MessageList messageList;
    private String channelId;

    public ChannelView(ChatService chatService) {
        this.chatService = chatService;
        setSizeFull();

        messageList = new MessageList();
        messageList.setSizeFull();
        add(messageList);

        var messageInput = new MessageInput(event -> sendMessage(event.getValue()));
        messageInput.setWidthFull();
        add(messageInput);

    }

    @Override
    public void setParameter(BeforeEvent event, String channelId) {
        if (chatService.channel(channelId).isEmpty()) {
            throw new IllegalArgumentException("Invalid channel ID");
        }
        this.channelId = channelId;
    }



    private void sendMessage(String message) {
        if (!message.isBlank()) {
            chatService.postMessage(channelId, message);
        }
    }


}