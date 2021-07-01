package Server.Compiler;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModelicaDiagnostic extends Diagnostic {
    private static final Logger logger = LoggerFactory.getLogger(OMCAdapter.class);
    final static Pattern hasError = Pattern.compile("Error:");
    final static Pattern errorMessage = Pattern.compile("Error:.*[\\n\\]]*");
    final static Pattern location = Pattern.compile("\\[([^\\[]*\\..+):([0-9]*:[0-9]*)-([0-9]*:[0-9]*):.*]");

    private String uri;

    private ModelicaDiagnostic(String str, DiagnosticSeverity severity){
        setSeverity(severity);
        parseErrorString(str);
    }

    private ModelicaDiagnostic(){
        addEmptyRange();
        addDefaultLocation();
    }

    private static ModelicaDiagnostic ModelicaErrorDiagnostic(String str){
        return new ModelicaDiagnostic(str, DiagnosticSeverity.Error);
    }

    public static List<ModelicaDiagnostic>CreateDiagnostics(String str){
        ArrayList<ModelicaDiagnostic> diagnostics = new ArrayList<>();
        Matcher hasErrorMatcher = hasError.matcher(str);
        if(hasErrorMatcher.find()) diagnostics.add(ModelicaErrorDiagnostic(str));
        return diagnostics;
    }

    public static List<ModelicaDiagnostic>CreateModelNotLoadedDiagnostic(String modelName, boolean topLevelLoaded){
        ArrayList<ModelicaDiagnostic> diagnostics = new ArrayList<>();
        ModelicaDiagnostic diagnostic = new ModelicaDiagnostic();
        String message = "Could not load Model " + modelName;
        if(topLevelLoaded) message += ", loaded top level model instead";
        diagnostic.setMessage(message);
        diagnostics.add(diagnostic);
        return diagnostics;
    }

    private void addEmptyRange(){
        Range errorRange = new Range();
        Position startP = new Position();
        Position endP = new Position();
        startP.setLine(0);
        startP.setCharacter(0);
        endP.setLine(0);
        endP.setCharacter(0);
        errorRange.setStart(startP);
        errorRange.setEnd(endP);
        setRange(errorRange);
    }

    private void addDefaultLocation(){
        //TODO maybe add Workspace Location
        uri = "path/to/project";
        return;
    }

    public static void parseResultString(String str){

    }

    public String getUri(){
        return uri;
    }

    /**
     * Reads Important Information from ErrorString and
     * stores Information in Diagnostic Attributes
     * @param str the Error String returned from OMC
     */
    public void parseErrorString(String str){
        _parseErrorMessage(str);
        _parseErrorLocation(str);
    }

    private void _parseErrorMessage(String str){
        Matcher m = errorMessage.matcher(str);
        //Todo Review if necessary to find multiple Occurrences
        if(m.find()) setMessage(m.group(0));
        logger.debug("Message: " + getMessage());
    }

    private void _parseErrorLocation(String str){
        Matcher m = location.matcher(str);
        if(m.find()){
            uri = m.group(1);
            logger.debug("Path: " + uri);
            _parseErrorRange(m.group(2), m.group(3));
        }
    }

    private void _parseErrorRange(String start, String end){
        Range errorRange = new Range();
        Position startP = new Position();
        Position endP = new Position();
        String[] startRange = start.split(":");
        String[] endRange = end.split(":");



        startP.setLine(Integer.parseInt(startRange[0]));
        startP.setCharacter(Integer.parseInt(startRange[1]));
        endP.setLine(Integer.parseInt(endRange[0]));
        endP.setCharacter(Integer.parseInt(endRange[1]));

        errorRange.setStart(startP);
        errorRange.setEnd(endP);
        setRange(errorRange);
        logger.debug("Range: " + getRange().toString());
    }
}
