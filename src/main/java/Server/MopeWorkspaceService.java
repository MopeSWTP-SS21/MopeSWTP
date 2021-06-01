package Server;

import Server.Compiler.ICompilerAdapter;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.WorkspaceService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MopeWorkspaceService implements WorkspaceService {
    private ICompilerAdapter compiler;
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

        switch(command){
            case "CheckModel":
               result = compiler.checkModel(args.get(0).toString());
               break;
            case "Version":
                result = compiler.getCompilerVersion();
                break;
        }


        String finalResult = result;
        return CompletableFuture.supplyAsync(() -> {
            return finalResult;
        });
    }



    public void InitOMC(ICompilerAdapter compiler){
        this.compiler = compiler;
        compiler.connect();
    }
}