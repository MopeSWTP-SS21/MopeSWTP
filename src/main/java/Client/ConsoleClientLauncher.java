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
import java.util.concurrent.*;


public class ConsoleClientLauncher {

    private static Socket socket;
    public static MopeLSPClient client;
    private Launcher<ModelicaLanguageServer> cLauncher;
    private static ExecutorService executor;
    private static String host;
    private static int port;
    private static Scanner sc = new Scanner(System.in);
    private static final Logger logger = LoggerFactory.getLogger(ConsoleClientLauncher.class);
    private static Future<Void> clientListening;

    public ConsoleClientLauncher(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        client = new MopeLSPClient();
        socket = new Socket(host, port);
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
        clientListening.get();
        executor.shutdown();
        logger.info("Client Finished");
    }

    private static void FullShutdown() throws IOException, ExecutionException, InterruptedException {
        shutdown();
        clientListening.get();
        executor.shutdown();
        logger.info("Client Finished");
    }

    public static void shutdown() throws ExecutionException, InterruptedException {
        client.shutdownServer();
        client.exitServer();
    }

    public static void main(String[] args) throws Exception {


        System.out.println("Serverip:");
        host= sc.next();
        System.out.println("Serverport:");
        port = sc.nextInt();

        ConsoleClientLauncher launcher = new ConsoleClientLauncher(host, port);

        clientListening = launcher.LaunchClient();

        ConsoleMenue();

    }

    private static void ConsoleMenue() throws IOException, ExecutionException, InterruptedException {
        boolean running=true;

        String[] menuItems = new String[] {
                "1: Initialize server",
                "2: Get compiler version",
                "3: Load File",
                "4: Load model",
                "5: Check Model",
                "6: Initialize Model",
                "7: Add Folder to ModelicaPath",
                "8: Show ModelicaPath",
                "9 : Exit - Shutdown Server",
                "10 : Exit - Disconnect",
                "11 : Complete"
        };

        while(running)
        {

            for (String item: menuItems ) {
                System.out.println(item);
            }
            System.out.print(">");

            int command= sc.nextInt();
            switch(command){
                case 1:
                    client.initServer();
                    break;
                case 2:
                    System.out.println(client.compilerVersion());
                    break;
                case 3:
                    System.out.print("path: ");
                    String filePath = sc.next();
                    System.out.println(client.loadFile(filePath));
                    break;
                case 5:
                    System.out.print("modelName: ");
                    String name = sc.next();
                    System.out.println(client.checkModel(name));
                    break;
                case 6:
                    System.out.println("not implemented");
                    break;
                case 9:
                    running=false;
                    FullShutdown();
                    break;
                case 7:
                    System.out.print("path: ");
                    String path = sc.next();
                    System.out.println(client.addPath(path));
                    break;
                case 4:
                    System.out.print("modelName: ");
                    String loadName = sc.next();
                    System.out.println(client.loadModel(loadName));
                    break;
                case 8:
                    System.out.println(client.modelicaPath());
                    break;
                case 10:
                    StopClient();
                    running = false;
                    break;
                case 11:
                    System.out.print("File: ");
                    String compFile = sc.next();
                    System.out.print("Line: ");
                    int line = sc.nextInt();
                    System.out.print("Column: ");
                    int col = sc.nextInt();
                    System.out.println(client.complete(compFile, line, col));
                    break;
                default:
                    logger.info("wrong entry");
                    break;
            }
        }
    }

}
