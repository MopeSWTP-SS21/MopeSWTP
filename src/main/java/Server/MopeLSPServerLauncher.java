package Server;


import Client.IModelicaLanguageClient;
import org.eclipse.lsp4j.ShowMessageRequestParams;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;

import org.eclipse.lsp4j.services.LanguageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.Log4jLoggerAdapter;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.*;

public class MopeLSPServerLauncher {
    private static ServerSocket serverSocket;
    private static MopeLSPServer server;
    private static ExecutorService executor;
    private static Socket socket;
    private static Logger logger = LoggerFactory.getLogger(MopeLSPServerLauncher.class);
    private static ConfigObject configObject;

    /**
     * <p>starts a new server opens a socket and starts listening on this socket/p>
     * @throws IOException in case of an I/O error
     */
    public MopeLSPServerLauncher() throws IOException {
        configObject = new ConfigObject();
        readConfig();
        server = new MopeLSPServer(configObject);
        serverSocket = new ServerSocket(configObject.port);
    }

    /**
     * <p>launches the server and accepts the connection of the client</p>
     * The method launchServer also calls the method get() defined in CompletableFuture class and handles InterruptedException by throwing an
     * AssertionError and ExecutionException by throwing a RuntimeException
     */
    public void launchServer() {

        System.setProperty(Log4jLoggerAdapter.ROOT_LOGGER_NAME, "TRACE");

        logger.info("Server socket listening on port " + configObject.port );
        System.out.flush();
        executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            while (true) {
                socket = serverSocket.accept();
                logger.info("Server connected to client socket");
                System.out.flush();
                Launcher<IModelicaLanguageClient> sLauncher = new LSPLauncher.Builder<IModelicaLanguageClient>()
                        .setLocalService(server)
                        .setRemoteInterface(IModelicaLanguageClient.class)
                        .setInput(socket.getInputStream())
                        .setOutput(socket.getOutputStream())
                        .setExecutorService(executor)
                        .create();
                LanguageClient consumer = sLauncher.getRemoteProxy();
                server.connect(consumer);

                CompletableFuture.supplyAsync(() ->{
                    Future listening = sLauncher.startListening();
                    try {
                        listening.get();
                        server.remove(consumer);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new AssertionError("unexpected interrupt", ie);
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                    return null;
                } );
            }
        });


        return ;
    }

    /**
     * <p>Stops the server by user when he chooses the shutdown option and hits enter </p>
     * @param server the server to stop
     */
    public static void stopFromConsole(MopeLSPServer server) {
        logger.info("Press enter for a server shutdown");
        try {
            while(server.isRunning() && System.in.available() == 0) {
                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    public static void readConfigFile(Path path) throws IOException {
        Properties prop = new Properties();
        try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(path.toString()), StandardCharsets.UTF_8)){
            prop.load(bufferedReader);
            configObject.port = Integer.parseInt(prop.getProperty("server.port"));
            configObject.path = prop.getProperty("server.path");
            logger.debug("Read Port " + configObject.port + " from " + configObject.path);
            bufferedReader.close();
        }
    }

    /**
     * <p>Trying to read a configfile from the standard config directory according to the OS</p>
     * <p>First it tries to read from the standard-config-path on Unix which is "home/username/.config/mope/server.conf".
     * If it is not a Unix OS it tries to read from the standard-config-path on Windows which is "home\\username\\mope\\server.conf".
     * If it not a Unix or Windows OS the method tries to read from the Project-folder where the server-config is stored as a backup.
     * The path is "src/main/java/Server/server.config"
     * The method also calls the readConfigFile(String path) defined in this class and handles IOException by
     * trying to read from above mentioned paths. If the reading of a config was not successful or the config-files were corrupt, it throws
     * an AssertionError.
     *</p>
     */
    public static void readConfig() {
        String home = System.getProperty("user.home");
        Path configPath = Path.of(home,"/.config/mope/server.conf");
        try{
            readConfigFile(configPath);
        }
        catch (IOException ie){
            configPath = Path.of(home,"\\mope\\server.conf");
            try{
                readConfigFile(configPath);
            } catch (IOException ex){
                configPath = Path.of("src/main/java/Server/server.config");
                try {
                    readConfigFile(configPath);
                } catch (IOException exc) {
                    throw new AssertionError("fatal error, could not read config file");
                }
            }
        }
    }

    public static void main(String[] args) {
        try{
            MopeLSPServerLauncher launcher = new MopeLSPServerLauncher();
            launcher.launchServer();
            new Thread(() -> stopFromConsole(server)).start();
            server.waitForShutDown();
            socket.shutdownInput();
            executor.shutdown();
            serverSocket.close();
            logger.info("Server Finished");
        }catch(Exception e){

        }
    }
}
