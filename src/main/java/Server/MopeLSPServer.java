package Server;

import Server.Compiler.OMCAdapter;
import omc.ZeroMQClient;
import omc.ior.ZMQPortFileProvider;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.*;

import java.util.concurrent.CompletableFuture;

public class MopeLSPServer implements ModelicaLanguageServer
{
    private LanguageClient client;// = new CopyOnWriteArrayList<>();
    private MopeDocumentService documentService;
    private MopeWorkspaceService workspaceService;


    public MopeLSPServer(){
        this.workspaceService = new MopeWorkspaceService();
        this.documentService = new MopeDocumentService();
    }

    @Override
    public CompletableFuture<String> checkModel(String filename){
        return CompletableFuture.supplyAsync(()->"checked");
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        InitializeResult result = new InitializeResult(new ServerCapabilities());

        System.out.println("Server->initialize triggerd");
        workspaceService.InitOMC(new OMCAdapter());


        this.client.showMessage(new MessageParams(MessageType.Info, "Hallo vom Server") );
        return CompletableFuture.supplyAsync(()->result);
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        System.out.println("server->shutdown");
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
        System.out.println("server->Connect");
        this.client = client;
        System.out.println("Added Client to Server");
    }
}
