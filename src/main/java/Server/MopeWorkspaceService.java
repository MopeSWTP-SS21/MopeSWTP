package Server;

import omc.ZeroMQClient;
import omc.corba.OMCInterface;
import omc.corba.Result;
import omc.corba.ScriptingHelper;
import omc.ior.ZMQPortFileProvider;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.WorkspaceService;
import version.Version;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class MopeWorkspaceService implements WorkspaceService {
    private OMCInterface omc;
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
               result = this.checkModel((String) args.get(0).toString());
               break;
            case "Version":
                result = this.getCompilerVersion();
                break;
        }


        String finalResult = result;
        return CompletableFuture.supplyAsync(() -> {
            return finalResult;
        });
    }
    private String checkModel(String modelName){
        //TODO
        //String result = omc.checkModel(modelName);
        Result result = omc.sendExpression("model abc Real x=1; end abc;");
        Optional<String> name = ScriptingHelper.getModelName("/home/swtp/modelica/exampleModels/example.mo");
        String result2 = omc.checkModel(name.orElse("abc"));
        return "Model " + modelName + " checked\n" + "->" + result2;
    }
    private String getCompilerVersion(){
        Version v = omc.getVersion();
        return v.toString();
    }


    public void InitOMC(OMCInterface omcClient){

        try{
            System.out.println("Init OMC");
            omc = omcClient;
            omc.connect();
            System.out.println("OMC connected to TestWorkspaceService");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}