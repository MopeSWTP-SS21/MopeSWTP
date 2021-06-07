package Server;

import org.eclipse.lsp4j.jsonrpc.services.JsonDelegate;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;

import java.util.concurrent.CompletableFuture;

public interface ModelicaLanguageServer extends LanguageServer, LanguageClientAware {
    @JsonDelegate
    ModelicaService getModelicaService();
}
