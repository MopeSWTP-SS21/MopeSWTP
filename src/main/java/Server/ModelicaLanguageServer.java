package Server;

import org.eclipse.lsp4j.jsonrpc.services.JsonDelegate;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;

public interface ModelicaLanguageServer extends LanguageServer, LanguageClientAware {
    @JsonDelegate
    ModelicaService getModelicaService();
}
