package Client;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MopeLSPClient implements IModelicaLanguageClient {

    private LanguageServer server;

    @Override
    public void telemetryEvent(Object object) {
        System.out.println("Client->telemetryEvent");
    }

    @Override
    public void publishDiagnostics(PublishDiagnosticsParams diagnostics) {
        System.out.println("Client->publishDiagnostics");
    }

    @Override
    public void showMessage(MessageParams messageParams) {
        System.out.println("Client->showMessage");
        System.out.println(messageParams.toString());
    }

    @Override
    public CompletableFuture<MessageActionItem> showMessageRequest(ShowMessageRequestParams requestParams) {
        System.out.println("Client->showMessageRequest");
        return null;
    }

    @Override
    public void logMessage(MessageParams message) {
        System.out.println("Client->logMessage");
        System.out.println(message.toString());
    }

    public void setServer(LanguageServer server){
        System.out.println("Client->setServer");
        this.server = server;
    }
    public void initServer(){
        InitializeParams params = new InitializeParams();
        server.initialize(params);
    }

    public String getCompletion(String comop) throws ExecutionException, InterruptedException {
        CompletionParams params = new CompletionParams();
        CompletableFuture<?> completion = server.getTextDocumentService().completion(params);
        var compGet = completion.get() ;
        return compGet.toString();
    }

    public String hover() throws ExecutionException, InterruptedException {
        HoverParams params = new HoverParams();
        params.setTextDocument(new TextDocumentIdentifier("file:///folder/name.mo") );
        params.setPosition(new Position(2,12));
        CompletableFuture<?> hover = server.getTextDocumentService().hover(params);
        var hoverGet = hover.get();
        return hoverGet.toString();
    }

    public void didOpenFile(String path){
        TextDocumentItem item = new TextDocumentItem();
        item.setText("Hallo, ...");
        DidOpenTextDocumentParams params = new DidOpenTextDocumentParams();
        params.setTextDocument(item);
        server.getTextDocumentService().didOpen(params);
    }

    public Object checkModel(String modelName)  {
        try{
            ExecuteCommandParams execute = new ExecuteCommandParams();
            execute.setCommand("CheckModel");
            execute.setArguments(List.of(modelName));
            System.out.println(modelName);
            System.out.println(execute.getArguments());
            CompletableFuture<Object> x = server.getWorkspaceService().executeCommand(execute);
            return x.get();
        }catch(Exception e){
            System.out.println("Error CheckModel");
        }
        return null;
    }

    public Object loadFile(String path){
        try{
            ExecuteCommandParams execute = new ExecuteCommandParams();
            execute.setCommand("LoadFile");
            execute.setArguments(List.of(path));
            CompletableFuture<Object> x = server.getWorkspaceService().executeCommand(execute);
            return x.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object compilerVersion()  {
        try{
            ExecuteCommandParams execute = new ExecuteCommandParams();
            execute.setCommand("Version");
            CompletableFuture<Object> x = server.getWorkspaceService().executeCommand(execute);
            return x.get();
        }catch(Exception e){
            System.out.println("Error RequestVersion");
        }
        return null;
    }
}
