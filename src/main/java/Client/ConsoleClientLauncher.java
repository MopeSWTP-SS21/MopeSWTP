package Client;

import Server.TestServer;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageServer;

public class ConsoleClientLauncher {
    public static void main(String[] args) throws Exception {
        ConsoleClient client = new ConsoleClient();

        Launcher<LanguageServer> launcher = LSPLauncher.createClientLauncher(client, System.in, System.out);
        client.setServer(launcher.getRemoteProxy());
        System.out.println("Starting...");

        launcher.startListening();
        System.out.println("Listening...");
        client.start(launcher.getRemoteProxy());
        client.getCompletion("say");
    }
}
