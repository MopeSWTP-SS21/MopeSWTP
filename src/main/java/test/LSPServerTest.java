package test;

import Client.ConsoleClientLauncher;
import Client.MopeLSPClient;
import Server.MopeLSPServer;
import Server.MopeLSPServerLauncher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Future;

import static Client.ConsoleClientLauncher.LaunchClient;
import static Client.ConsoleClientLauncher.client;
import static Server.MopeLSPServerLauncher.LaunchServer;
import static org.junit.jupiter.api.Assertions.*;

class LSPServerTest{

    private static Future<Void> serverListening;
    private static Future<Void> clientListening;

    public void startServer() throws IOException {
        new Thread() {
            public void run() {
                try {
                    serverListening = LaunchServer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public void startClient() throws IOException {
        new Thread() {
            public void run() {
                try {
                    clientListening = LaunchClient();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    @Test
    public void initializeServer() throws IOException {
        startServer();
        startClient();
        client.initServer();

    }
}