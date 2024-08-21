package com.example.application.views.channel;

import com.example.application.chat.ChatService;
import com.example.application.chat.Message;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import reactor.core.Disposable;

import java.util.List;
import java.util.ArrayList;

@Route(value = "channel")
public class ChannelView extends VerticalLayout
        implements HasUrlParameter<String> {

    private final ChatService chatService;
    private final MessageList messageList;
    private String channelId;
    private final List<Message> receivedMessages = new ArrayList<Message>();

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
            event.forwardTo(LobbyView.class);
        } else {
            this.channelId = channelId;
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        var subscription = subscribe();
        addDetachListener(event -> subscription.dispose());
    }

    private void sendMessage(String message) {
        if (!message.isBlank()) {
            chatService.postMessage(channelId, message);
        }
    }

    private MessageListItem createMessageListItem(Message message) {
        var item = new MessageListItem(
                message.message(),
                message.timestamp(),
                message.author()
        );
        return item;
    }
    private void receiveMessages(List<Message> incoming) {
        getUI().ifPresent(ui -> ui.access(() -> {
            receivedMessages.addAll(incoming);
            messageList.setItems(receivedMessages.stream()
                    .map(this::createMessageListItem)
                    .toList());
        }));
    }

    private Disposable subscribe() {
        var subscription = chatService
                .liveMessages(channelId)
                .subscribe(this::receiveMessages);
        return subscription;
    }


}