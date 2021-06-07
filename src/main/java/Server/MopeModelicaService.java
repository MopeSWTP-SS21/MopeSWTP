package Server;

import Server.Compiler.ICompilerAdapter;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.WorkspaceSymbolParams;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.jsonrpc.services.JsonSegment;

import java.util.List;
import java.util.concurrent.CompletableFuture;


public class MopeModelicaService implements ModelicaService {

    private ICompilerAdapter compiler;
    @Override
    public CompletableFuture<String> checkModel(String modelName){
        String result = compiler.checkModel(modelName);//args.get(0).toString().replaceAll("\"", ""));
        return CompletableFuture.supplyAsync(()->result);
    }

    @Override
    public CompletableFuture<String> loadModel(String modelName){
        String result = compiler.loadModel(modelName);//args.get(0).toString().replaceAll("\"", ""));
        return CompletableFuture.supplyAsync(()->result);
    }

    @Override
    public CompletableFuture<String> loadFile(String path){
        String result = compiler.loadFile(path);//args.get(0).toString().replaceAll("\"", ""));
        return CompletableFuture.supplyAsync(()->result);
    }

    @Override
    public CompletableFuture<String> getModelicaPath(){
        String result = compiler.getModelicaPath();
        return CompletableFuture.supplyAsync(()->result);
    }

    @Override
    public CompletableFuture<String> addModelicaPath(String path){
        String result = compiler.addFolderToModelicaPath(path);//args.get(0).toString().replaceAll("\"", ""));
        return CompletableFuture.supplyAsync(()->result);
    }

    @Override
    public CompletableFuture<String> getCompilerVersion(){
        String result = compiler.getCompilerVersion();
        return CompletableFuture.supplyAsync(()->result);
    }

    public void InitOMC(ICompilerAdapter compiler){
        this.compiler = compiler;
        compiler.connect();
    }
}
