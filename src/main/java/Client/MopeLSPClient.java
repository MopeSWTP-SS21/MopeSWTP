package Client;

import Server.ModelicaLanguageServer;
import org.eclipse.lsp4j.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MopeLSPClient implements IModelicaLanguageClient {
    //TODO use proper ReturnTypes
    private ModelicaLanguageServer server;
    private static final Logger logger = LoggerFactory.getLogger(MopeLSPClient.class);


    public void shutdownServer() throws ExecutionException, InterruptedException {
        CompletableFuture<Object> result = server.shutdown();
        result.get();

    }
    public void exitServer() {
        server.exit();
    }

    @Override
    public void telemetryEvent(Object object) {
        logger.info("Client->telemetryEvent");
    }

    @Override
    public void publishDiagnostics(PublishDiagnosticsParams diagnostics) {
        StringBuilder log = new StringBuilder("DiagnosticLocation: " + diagnostics.getUri() + "\nDiagnostics: \n");
        for(var d : diagnostics.getDiagnostics()){
            log.append(d.toString()).append("\n");
        }
        logger.info(log.toString());

    }

    @Override
    public void showMessage(MessageParams messageParams) {
        logger.info("Client->showMessage");
        logger.info(messageParams.toString());
    }

    @Override
    public CompletableFuture<MessageActionItem> showMessageRequest(ShowMessageRequestParams requestParams) {
        logger.info("Client->showMessageRequest");
        MessageActionItem result = new MessageActionItem();
        result.setTitle("MessageRequestArrived");
        return CompletableFuture.completedFuture(result) ;
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
    public String initServer() {
        try{
            InitializeParams params = new InitializeParams();
            CompletableFuture<InitializeResult> result = server.initialize(params);
            return result.get().toString();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String complete(String file, int line, int col) {
        TextDocumentIdentifier doc = new TextDocumentIdentifier();
        doc.setUri(file);
        CompletionParams params = new CompletionParams();
        params.setTextDocument(doc);
        CompletionContext c = new CompletionContext();
        c.setTriggerCharacter(".");
        params.setContext(c);
        Position p = new Position();
        p.setCharacter(col);
        p.setLine(line);
        params.setPosition(p);
        var completion = server.getTextDocumentService().completion(params);
        Object compGet = null;
        try {
            compGet = completion.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
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

    public String getDocumentation(String className){
        try{
            CompletableFuture<String> x = server.getModelicaService().getDocumentation(className);
            return x.get();
        }catch(Exception e){
            logger.error("Error loading Documentation",e);
            e.printStackTrace();
        }
        return null;
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
            CompletableFuture<String> x = server.getModelicaService().loadFile(path);
            return x.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object addPath(String path){
        try{
            CompletableFuture<String> x = server.getModelicaService().addModelicaPath(path);
            return x.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object loadModel(String name){
        try{
            CompletableFuture<String> x = server.getModelicaService().loadModel(name);
            return x.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object compilerVersion()  {
        try{
            CompletableFuture<String> x = server.getModelicaService().getCompilerVersion();
            return x.get();
        }catch(Exception e){
            logger.error("Error RequestVersion",e);
            e.printStackTrace();
        }
        return null;
    }

    public Object modelicaPath()  {
        try{
            CompletableFuture<String> x = server.getModelicaService().getModelicaPath();
            return x.get();
        }catch(Exception e){
            System.out.println("Error Get Path");
        }
        return null;
    }

    public Object sendExpression(String command) {
        CompletableFuture<String> result = server.getModelicaService().sendExpression(command);
        try {
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error during sendExpression", e);
        }
        return null;
    }

    @Override
    public CompletableFuture<List<WorkspaceFolder>> workspaceFolders(){
        CompletableFuture<List<WorkspaceFolder>> result = new CompletableFuture<>();
        logger.info("WorkspaceFolders requested by Server");
        WorkspaceFolder f = new WorkspaceFolder();
        f.setName("ExampleModels");
        f.setUri("/home/swtp/modelica/exampleModels");
        logger.info("WorkspaceFoldersResult created");
        return result;
    }


}
