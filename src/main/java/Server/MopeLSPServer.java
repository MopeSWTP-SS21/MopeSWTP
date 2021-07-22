package Server;

import Client.MopeLSPClient;
import Server.Compiler.ICompilerAdapter;
import Server.Compiler.OMCAdapter;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MopeLSPServer implements ModelicaLanguageServer
{
    private static final Logger logger = LoggerFactory.getLogger(MopeLSPServer.class);
    private List<LanguageClient> clients;
    private MopeDocumentService documentService;
    private MopeWorkspaceService workspaceService;
    private MopeModelicaService modelicaService;
    private DiagnosticHandler diagnosticHandler;
    private static ICompilerAdapter compiler;
    private ConfigObject cfg;
    private CompletableFuture<Object> isRunning;

    public MopeLSPServer(ConfigObject config){
        this.clients = new ArrayList<>();
        this.diagnosticHandler = new DiagnosticHandler(this);
        this.compiler = new OMCAdapter(MopeLSPServerLauncher.omcpath, "us", "mope_local" );
        this.workspaceService = new MopeWorkspaceService(compiler);
        this.documentService = new MopeDocumentService(compiler);
        this.modelicaService = new MopeModelicaService(compiler, this);
        this.cfg = config;
        this.isRunning = new CompletableFuture<>();
    }


    public DiagnosticHandler getDiagnosticHandler(){
        return this.diagnosticHandler;
    }

    public void waitForShutDown() throws ExecutionException, InterruptedException {
        this.isRunning.get();
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        InitializeResult result = new InitializeResult(new ServerCapabilities());

        logger.info("Server->initialize triggerd");
        compiler.connect();

        return CompletableFuture.supplyAsync(()->result);
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        logger.info("server->shutdown");
        notifyAllClientsAboutShutdown();
        try {
            compiler.exit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return CompletableFuture.supplyAsync(() -> null);
    }

    public CompletableFuture<Object> disconnectClient() {
        logger.info("Client is about to be disconnected");
        return CompletableFuture.supplyAsync(() -> null);
    }

    private void notifyAllClientsAboutShutdown(){
        for (var c: clients) {
            c.showMessage(new MessageParams(MessageType.Info, "Server is about to shutdown"));
        }
    }
    public boolean isRunning() {
        return !isRunning.isDone();
    }


    @Override
    public void exit() {
        logger.info("server->exit");
        isRunning.complete(null);
    }

    @Override
    public TextDocumentService getTextDocumentService() {
        return this.documentService;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return this.workspaceService;
    }

    @Override
    public ModelicaService getModelicaService() { return this.modelicaService; }

    @Override
    public void connect(LanguageClient client) {
        logger.info("server->Connect");
        this.clients.add(client);
        logger.info("Added Client to Server");
        sayHelloToAllClients();
    }

    public void remove(LanguageClient client){
        this.clients.remove(client);
        logger.info("Removed Client from Server");
    }

    private void sayHelloToAllClients(){
        for (var c: clients) {
            c.showMessage(new MessageParams(MessageType.Info, "Hallo vom Server"));
        }
    }

    public void publishDiagnosticsToAllClients(PublishDiagnosticsParams params){
        for (var c: clients) {
            c.publishDiagnostics(params);
        }
    }
}
