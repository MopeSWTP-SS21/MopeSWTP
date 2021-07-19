package Client;

import Server.ModelicaLanguageServer;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import version.Version;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class ConsoleClientLauncher {

    private static Socket socket;
    public static MopeLSPClient client;
    private Launcher<ModelicaLanguageServer> cLauncher;
    private static ExecutorService executor;
    private static String host;
    private static int port;
    private static Scanner sc = new Scanner(System.in);
    private static ConsoleMenu menu;
    private static final Logger logger = LoggerFactory.getLogger(ConsoleClientLauncher.class);
    private static Future<Void> clientListening;

    public ConsoleClientLauncher(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        client = new MopeLSPClient();
        socket = new Socket(host, port);
        menu = new ConsoleMenu(client);
    }

    public Future<Void> LaunchClient() throws IOException {
        executor = Executors.newFixedThreadPool(2);
        cLauncher = new LSPLauncher.Builder<ModelicaLanguageServer>()
                .setLocalService(client)
                .setRemoteInterface(ModelicaLanguageServer.class)
                .setInput(socket.getInputStream())
                .setOutput(socket.getOutputStream())
                .setExecutorService(executor)
                .create();
        client.setServer(cLauncher.getRemoteProxy());
        Future<Void> future = cLauncher.startListening();
        logger.info("Client listening");
        return future;
    }

    private static void StopClient() throws IOException, ExecutionException, InterruptedException {
        socket.close();

        executor.shutdown();
        clientListening.get();
        logger.info("Client Finished");
    }

    public static void main(String[] args) throws Exception {


        System.out.println("Serverip:");
        host= sc.next();
        System.out.println("Serverport:");
        port = sc.nextInt();

        ConsoleClientLauncher launcher = new ConsoleClientLauncher(host, port);

        clientListening = launcher.LaunchClient();
        menu.run();
        StopClient();

    }

}
