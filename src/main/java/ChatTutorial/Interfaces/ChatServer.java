package ChatTutorial.Interfaces;

import ChatTutorial.UserMessage;
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.jsonrpc.services.JsonSegment;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@JsonSegment("server")
public interface ChatServer {

    /**
     * The `server/fetchMessage` request is sent by the client to fetch messages posted so far.
     */
    @JsonRequest
    CompletableFuture<List<UserMessage>> fetchMessages();

    /**
     * The `server/postMessage` notification is sent by the client to post a new message.
     * The server should store a message and broadcast it to all clients.
     */
    @JsonNotification
    void postMessage(UserMessage message);

}