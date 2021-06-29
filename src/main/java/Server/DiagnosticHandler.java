package Server;

import Server.Compiler.ModelicaDiagnostic;
import org.eclipse.lsp4j.Location;

import java.util.*;

public class DiagnosticHandler {


    private HashMap<String, List<ModelicaDiagnostic>> Diagnostics;

    public DiagnosticHandler(){
        Diagnostics = new HashMap<>();
    }

    public void addDiagnostics(List<ModelicaDiagnostic> diagnostics){
        for(ModelicaDiagnostic dia : diagnostics){

            if(Diagnostics.containsKey(dia.getUri())) Diagnostics.get(dia.getUri()).add(dia);
            else {
                Diagnostics.put(dia.getUri(), new ArrayList<>());
                Diagnostics.get(dia.getUri()).add(dia);
            }
        }
    }

    public void clearDiagnostics(){
        Diagnostics.clear();
    }

}
