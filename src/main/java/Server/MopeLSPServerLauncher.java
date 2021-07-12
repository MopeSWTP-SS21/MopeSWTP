package Server;


import Client.IModelicaLanguageClient;
import org.eclipse.lsp4j.ShowMessageRequestParams;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;

import org.eclipse.lsp4j.services.LanguageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.Log4jLoggerAdapter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.*;

public class MopeLSPServerLauncher {
    private static ServerSocket serverSocket;
    private static MopeLSPServer server;
    private static ExecutorService executor;
    private static Socket socket;
    private static int port = 4200;
    private static Logger logger = LoggerFactory.getLogger(MopeLSPServerLauncher.class);

    public MopeLSPServerLauncher(int port) throws IOException {
        ConfigObject config = new ConfigObject("4200");
        this.port = port;
        server = new MopeLSPServer(config);
        serverSocket = new ServerSocket(port);
    }

    public void LaunchServer() {

        System.setProperty(Log4jLoggerAdapter.ROOT_LOGGER_NAME, "TRACE");

        logger.info("Server socket listening on port " + port );
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
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    return null;
                } );
            }
        });


        return ;
    }

    public static void stopFromConsole(MopeLSPServer server) {
        logger.info("Press enter for a server shutdown");
        try {
            while(server.isRunning() && System.in.available() == 0) {
                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException ie) {

        }
        //server.shutdown();
    }

    public static void readConfigFile(String path) throws IOException {
        Properties prop = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(path)){
            prop.load(fileInputStream);
            port = Integer.parseInt(prop.getProperty("server.port"));
            logger.info("Read Port " + port + " from " + path);
        }
    }

    public static void readConfig() {
        String home = System.getProperty("user.home");
        String configPath = home+"/.config/mope/server.conf";
        try{
            readConfigFile(configPath);
        }
        catch (IOException ie){
            configPath = home+ "\\mope\\server.conf";
            try{
                readConfigFile(configPath);
            } catch (Exception ex){
                configPath = "src/main/java/Server/server.conf";
                try {
                    readConfigFile(configPath);
                } catch (Exception exc) {}
            }
        }
    }

    public static void main(String[] args) {
        try{
            readConfig();
            MopeLSPServerLauncher launcher = new MopeLSPServerLauncher(port);
            launcher.LaunchServer();
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
