package Client;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageServer;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ConsoleClientLauncher {

    private static Socket socket;
    private static MopeLSPClient client;
    private static Launcher<LanguageServer> cLauncher;
    private static ExecutorService executor;
    private static String host;
    private static int port;
    private static Future<Void> clientListening;
    private static Future<Void> LaunchClient() throws IOException {
        client = new MopeLSPClient();
        socket = new Socket(host, port);
        executor = Executors.newFixedThreadPool(2);
        cLauncher = new LSPLauncher.Builder<org.eclipse.lsp4j.services.LanguageServer>()
                .setLocalService(client)
                .setRemoteInterface(org.eclipse.lsp4j.services.LanguageServer.class)
                .setInput(socket.getInputStream())
                .setOutput(socket.getOutputStream())
                .setExecutorService(executor) //Not sure about this?
                .create();
        client.setServer(cLauncher.getRemoteProxy());
        Future<Void> future = cLauncher.startListening();
        System.out.println("Client Listening");
        return future;
    }

    private static void StopClient() throws IOException, ExecutionException, InterruptedException {
        //client.shutdown();
        socket.close();
        executor.shutdown();
        clientListening.get();
        System.out.println("Client Finished");
    }

    public static void main(String[] args) throws Exception {

        host = "127.0.0.1";
        port = 1234;
        clientListening = LaunchClient();
        client.initServer();
        System.out.println(client.compilerVersion());
        //System.out.println(client.checkModel("abc"));
        System.in.read();
        StopClient();

    }

}
