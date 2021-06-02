package Client;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MopeLSPClient implements IModelicaLanguageClient {

    private LanguageServer server;
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

    public void setServer(LanguageServer server){
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
            ExecuteCommandParams execute = new ExecuteCommandParams();
            execute.setCommand("CheckModel");
            execute.setArguments(List.of(modelName));
            CompletableFuture<Object> x = server.getWorkspaceService().executeCommand(execute);
            return x.get();
        }catch(Exception e){
            logger.error("Error CheckModel",e);
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
}
