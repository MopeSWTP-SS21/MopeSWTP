package Server;

import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.jsonrpc.services.JsonSegment;

import java.util.concurrent.CompletableFuture;

@JsonSegment("modelica")
public interface ModelicaService {
    @JsonRequest
    default CompletableFuture<String> checkModel(String modelName){
        throw new UnsupportedOperationException();
    }
    @JsonRequest
    default CompletableFuture<String> loadModel(String modelName){
        throw new UnsupportedOperationException();
    }

    @JsonRequest
    CompletableFuture<String> sendExpression(String command);

    @JsonRequest
    default CompletableFuture<String> loadFile(String path){
        throw new UnsupportedOperationException();
    }
    @JsonRequest
    default CompletableFuture<String> initializeModel(String modelName){
        throw new UnsupportedOperationException();
    }
    @JsonRequest
    CompletableFuture<String> getDocumentation(String className);
    @JsonRequest
    default CompletableFuture<String> getModelicaPath(){
        throw new UnsupportedOperationException();
    }
    @JsonRequest
    default CompletableFuture<String> addModelicaPath(String path){
        throw new UnsupportedOperationException();
    }
    @JsonRequest
    default CompletableFuture<String> getCompilerVersion(){
        throw new UnsupportedOperationException();
    }
}
