import Client.ConsoleClient;
import Server.TestServer;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.Future;

public class DualLauncher {
    public static void main(String[] args) throws Exception {
        ConsoleClient client = new ConsoleClient();
        TestServer testServer = new TestServer();


        PipedOutputStream clientWritesTo = new PipedOutputStream();
        PipedInputStream clientReadsFrom = new PipedInputStream();
        PipedInputStream serverReadsFrom = new PipedInputStream();
        PipedOutputStream serverWritesTo = new PipedOutputStream();

        serverWritesTo.connect(clientReadsFrom);
        clientWritesTo.connect(serverReadsFrom);

        Launcher<LanguageServer> cLauncher = LSPLauncher.createClientLauncher(client, clientReadsFrom, clientWritesTo);
        Launcher<LanguageClient> sLauncher = LSPLauncher.createServerLauncher(testServer, serverReadsFrom, serverWritesTo);
        client.setServer(cLauncher.getRemoteProxy());
        testServer.connect(sLauncher.getRemoteProxy());


        Future<Void>cListeningFuture = cLauncher.startListening();
        Future<Void>sListeningFuture = sLauncher.startListening();

        System.out.println("Launchers started To Listen..");






        client.start(cLauncher.getRemoteProxy());
        System.out.println(client.getCompletion("say"));

        cListeningFuture.cancel(true);
        sListeningFuture.cancel(true);


        //cListeningFuture.get();
        //sListeningFuture.get();

        System.out.println("finished");

        //clientWritesTo.close();
        //clientReadsFrom.close();
        //serverWritesTo.close();
        //serverReadsFrom.close();

        //System.in.read();


    }
}
