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

/**
 * @author Manuel S. Wächtershäuser, Ilmar Bosnak, Conrad Lange
 */
public class ConsoleClientLauncher {

    private static Socket socket;
    public static MopeLSPClient client;
    private Launcher<ModelicaLanguageServer> cLauncher;
    private static ExecutorService executor;
    public String host;
    public int port;
    private static final Scanner sc = new Scanner(System.in);
    private static ConsoleMenu menu;
    private static final Logger logger = LoggerFactory.getLogger(ConsoleClientLauncher.class);
    public static Future<Void> clientListening;


    /**
     * The constructor creates a client-instance, opens a socketand prints the client menu
     * @param host is the ip-address of the server to connect to
     * @param port on this port the server is running
     * @throws IOException which is thrown in case of an failed or interrupted I/O operation
     */
    public ConsoleClientLauncher(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        client = new MopeLSPClient();
        socket = new Socket(host, port);
        menu = new ConsoleMenu(client);
    }

    /**
     * <p>Launches the client and connects him to the socket.</p>
     * <p>Additionally the client starts listening which means he is able to receive RPC (notifications/requests/responses)</p>
     * @return a future result but it has no value, which will be completed after clients disconnects from server
     * @throws IOException which is thrown in case of an failed or interrupted I/O operation
     */
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

    /**
     * <p>This method stops the client by closing the socket</p>
     * <p>In case it was successful it informs the user that the client has finished </p>
     * @throws ExecutionException, in case of retrieving a result of a task which aborted by throwing an exception
     */
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
            logger.error("error by handling I/O");
        }
        try {
            clientListening.get();
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            System.exit(1);
        }
        executor.shutdown();
        logger.info("Client Finished");
    }


    /**
     * This method requests the server to shutdown.
     * @throws ExecutionException, in case of retrieving a result of a task which aborted by throwing an exception
     * @throws InterruptedException in case of a thread gets interrupted
     */
    public static void shutdownServer() throws ExecutionException {
        try{
            client.shutdownServer();
            client.exitServer();
        }catch(InterruptedException ie){
            Thread.currentThread().interrupt();
            logger.error("Some Problems occurred during server shutdown", ie);
        }
    }

    /**
     * The main method asks for a server ip and for the port and afterwards it starts the client with all its feautures.
     * @param args
     * @throws Exception in case of undesired behaviour
     */
    public static void main(String[] args) throws Exception {
        System.out.println("Serverip:");
        String host= sc.next();
        System.out.println("Serverport:");
        int port = sc.nextInt();

        ConsoleClientLauncher launcher = new ConsoleClientLauncher(host, port);

        clientListening = launcher.launchClient();
        var shutdownServer= menu.run();
        if(shutdownServer) shutdownServer();
        stopClient();
    }
}
