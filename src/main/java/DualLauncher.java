import Client.ConsoleClient;
import Server.TestModelicaServer;
import Server.TestServer;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;

import java.io.*;
import java.net.ServerSocket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public  class DualLauncher {
    private static final Object serverLock = new Object();
    private static final Object clientLock = new Object();
    public static void main(String[] args) throws Exception {

        ExecutorService executor = Executors.newFixedThreadPool(2);

        PipedOutputStream clientWritesTo = new PipedOutputStream();
        PipedInputStream clientReadsFrom = new PipedInputStream();
        PipedInputStream serverReadsFrom = new PipedInputStream();
        PipedOutputStream serverWritesTo = new PipedOutputStream();

        serverWritesTo.connect(clientReadsFrom);
        clientWritesTo.connect(serverReadsFrom);

        /*try{
            Thread startServer = new Thread(DualLauncher::startTestServer(serverReadsFrom,serverWritesTo,executor));
        }catch(IOException e){

        }*/


        Thread server = new Thread(new Runnable() {
            public void run()
            {
                System.out.println("Server Thread");
                TestServer server = new TestServer();
                startTestServer(serverReadsFrom,serverWritesTo,server);
            }});
        server.start();
        Thread client = new Thread(new Runnable() {
            public void run()
            {
                ConsoleClient client = new ConsoleClient();

                startConsoleClient(clientReadsFrom,clientWritesTo,client);

            }});
        client.start();



        System.out.println("finished");

        //clientWritesTo.close();
        //clientReadsFrom.close();
        //serverWritesTo.close();
        //serverReadsFrom.close();

        System.out.println("Press enter");
        System.in.read();
        synchronized (serverLock) { serverLock.notify(); }
        synchronized (clientLock) { clientLock.notify(); }

    }


    public static void startTestServer(InputStream in, OutputStream out, TestServer testServer)  {
        try{
            Launcher<LanguageClient> sLauncher = new LSPLauncher.Builder<LanguageClient>()
                    .setLocalService(testServer)
                    .setRemoteInterface(LanguageClient.class)
                    .setInput(in)
                    .setOutput(out)
                    //.setExecutorService(executor) //Not sure about this?
                    .create();

            testServer.connect(sLauncher.getRemoteProxy());
            Future<Void>sListeningFuture = sLauncher.startListening();
            System.out.println("Server Listening");
            synchronized(serverLock) {
                try {
                    serverLock.wait();
                } catch (InterruptedException e) { /* if interrupted, we exit anyway */ }
            }

            sListeningFuture.get();
            System.out.println("Server Finished");
        }catch(Exception e){

        }

    }

    public static void startConsoleClient(InputStream in, OutputStream out, ConsoleClient client){
        try{
            Launcher<LanguageServer> cLauncher = new LSPLauncher.Builder<LanguageServer>()
                    .setLocalService(client)
                    .setRemoteInterface(LanguageServer.class)
                    .setInput(in)
                    .setOutput(out)
                    //.setExecutorService(executor) //Not sure about this?
                    .create();

            client.setServer(cLauncher.getRemoteProxy());
            Future<Void>cListeningFuture = cLauncher.startListening();
            System.out.println("Client Listening");
            System.out.println(client.checkModel("abc", "def"));
            synchronized(clientLock) {
                try { clientLock.wait(); } catch (InterruptedException e) { /* if interrupted, we exit anyway */ }
            }
            out.close();
            cListeningFuture.get();
            System.out.println("Client Finished");
        }catch(Exception e){

        }

    }
}
