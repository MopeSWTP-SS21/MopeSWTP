package Server;

import Client.MopeLSPClient;
import Server.Compiler.ICompilerAdapter;
import Server.Compiler.OMCAdapter;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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

    public MopeLSPServer(ConfigObject config){
        this.clients = new ArrayList<>();
        this.diagnosticHandler = new DiagnosticHandler(this);
        this.compiler = new OMCAdapter("/usr/bin/omc", "us", "mope_local" );
        this.documentService = new MopeDocumentService(compiler);
        this.modelicaService = new MopeModelicaService(compiler, this);
        this.workspaceService = new MopeWorkspaceService(this.modelicaService);
        this.cfg = config;
    }

    public DiagnosticHandler getDiagnosticHandler(){
        return this.diagnosticHandler;
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        InitializeResult result = new InitializeResult(MopeServerCapabilities.getCapabilities());

        logger.info("Server->initialize triggerd");
        compiler.connect();

        return CompletableFuture.supplyAsync(()->result);
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        logger.info("server->shutdown");
        return null;
    }

    @Override
    public void exit() {
        System.out.println("server->exit");
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
