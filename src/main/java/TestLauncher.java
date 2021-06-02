import Client.MopeLSPClient;
import Server.MopeLSPServer;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public  class TestLauncher {
    private static final Logger logger = LoggerFactory.getLogger(TestLauncher.class);
    private static final Object serverLock = new Object();
    private static final Object clientLock = new Object();
    public static void main(String[] args) throws Exception {



        Thread startServer = new Thread(TestLauncher::startTestServer);
        Thread startClient = new Thread(TestLauncher::startConsoleClient);

        startServer.start();
        Thread.sleep(1000);
        startClient.start();







        logger.info("finished");



        logger.info("Press enter");
        System.in.read();
        synchronized (serverLock) { serverLock.notify(); }
        synchronized (clientLock) { clientLock.notify(); }

    }


    public static void startTestServer()  {
        try{
            MopeLSPServer testServer = new MopeLSPServer();
            ServerSocket socket = new ServerSocket(1234);
            logger.info("Server socket listening");
            System.out.flush();//??
            Socket connection = socket.accept();
            logger.info("Server connected to client socket");
            System.out.flush();
            ExecutorService executor = Executors.newFixedThreadPool(2);
            Launcher<LanguageClient> sLauncher = new LSPLauncher.Builder<LanguageClient>()
                    .setLocalService(testServer)
                    .setRemoteInterface(LanguageClient.class)
                    .setInput(connection.getInputStream())
                    .setOutput(connection.getOutputStream())
                    .setExecutorService(executor) //Not sure about this?
                    .create();

            testServer.connect(sLauncher.getRemoteProxy());
            Future<Void>sListeningFuture = sLauncher.startListening();
            logger.info("Server Listening");
            synchronized(serverLock) {
                try {
                    serverLock.wait();
                } catch (InterruptedException e) { /* if interrupted, we exit anyway */ }
            }
            socket.close();
            connection.close();
            executor.shutdown();
            sListeningFuture.get();
            logger.info("Server Finished");
        }catch(Exception e){

        }

    }

    public static void startConsoleClient(){
        try{
            MopeLSPClient client = new MopeLSPClient();
            Socket socket = new Socket("127.0.0.1", 1234);
            logger.info("Client socket connected");
            System.out.flush();
            ExecutorService executor = Executors.newFixedThreadPool(2);
            Launcher<org.eclipse.lsp4j.services.LanguageServer> cLauncher = new LSPLauncher.Builder<org.eclipse.lsp4j.services.LanguageServer>()
                    .setLocalService(client)
                    .setRemoteInterface(org.eclipse.lsp4j.services.LanguageServer.class)
                    .setInput(socket.getInputStream())
                    .setOutput(socket.getOutputStream())
                    .setExecutorService(executor) //Not sure about this?
                    .create();

            client.setServer(cLauncher.getRemoteProxy());
            Future<Void>cListeningFuture = cLauncher.startListening();
            logger.info("Client Listening");
            client.initServer();
            client.hover();
            logger.info(client.compilerVersion().toString());
            //System.out.println(client.checkModel("abc"));
            synchronized(clientLock) {
                try { clientLock.wait(); } catch (InterruptedException e) { /* if interrupted, we exit anyway */ }
            }
            socket.close();
            executor.shutdown();
            cListeningFuture.get();
            logger.info("Client Finished");
        }catch(Exception e){

        }

    }
}
