package Client;

import Server.ModelicaLanguageServer;
import org.eclipse.lsp4j.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Is the actual Client, which connects to the server
 * @author Manuel S. Wächtershäuser, Conrad Lange, Ilmar Bosnak
 */
public class MopeLSPClient implements IModelicaLanguageClient {
    //TODO use proper ReturnTypes
    private ModelicaLanguageServer server;
    private static final Logger logger = LoggerFactory.getLogger(MopeLSPClient.class);

    /**
     * <p>Here in this method the client sends a request to the server asking him to shutdown.</p>
     * @throws ExecutionException,  if this future completed exceptionally
     * @throws InterruptedException, if the current thread was interrupted while waiting
     */
    public void shutdownServer() throws ExecutionException, InterruptedException {
        CompletableFuture<Object> result = server.shutdown();
        result.get();

    }

    /**
     * <p>This method asks the server to exit.</p>
     */
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

    /**
     * <p>Sets the server instance used by the launcher.</p>
     * @param server has to be an ModelicaLanguageServer-object which will be used for the launcher
     */
    public void setServer(ModelicaLanguageServer server){
        logger.info("Client->setServer");
        this.server = server;
    }

    /**
     * <p>The client uses this method to initialize the server </p>
     * <p>This happens by sending an initial request and it must be called once.</p>
     * @return Why does it return a String though?
     */
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

    /**
     * <p>This method is used to request a list of possible code-completions from the server.</p>
     * @param file file containing the keyword which is going to be completed
     * @param line a digit, identifying the line where the keyword is going to be completed
     * @param col a digit, identifying the column where the keyword is going to be completed
     * @return a string, which is the full keyword of Modelica language
     */
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

    /**
     * <p>A method which is used for the hover-over-code feature</p>
     * <p>The hover request is send from the client to the server to request hover information at a given text document position
     * The LSP command used here is textDocument/hover </p>
     * @see <a href="https://microsoft.github.io/language-server-protocol/specification#textDocument_hover">LSP Specification</a>
     * @return the server response as a string
     * @throws ExecutionException in case of retrieving a result of a task which aborted by throwing an exception
     * @throws InterruptedException in case of a thread is interrupted
     */
    public String hover() throws ExecutionException, InterruptedException {
        HoverParams params = new HoverParams();
        params.setTextDocument(new TextDocumentIdentifier("file:///folder/name.mo") );
        params.setPosition(new Position(2,12));
        CompletableFuture<?> hover = server.getTextDocumentService().hover(params);
        var hoverGet = hover.get();
        return hoverGet.toString();
    }

    /**
     * <p>This method returns a HTML-formatted Documentation of a given Class.</p>
     * @param className has to be a valid classname
     * @return the html-formatted documentation by given classname
     */
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

    /**
     * <p>This method is used to signal the server that newly text documents were opened.</p>
     * <p>The document open notification is from the client to the server to signal newly opened text documents.
     * Before a client can change a text document it must claim ownership of its content using the textDocument/didOpen notification.</p>
     * @see <a href="https://microsoft.github.io/language-server-protocol/specification#textDocument_didOpen>LSP Specification</a>
     * @param path of the file that was opened
     */
    public void didOpenFile(String path){
        TextDocumentItem item = new TextDocumentItem();
        item.setText("Hallo, ...");
        DidOpenTextDocumentParams params = new DidOpenTextDocumentParams();
        params.setTextDocument(item);
        server.getTextDocumentService().didOpen(params);
    }

    /**
     * This method performs basic checks on a model and returns the number of variables and equations in it.
     * The method checkModel calls the method get() defined in CompletableFuture class and handles the exception by
     * priting the stack trace to the console
     * @param modelName has to be a name of a model.mo file
     * @return a string formatted output with a number of variables and equations in the model-file.
     */
    public Object checkModel(String modelName)  {
        try{
            CompletableFuture<String> x = server.getModelicaService().checkModel(modelName);
            return x.get();
        }catch(InterruptedException | ExecutionException e){
            logger.error("Error CheckModel",e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * <p>This method loads a model or many models in a file by adding its path to the Modelica-library.</p>
     * <p>It uses the OpenModelica Scripting API method loadFile() and merge it with the loaded AST</p>
     * @param path is the absolute path of the Modelica-file
     * @return returns the result of the request whether it was successful or not in a string format by OMC
     */
    public Object loadFile(String path){
        try{
            CompletableFuture<String> x = server.getModelicaService().loadFile(path);
            return x.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * <p>This method adds a folder path (where the modelica-file is located) to the Modelica library.</p>
     * @param path that has to be an absolute path to the modelica file
     * @return returns its result in a string format
     */
    public Object addPath(String path){
        try{
            CompletableFuture<String> x = server.getModelicaService().addModelicaPath(path);
            return x.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * <p>Loads a model the can be found in the modelica path</p>
     * @param name is a (fully qualified) Modelica class name
     * @return returns its result in a string format
     */
    public Object loadModel(String name){
        try{
            CompletableFuture<String> x = server.getModelicaService().loadModel(name);
            return x.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * <p>Provides an output with current Modelica Compiler Version</p>
     * @return returns its result in a string format
     */
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

    /**
     * <p>This method prints the Modelica path to the console.</p>
     * @return returns its result in a string format
     */
    public Object modelicaPath()  {
        try{
            CompletableFuture<String> x = server.getModelicaService().getModelicaPath();
            return x.get();
        }catch(Exception e){
            System.out.println("Error Get Path");
        }
        return null;
    }

    /**
     * <p>This method allows sending arbitrary OM-API commands to the OMC</p>
     * @param command is an OMShell command to be executed
     * @return an output formatted as a string
     * @see <a href="https://build.openmodelica.org/Documentation/OpenModelica.Scripting.html">List of OM commands</a>
     */
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
