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
    private static MopeLSPClient client;
    private static Launcher<LanguageServer> cLauncher;
    private static ExecutorService executor;
    private static String host;
    private static int port;
    private static Scanner sc = new Scanner(System.in);
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


        System.out.println("Serverip:");
        host= sc.next();
        System.out.println("Serverport:");
        port = sc.nextInt();


        clientListening = LaunchClient();

        ConsoleMenue();


        StopClient();

    }

    private static void ConsoleMenue(){
        boolean running=true;

        String[] menuItems = new String[] {   "1: Initialize server",
                                            "2: Get compiler version",
                "3: Load File",
                "4: Load model",
                "5: Check Model",
                "6: Initialize Model",
                "7: Add Folder to ModelicaPath",
                "8: Show ModelicaPath",
                "9 : Exit"
        }                ;

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
                default:
                    System.out.println("wrong entry");
                    break;
            }
        }
    }

}
