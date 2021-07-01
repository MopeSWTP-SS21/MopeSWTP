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
    private static int port = 1234;
    private static Logger logger = LoggerFactory.getLogger(MopeLSPServerLauncher.class);

    public MopeLSPServerLauncher(int port) throws IOException {
        ConfigObject config = new ConfigObject("1234");
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
                Socket socket = serverSocket.accept();
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

    public static void main(String[] args) {
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
            launcher.LaunchServer();

            System.in.read();
            serverSocket.close();
            executor.shutdown();
            logger.info("Server Finished");
        }catch(Exception e){

        }
    }
}
