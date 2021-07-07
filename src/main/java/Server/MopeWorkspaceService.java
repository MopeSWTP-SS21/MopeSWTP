package Server;

import Server.Compiler.ICompilerAdapter;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.WorkspaceService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MopeWorkspaceService implements WorkspaceService {
    private ModelicaService modelicaService;
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

        String result = "Cannot execute Command " + command + "!";
        try{

            switch(command){
                case "LoadFile":
                    result = modelicaService.loadFile(args.get(0).toString().replaceAll("\"", "")).get() ;
                    break;
                case "CheckModel":
                    result = modelicaService.checkModel(args.get(0).toString().replaceAll("\"", "")).get();
                    break;
                case "AddPath":
                    result = modelicaService.addModelicaPath(args.get(0).toString().replaceAll("\"", "")).get();
                    break;
                case "GetPath":
                    result = modelicaService.getModelicaPath().get();
                    break;
                case "LoadModel":
                    result = modelicaService.loadModel(args.get(0).toString().replaceAll("\"", "")).get();
                    break;
                case "Version":
                    result = modelicaService.getCompilerVersion().get();
                    break;
                case "known":
                    result = "This command is known... ";
                    break;
            }
        } catch(InterruptedException | ExecutionException e){
            e.printStackTrace();
        }


        String finalResult = result;
        return CompletableFuture.supplyAsync(() -> finalResult);
    }
    public MopeWorkspaceService(ModelicaService service){
        super();
        modelicaService = service;
    }
}