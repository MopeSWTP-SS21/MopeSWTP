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
    private CompletableFuture<Object> shut;
    private ServerSocket socket;
    private String path;

    public MopeLSPServer(ConfigObject config){
        this.clients = new ArrayList<>();
        this.diagnosticHandler = new DiagnosticHandler(this);
       /* Properties prop = new Properties();
        String configPath = "$HOME/.config/mope/server.conf";
        try {
            InputStream configStream = new FileInputStream(configPath);
            prop.load(configStream);
            path = prop.getProperty("server.path");
        } catch (Exception e){
            e.printStackTrace();
        }*/
        this.compiler = new OMCAdapter("/usr/bin/omc", "us", "mope_local" );
        this.workspaceService = new MopeWorkspaceService(compiler);
        this.documentService = new MopeDocumentService(compiler);
        this.modelicaService = new MopeModelicaService(compiler, this);
        this.cfg = config;
        this.shut = new CompletableFuture<>();
    }

    public DiagnosticHandler getDiagnosticHandler(){
        return this.diagnosticHandler;
    }

    public void waitForShutDown() throws ExecutionException, InterruptedException {
        this.shut.get();
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

        //shut.complete(null);
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
        return !shut.isDone();
    }


    @Override
    public void exit() {
        logger.info("server->exit");
        shut.complete(null);
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
