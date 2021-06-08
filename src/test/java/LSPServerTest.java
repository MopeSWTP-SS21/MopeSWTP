

import Client.ConsoleClientLauncher;
import Client.MopeLSPClient;
import Server.MopeLSPServer;
import Server.MopeLSPServerLauncher;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LSPServerTest{

    private  Future<Void> serverListening;
    private  Future<Void> clientListening;

    MopeLSPServerLauncher serverLauncher;
    {
        try {
            serverLauncher = new MopeLSPServerLauncher(1234);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ConsoleClientLauncher clientLauncher;
    {
        try {
            clientLauncher = new ConsoleClientLauncher("localhost",1234);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @BeforeAll
    public void startServer() throws IOException {
        new Thread(() -> {
            try {
                serverListening = serverLauncher.LaunchServer();
                //test.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    @BeforeAll
    public void startClient() throws IOException {
        new Thread(() -> {
            try {
                clientListening = clientLauncher.LaunchClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    public void initializeServer() throws IOException, InterruptedException {
        Thread.currentThread().sleep(1000);
        clientLauncher.client.initServer();
        Thread.currentThread().sleep(15000);
    }
    @Test
    public void getOMCVersion() throws IOException, InterruptedException {
        initializeServer();
        clientLauncher.client.compilerVersion();
    }
}