package Client;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageServer;

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
    private Launcher<LanguageServer> cLauncher;
    private static ExecutorService executor;
    private static String host;
    private static int port;
    private static Scanner sc = new Scanner(System.in);
    private static Future<Void> clientListening;

    public ConsoleClientLauncher(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        client = new MopeLSPClient();
        socket = new Socket(host, port);
    }

    public Future<Void> LaunchClient() throws IOException {
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



        //System.out.println(client.checkModel("abc"));

        System.out.println("Serverip:");
        host= sc.next();
        System.out.println("Serverport:");
        port = sc.nextInt();

        ConsoleClientLauncher launcher = new ConsoleClientLauncher(host, port);

        clientListening = launcher.LaunchClient();

        ConsoleMenue();


        StopClient();

    }

    private static void ConsoleMenue(){
        boolean running=true;
        while(running)
        {
            System.out.print("1: Initialize server\n2: Get compiler version\n3: Check model\n4: Exit\n");
            int command= sc.nextInt();
            switch(command){
                case 1:
                    client.initServer();
                    break;
                case 2:
                    System.out.println(client.compilerVersion());
                    break;
                case 4:
                    running=false;
                    break;
                default:
                    System.out.println("wrong entry");
                    break;
            }
        }
    }

}
