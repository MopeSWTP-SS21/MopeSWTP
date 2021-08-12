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
 * <p>A class that launches the MopeLSPClient</p>
 * @author Manuel S. Wächtershäuser, Ilmar Bosnak, Conrad Lange
 */
public class ConsoleClientLauncher {

    private static Socket socket;
    public static MopeLSPClient client;
    private Launcher<ModelicaLanguageServer> cLauncher;
    private static ExecutorService executor;
    private static final Scanner sc = new Scanner(System.in);
    private static ConsoleMenu menu;
    private static final Logger logger = LoggerFactory.getLogger(ConsoleClientLauncher.class);
    public static Future<Void> clientListening;


    /**
     * <p>This method creates a launcher, opens a socket and connects the launcher to the server at the given host and port</p>
     * @param host is the IPv4-address of the server to connect to. Additionally it is allowed to use "localhost"
     * @param port on this port the server is running
     * @throws IOException if an I/O error occurs when creating the socket.
     */
    public ConsoleClientLauncher(String host, int port) throws IOException {
        client = new MopeLSPClient();
        socket = new Socket(host, port);
        menu = new ConsoleMenu(client);
    }

    /**
     * <p>Launches the client and connects him to the socket.</p>
     * <p>Additionally the client starts listening which means he is able to receive RPC (notifications/requests/responses)</p>
     * @return the future will be completed when the listening thread of the client exits, because it has been signalled an end-of-file by the input stream.
     * @throws IOException if an I/O error occurs when creating the input stream, the socket is closed, the socket is not connected,
     * or the socket input has been shutdown using shutdownInput() or when creating the output stream or if the socket is not connected.
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
     * <p>This method stops the client by closing the socket and waits for the listening thread of the client to exit</p>
     */
     public static void stopClient() {
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
        } catch (IOException  e) {
            logger.error("error by handling I/O");
            throw new RuntimeException(e);
        }
        try {
            clientListening.get();
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new AssertionError("unexpected interrupt", ie);
        } catch (ExecutionException ex){
            logger.error("This should never happen");
            throw new RuntimeException(ex);
        }
        executor.shutdown();
        logger.info("Client Finished");
    }


    /**
     * <p>This method requests the server to shutdown.</p>
     * <p>The request is triggered by executing the shutdown command first. After receiving a notification form the server
     * the exit command will be executed subsequently.</p>
     */
    public static void shutdownServer() throws ExecutionException, InterruptedException {
            client.shutdownServer();
            client.exitServer();
    }

    /**
     * The main method asks for a server ipv4 and for the port and afterwards it starts the client with all its feautures.
     * @param args not used
     */
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
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
