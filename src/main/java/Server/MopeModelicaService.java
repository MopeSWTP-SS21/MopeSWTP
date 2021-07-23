package Server;

import Server.Compiler.ICompilerAdapter;
import Server.Compiler.ModelicaDiagnostic;
import omc.corba.Result;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;


public class MopeModelicaService implements ModelicaService {

    private final ICompilerAdapter compiler;
    private final MopeLSPServer server;
    @Override
    public CompletableFuture<String> checkModel(String modelName){
        server.getDiagnosticHandler().clearDiagnostics();
        String result = compiler.checkModel(modelName);
        server.getDiagnosticHandler().addDiagnostics(ModelicaDiagnostic.CreateDiagnostics(result));
        return CompletableFuture.supplyAsync(()->result);
    }

    @Override
    public CompletableFuture<String> loadModel(String modelName){
        server.getDiagnosticHandler().clearDiagnostics();
        ArrayList<ModelicaDiagnostic> diagnostics= new ArrayList();
        Result result = compiler.loadModel(modelName);
        diagnostics.addAll(
                ModelicaDiagnostic.CreateDiagnostics(result.toString())
        );
        Result loaded = compiler.existClass(modelName);
        if(!Boolean.parseBoolean(loaded.result)){
            diagnostics.addAll(
                    ModelicaDiagnostic.CreateModelNotLoadedDiagnostic(modelName, Boolean.parseBoolean(result.result))
            );
        }
        server.getDiagnosticHandler().addDiagnostics(diagnostics);
        return CompletableFuture.supplyAsync(result::toString);
    }

    @Override
    public CompletableFuture<String> sendExpression(String command){
        Result result = compiler.sendExpression(command);
        if(result.error.isPresent()) return CompletableFuture.completedFuture(result.error.get());
        return CompletableFuture.completedFuture(result.result);
    }
    @Override
    public CompletableFuture<String> loadFile(String path){
        server.getDiagnosticHandler().clearDiagnostics();
        String result = compiler.loadFile(path);
        server.getDiagnosticHandler().addDiagnostics(ModelicaDiagnostic.CreateDiagnostics(result));
        return CompletableFuture.supplyAsync(()->result);
    }

    @Override
    public CompletableFuture<String> getDocumentation(String className){
        String result = compiler.getDocumentation(className);
        return CompletableFuture.supplyAsync(() -> result);
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

    public MopeModelicaService(ICompilerAdapter comp, MopeLSPServer server){
        super();
        this.server = server;
        compiler = comp;
    }
}
