package Server;

import Server.Compiler.ModelicaDiagnostic;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.services.LanguageClient;

import java.util.*;

public class DiagnosticHandler {

    private HashMap<String, List<Diagnostic>> Diagnostics;
    private MopeLSPServer server;

    public DiagnosticHandler(MopeLSPServer server){
        this.server = server;
        Diagnostics = new HashMap<>();
    }
    /**
     * <p>This method should be called when Diagnostics are added, it propagates diagnostic data to all clients</p>
     */
    public void publishDiagnostics(){
        for(String location : Diagnostics.keySet()){
            var params = new PublishDiagnosticsParams();
            params.setUri(location);
            params.setDiagnostics(Diagnostics.get(location));
            server.publishDiagnosticsToAllClients(params);
        }
    }

    /**
     * <p>Adds diagnostic data to the list containing diagnostics data and publishes the list</p>
     * @param diagnostics
     */
    public void addDiagnostics(List<ModelicaDiagnostic> diagnostics){
        if(diagnostics.isEmpty()) return;
        for(ModelicaDiagnostic dia : diagnostics){

            if(Diagnostics.containsKey(dia.getUri())) Diagnostics.get(dia.getUri()).add(dia);
            else {
                Diagnostics.put(dia.getUri(), new ArrayList<>());
                Diagnostics.get(dia.getUri()).add(dia);
            }
        }
        publishDiagnostics();
    }

    /**
     * <p>Empties the whole diagnostic list</p>
     */
    public void clearDiagnostics(){
        Diagnostics.clear();
    }

}
