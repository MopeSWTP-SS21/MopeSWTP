package Server;


import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.Log4jLoggerAdapter;

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
    private Launcher<LanguageClient> sLauncher;
    private static ExecutorService executor;
    private static String host;
    private static int port = 1234;
    private static Future<Void> serverListening;
    private static Logger logger = LoggerFactory.getLogger(MopeLSPServerLauncher.class);

    public MopeLSPServerLauncher(int port) throws IOException {
        this.port = port;
        server = new MopeLSPServer();
        serverSocket = new ServerSocket(port);
    }

    public Future<Void> LaunchServer() throws IOException {

        System.setProperty(Log4jLoggerAdapter.ROOT_LOGGER_NAME, "TRACE");

        logger.info("Server socket Listening");
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
            MopeLSPServerLauncher launcher = new MopeLSPServerLauncher(1234);
            serverListening = launcher.LaunchServer();
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
