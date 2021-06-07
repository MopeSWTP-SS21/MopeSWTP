package Server;

import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.jsonrpc.services.JsonSegment;

import java.util.concurrent.CompletableFuture;

@JsonSegment("modelica")
public interface ModelicaService {
    @JsonRequest
    default CompletableFuture<String> checkModel(String modelname){
        throw new UnsupportedOperationException();
    }
}
