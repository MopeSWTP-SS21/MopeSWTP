package Server;

import Client.MopeLSPClient;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MopeLSPServerLauncher {
    private static Socket socket;
    private static ServerSocket serverSocket;
    private static MopeLSPServer server;
    private static Launcher<LanguageClient> sLauncher;
    private static ExecutorService executor;
    private static String host;
    private static int port;
    private static Future<Void> serverListening;

    private static Future<Void> LaunchServer() throws IOException {
        server = new MopeLSPServer();
        serverSocket = new ServerSocket(port);
        System.out.println("Server socket listening");
        System.out.flush();
        socket = serverSocket.accept();
        System.out.println("Server connected to client socket");
        System.out.flush();
        executor = Executors.newFixedThreadPool(2);
        sLauncher = new LSPLauncher.Builder<org.eclipse.lsp4j.services.LanguageClient>()
                .setLocalService(server)
                .setRemoteInterface(org.eclipse.lsp4j.services.LanguageClient.class)
                .setInput(socket.getInputStream())
                .setOutput(socket.getOutputStream())
                .setExecutorService(executor) //Not sure about this?
                .create();
        server.connect(sLauncher.getRemoteProxy());
        Future<Void> future = sLauncher.startListening();
        System.out.println("Server Listening");
        return future;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try{
            port =1234;
            serverListening = LaunchServer();
            System.in.read();
            serverSocket.close();
            socket.close();
            executor.shutdown();
            serverListening.get();
            System.out.println("Server Finished");
        }catch(Exception e){

        }
    }
}
