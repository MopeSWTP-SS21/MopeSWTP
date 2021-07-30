package Client;

import Server.ModelicaLanguageServer;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.*;


public class ConsoleClientLauncher {

    private static Socket socket;
    public static MopeLSPClient client;
    private Launcher<ModelicaLanguageServer> cLauncher;
    private static ExecutorService executor;
    private static String host;
    private static int port;
    private static final Scanner sc = new Scanner(System.in);
    private static ConsoleMenu menu;
    private static final Logger logger = LoggerFactory.getLogger(ConsoleClientLauncher.class);
    public static Future<Void> clientListening;

    public ConsoleClientLauncher(String host, int port) throws IOException {
        ConsoleClientLauncher.host = host;
        ConsoleClientLauncher.port = port;
        client = new MopeLSPClient();
        socket = new Socket(host, port);
        menu = new ConsoleMenu(client);
    }

    public Future<Void> launchClient() throws IOException {
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

    public static void stopClient() throws ExecutionException{
        try{
            socket.close();
        } catch (SocketException e){
            /*
            TODO Ignore following Exception:
            org.eclipse.lsp4j.jsonrpc.json.StreamMessageProducer fireStreamClosed
            INFO: Socket closed
            java.net.SocketException: Socket closed
            TODO This exception is thrown/printed during socket.close(), no matter if serverShutdown was called before or not...
             */
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            clientListening.get();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        executor.shutdown();
        logger.info("Client Finished");
    }



    public static void shutdownServer() throws ExecutionException {
        try{
            client.shutdownServer();
            client.exitServer();
        }catch(InterruptedException ie){
            logger.error("Some Problems occurred during server shutdown", ie);
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Serverip:");
        host= sc.next();
        System.out.println("Serverport:");
        port = sc.nextInt();

        ConsoleClientLauncher launcher = new ConsoleClientLauncher(host, port);

        clientListening = launcher.launchClient();
        var shutdownServer= menu.run();
        if(shutdownServer) shutdownServer();
        stopClient();
    }
}
