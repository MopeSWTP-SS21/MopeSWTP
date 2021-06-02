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

        //This Part is for debugging purpose
        System.out.println("Argument:");
        for (Object arg: args) {

            System.out.println(arg);
            System.out.println(arg.toString());
        }

        String result = "Cannot execute Command " + command + "!";

        switch(command){
            case "LoadFile":
                result = compiler.loadFile(args.get(0).toString().replaceAll("\"", ""));
                break;
            case "CheckModel":
               result = compiler.checkModel(args.get(0).toString().replaceAll("\"", ""));
               break;
            case "Version":
                result = compiler.getCompilerVersion();
                break;
        }


        String finalResult = result;
        return CompletableFuture.supplyAsync(() -> finalResult);
    }



    public void InitOMC(ICompilerAdapter compiler){
        this.compiler = compiler;
        compiler.connect();
    }
}