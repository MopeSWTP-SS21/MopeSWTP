package Client;

import Server.ModelicaLanguageServer;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MopeLSPClient implements IModelicaLanguageClient {

    private ModelicaLanguageServer server;
    private static final Logger logger = LoggerFactory.getLogger(MopeLSPClient.class);

    @Override
    public void telemetryEvent(Object object) {
        logger.info("Client->telemtryEvent");
    }

    @Override
    public void publishDiagnostics(PublishDiagnosticsParams diagnostics) {
        logger.info("Client->publishDiagnostics");
    }

    @Override
    public void showMessage(MessageParams messageParams) {
        logger.info("Client->showMessage");
        logger.info(messageParams.toString());
    }

    @Override
    public CompletableFuture<MessageActionItem> showMessageRequest(ShowMessageRequestParams requestParams) {
        logger.info("Client->showMessageRequest");
        return null;
    }

    @Override
    public void logMessage(MessageParams message) {
        logger.info("Client->logMessage");
        logger.info(message.toString());
    }

    public void setServer(ModelicaLanguageServer server){
        logger.info("Client->setServer");
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
            /*ExecuteCommandParams execute = new ExecuteCommandParams();
            execute.setCommand("CheckModel");
            execute.setArguments(List.of(modelName));
            System.out.println(modelName);
            System.out.println(execute.getArguments());
            CompletableFuture<Object> x = server.getWorkspaceService().executeCommand(execute);*/
            CompletableFuture<String> x = server.getModelicaService().checkModel(modelName);
            return x.get();
        }catch(Exception e){
            logger.error("Error CheckModel",e);
            e.printStackTrace();
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

    public Object addPath(String path){
        try{
            ExecuteCommandParams execute = new ExecuteCommandParams();
            execute.setCommand("AddPath");
            execute.setArguments(List.of(path));
            CompletableFuture<Object> x = server.getWorkspaceService().executeCommand(execute);
            return x.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object loadModel(String name){
        try{
            ExecuteCommandParams execute = new ExecuteCommandParams();
            execute.setCommand("LoadModel");
            execute.setArguments(List.of(name));
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
            logger.error("Error RequestVersion",e);
            e.printStackTrace();
        }
        return null;
    }

    public Object modelicaPath()  {
        try{
            ExecuteCommandParams execute = new ExecuteCommandParams();
            execute.setCommand("GetPath");
            CompletableFuture<Object> x = server.getWorkspaceService().executeCommand(execute);
            return x.get();
        }catch(Exception e){
            System.out.println("Error Get Path");
        }
        return null;
    }
}
