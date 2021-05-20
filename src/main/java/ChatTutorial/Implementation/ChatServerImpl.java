package ChatTutorial.Implementation;

import ChatTutorial.Interfaces.ChatClient;
import ChatTutorial.Interfaces.ChatServer;
import ChatTutorial.UserMessage;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServerImpl implements ChatServer {

    private final List<UserMessage> messages = new CopyOnWriteArrayList<>();
    private final List<ChatClient> clients = new CopyOnWriteArrayList<>();

    /**
     * Return existing messages.
     */
    @Override
    public CompletableFuture<List<UserMessage>> fetchMessages() {
        return CompletableFuture.completedFuture(messages);
    }

    /**
     * Store the message posted by the chat client
     * and broadcast it to all clients.
     */
    @Override
    public void postMessage(UserMessage message) {
        messages.add(message);
        for (ChatClient client : clients) {
            client.didPostMessage(message);
        }
    }

    /**
     * Connect the given chat client.
     * Return a runnable which should be executed to disconnect the client.
     */
    public Runnable addClient(ChatClient client) {
        this.clients.add(client);
        return () -> this.clients.remove(client);
    }

}
