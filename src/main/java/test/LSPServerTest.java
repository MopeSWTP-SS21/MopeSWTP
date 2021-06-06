package test;

import Client.ConsoleClientLauncher;
import Client.MopeLSPClient;
import Server.MopeLSPServer;
import Server.MopeLSPServerLauncher;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

class LSPServerTest{

    private static Future<Void> serverListening;
    private static Future<Void> clientListening;

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
    @BeforeEach
    public void startServer() throws IOException {
        new Thread(() -> {
            try {
                serverListening = serverLauncher.LaunchServer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    @BeforeEach
    public void startClient() throws IOException {
        new Thread(() -> {
            try {
                clientListening = clientLauncher.LaunchClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    @Test
    public void initializeServer() throws IOException {
       // startServer();
       // startClient();
       // clientLauncher.client.initServer();

    }
}