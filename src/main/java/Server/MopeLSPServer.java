package Server;

import Server.Compiler.OMCAdapter;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class MopeLSPServer implements ModelicaLanguageServer
{
    private static final Logger logger = LoggerFactory.getLogger(MopeLSPServer.class);
    private LanguageClient client;
    private MopeDocumentService documentService;
    private MopeWorkspaceService workspaceService;
    private ConfigObject cfg;

    public MopeLSPServer(ConfigObject config){
        this.workspaceService = new MopeWorkspaceService();
        this.documentService = new MopeDocumentService();
        this.cfg = config;
    }

    @Override
    public CompletableFuture<String> checkModel(String filename){
        return CompletableFuture.supplyAsync(()->"checked");
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        InitializeResult result = new InitializeResult(new ServerCapabilities());

        logger.info("Server->initialize triggerd");
        workspaceService.InitOMC( new OMCAdapter("/usr/bin/omc", "us", "mope_local" ));


        this.client.showMessage(new MessageParams(MessageType.Info, "Hallo vom Server") );
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
    public void connect(LanguageClient client) {
        logger.info("server->Connect");
        this.client = client;
        logger.info("Added Client to Server");
    }
}
