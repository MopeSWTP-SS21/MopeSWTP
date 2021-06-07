package Server;


import Client.IModelicaLanguageClient;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.Log4jLoggerAdapter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.antlr.v4.runtime.misc.Utils.readFile;

public class MopeLSPServerLauncher {
    private static Socket socket;
    private static ServerSocket serverSocket;
    private static MopeLSPServer server;
    private Launcher<IModelicaLanguageClient> sLauncher;
    private static ExecutorService executor;
    private static String host;
    private static int port = 1234;
    private static Future<Void> serverListening;
    private static Logger logger = LoggerFactory.getLogger(MopeLSPServerLauncher.class);

    public MopeLSPServerLauncher(int port) throws IOException {
        ConfigObject config = new ConfigObject("1234");
        this.port = port;
        server = new MopeLSPServer(config);
        serverSocket = new ServerSocket(port);
    }

    public Future<Void> LaunchServer() throws IOException {

        System.setProperty(Log4jLoggerAdapter.ROOT_LOGGER_NAME, "TRACE");

        logger.info("Server socket listening");
        System.out.flush();
        socket = serverSocket.accept();
        logger.info("Server connected to client socket");
        System.out.flush();
        executor = Executors.newFixedThreadPool(2);
        sLauncher = new LSPLauncher.Builder<IModelicaLanguageClient>()
                .setLocalService(server)
                .setRemoteInterface(IModelicaLanguageClient.class)
                .setInput(socket.getInputStream())
                .setOutput(socket.getOutputStream())
                .setExecutorService(executor) //Not sure about this?
                .create();
        server.connect(sLauncher.getRemoteProxy());
        Future<Void> future = sLauncher.startListening();
        logger.info("Server Listening");
        return future;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try{
            Properties prop = new Properties();
            String configPath = "src/main/java/Server/server.config";
            InputStream configStream;
            try{
                configStream = new FileInputStream(configPath);
                prop.load(configStream);
                port = Integer.parseInt(prop.getProperty("server.port"));
                logger.info("Read Port " + port + " from " + configPath);
            }
            catch (Exception e){
                e.printStackTrace();
                port =1234;
            }
            MopeLSPServerLauncher launcher = new MopeLSPServerLauncher(port);
            serverListening = launcher.LaunchServer();

            System.in.read();
            serverSocket.close();
            socket.close();
            executor.shutdown();
            serverListening.get();
            logger.info("Server Finished");
        }catch(Exception e){

        }
    }
}
