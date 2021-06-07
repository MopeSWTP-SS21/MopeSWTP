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
    public CompletableFuture<String> checkModel(String modelname){
        String result = compiler.checkModel(modelname);//args.get(0).toString().replaceAll("\"", ""));
        return CompletableFuture.supplyAsync(()->result);
    }

    public void InitOMC(ICompilerAdapter compiler){
        this.compiler = compiler;
        compiler.connect();
    }
}
