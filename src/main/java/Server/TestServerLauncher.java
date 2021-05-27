package Server;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class TestServerLauncher {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("ServerLauncher doing its thing...");
        TestServer testServer = new TestServer();
        Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(testServer, System.in, System.out);
        LanguageClient client = launcher.getRemoteProxy();
        testServer.connect(client);

        Future<?> startListening = launcher.startListening();

        startListening.get();
    }
}
