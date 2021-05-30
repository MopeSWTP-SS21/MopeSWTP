package Server;

import omc.ZeroMQClient;
import omc.corba.OMCInterface;
import omc.corba.Result;
import omc.ior.ZMQPortFileProvider;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.services.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

public class TestServer implements TestModelicaServer
{
    private LanguageClient client;// = new CopyOnWriteArrayList<>();
    private TextDocumentService textDocumentService;
    private TestWorkspaceService workspaceService;


    public TestServer(){
        this.workspaceService = new TestWorkspaceService();
        this.textDocumentService = new TestDocumentService();
    }

    @Override
    public CompletableFuture<String> checkModel(String filename){
        return CompletableFuture.supplyAsync(()->"checked");
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        InitializeResult result = new InitializeResult(new ServerCapabilities());

        System.out.println("Server->initialize triggerd");
        workspaceService.InitOMC();


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
        return this.textDocumentService;
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
