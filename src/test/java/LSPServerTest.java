

import Client.ConsoleClientLauncher;
import Client.MopeLSPClient;
import Server.MopeLSPServer;
import Server.MopeLSPServerLauncher;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LSPServerTest{

    private static final Logger logger = LoggerFactory.getLogger(MopeLSPServer.class);
    private final CompletableFuture<Boolean> testsFinished = new CompletableFuture<>();

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
    public void startServer() throws IOException, ExecutionException, InterruptedException {
        new Thread(() -> {
            try {
                serverLauncher.LaunchServer();
                testsFinished.get();
                logger.info("Server Thread finishing...");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    @BeforeAll
    public void startClient() throws IOException {
        new Thread(() -> {
            try {
                clientLauncher.LaunchClient();
                testsFinished.get();
                logger.info("Client Thread finishing...");
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
        assertEquals("V 1.17.0",clientLauncher.client.compilerVersion());
    }

    @AfterAll
    public void endTests(){
        logger.info("All tests done... Completing Future");
        testsFinished.complete(true);
    }
}