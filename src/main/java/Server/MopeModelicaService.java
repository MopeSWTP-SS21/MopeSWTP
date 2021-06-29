package Server;

import Server.Compiler.ICompilerAdapter;
import Server.Compiler.ModelicaDiagnostic;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.RequestMessage;
import org.eclipse.lsp4j.jsonrpc.messages.ResponseError;
import org.eclipse.lsp4j.jsonrpc.messages.ResponseMessage;

import java.util.concurrent.CompletableFuture;


public class MopeModelicaService implements ModelicaService {

    private ICompilerAdapter compiler;
    @Override
    public CompletableFuture<String> checkModel(String modelName){
        String result = compiler.checkModel(modelName);
        return CompletableFuture.supplyAsync(()->result);
    }

    @Override
    public CompletableFuture<String> loadModel(String modelName){
        String result = compiler.loadModel(modelName);
        return CompletableFuture.supplyAsync(()->result);
    }

    @Override
    public CompletableFuture<String> loadFile(String path){
        String result = compiler.loadFile(path);
        return CompletableFuture.supplyAsync(()->result);
    }

    @Override
    public CompletableFuture<String> getModelicaPath(){
        String result = compiler.getModelicaPath();
        return CompletableFuture.supplyAsync(()->result);
    }

    @Override
    public CompletableFuture<String> addModelicaPath(String path){
        String result = compiler.addFolderToModelicaPath(path);
        return CompletableFuture.supplyAsync(()->result);
    }

    @Override
    public CompletableFuture<String> getCompilerVersion(){
        String result = compiler.getCompilerVersion();
        return CompletableFuture.supplyAsync(()->result);
    }

    public MopeModelicaService(ICompilerAdapter comp){
        super();
        compiler = comp;
    }
}
