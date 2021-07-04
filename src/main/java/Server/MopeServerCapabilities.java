package Server;

import org.eclipse.lsp4j.CompletionOptions;
import org.eclipse.lsp4j.ServerCapabilities;

import java.util.List;

public class MopeServerCapabilities {
    public static ServerCapabilities getCapabilities(){
        ServerCapabilities capabilities = new ServerCapabilities();
        addCompletionCapabilities(capabilities);
        return capabilities;
    }
    private static void addCompletionCapabilities(ServerCapabilities cap){
        CompletionOptions completion = new CompletionOptions();
        completion.setTriggerCharacters(List.of("."));
        completion.setResolveProvider(false);
        cap.setCompletionProvider(completion);
    }
}
