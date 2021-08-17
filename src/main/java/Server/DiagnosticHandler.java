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

    public void publishDiagnostics(){

        for(String location : Diagnostics.keySet()){
            var params = new PublishDiagnosticsParams();
            params.setUri(location);
            params.setDiagnostics(Diagnostics.get(location));
            server.publishDiagnosticsToAllClients(params);
        }

    }

    public void addDiagnostics(List<ModelicaDiagnostic> diagnostics){
        if(diagnostics.isEmpty()) {publishDiagnostics(); return;}
        for(ModelicaDiagnostic dia : diagnostics){

            if(Diagnostics.containsKey(dia.getUri())) Diagnostics.get(dia.getUri()).add(dia);
            else {
                Diagnostics.put(dia.getUri(), new ArrayList<>());
                Diagnostics.get(dia.getUri()).add(dia);
            }
        }
        publishDiagnostics();
    }

    public void clearDiagnostics(){
        for(String key : Diagnostics.keySet()){
            Diagnostics.get(key).clear();
        }
    }

}
