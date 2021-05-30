package Server;

import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;

import java.util.concurrent.CompletableFuture;

public interface TestModelicaServer extends LanguageServer, LanguageClientAware {
    @JsonRequest
    default CompletableFuture<String> checkModel(String filename){
        throw new UnsupportedOperationException();
    }
}
