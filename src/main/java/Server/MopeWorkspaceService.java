package Server;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.WorkspaceService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MopeWorkspaceService implements WorkspaceService {
    private final ModelicaService modelicaService;
    @Override
    public CompletableFuture<List<? extends SymbolInformation>> symbol(WorkspaceSymbolParams workspaceSymbolParams) {
        return null;
    }

    @Override
    public void didChangeConfiguration(DidChangeConfigurationParams didChangeConfigurationParams) {

    }

    @Override
    public void didChangeWatchedFiles(DidChangeWatchedFilesParams didChangeWatchedFilesParams) {

    }

    @Override
    public CompletableFuture<Object> executeCommand(ExecuteCommandParams params){
        String command = params.getCommand();
        List<Object> args = params.getArguments();
        CompletableFuture<Object> finalResult = new CompletableFuture<>();

        CompletableFuture<String> result = new CompletableFuture<>();
            String argument = "";
            if(!args.isEmpty()) argument = args.get(0).toString().replaceAll("\"", "");
            switch(command){
                case "ExecuteCommand":
                    result = modelicaService.sendExpression(argument);
                    break;
                case "LoadFile":
                    result = modelicaService.loadFile(argument);
                    break;
                case "CheckModel":
                    result = modelicaService.checkModel(argument);
                    break;
                case "AddPath":
                    result = modelicaService.addModelicaPath(argument);
                    break;
                case "GetPath":
                    result = modelicaService.getModelicaPath();
                    break;
                case "LoadModel":
                    result = modelicaService.loadModel(argument);
                    break;
                case "Version":
                    result = modelicaService.getCompilerVersion();
                    break;
            }

        try {
            finalResult.complete(result.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(finalResult);
    }
    public MopeWorkspaceService(ModelicaService service){
        super();
        modelicaService = service;
    }
}