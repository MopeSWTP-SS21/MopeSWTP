package Server;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.WorkspaceService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static Server.MopeLSPServerLauncher.logger;

public class MopeWorkspaceService implements WorkspaceService {

    public String error = "invalid command";
    private final ModelicaService modelicaService;
    private UnsupportedOperationException unsupOpEx = null;

    @Override
    public CompletableFuture<List<? extends SymbolInformation>> symbol(WorkspaceSymbolParams workspaceSymbolParams) {
        return null;
    }

    @Override
    public void didChangeConfiguration(DidChangeConfigurationParams didChangeConfigurationParams) {
        // not yet implemented
        throw unsupOpEx;
    }

    @Override
    public void didChangeWatchedFiles(DidChangeWatchedFilesParams didChangeWatchedFilesParams) {
        //not yet implemented
        throw unsupOpEx;
    }

    @Override
    public CompletableFuture<Object> executeCommand(ExecuteCommandParams params){
        String command = params.getCommand();
        List<Object> args = params.getArguments();
        CompletableFuture<String> result = CompletableFuture.completedFuture(null);
            String argument = "";
            if(!args.isEmpty()) argument = args.get(0).toString().replaceAll("\"", "");
            switch(command){
                case "executeCommand":
                    result = modelicaService.sendExpression(argument);
                    break;
                case "loadFile":
                    result = modelicaService.loadFile(argument);
                    break;
                case "checkModel":
                    result = modelicaService.checkModel(argument);
                    break;
                case "addPath":
                    result = modelicaService.addModelicaPath(argument);
                    break;
                case "getPath":
                    result = modelicaService.getModelicaPath();
                    break;
                case "loadModel":
                    result = modelicaService.loadModel(argument);
                    break;
                case "version":
                    result = modelicaService.getCompilerVersion();
                    break;
                default:
                    result = CompletableFuture.completedFuture(error);
                    logger.error("Command is invalid, output is: " + result);
                    break;
            }
        // transform the result from CompletableFuture<String> to CompletableFuture<Object>
        return result.thenApply(x -> x);
    }
    public MopeWorkspaceService(ModelicaService service){
        super();
        modelicaService = service;
    }
}