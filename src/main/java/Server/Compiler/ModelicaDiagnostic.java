package Server.Compiler;

import omc.corba.Result;
import org.eclipse.lsp4j.Diagnostic;
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
    final static Pattern errorMessage = Pattern.compile("Error:.*[\\n\\]]*");
    final static Pattern location = Pattern.compile("\\[([^\\[]*\\.mo):([0-9]*:[0-9]*)-([0-9]*:[0-9]*):.*]");

    private String uri;
    private ModelicaDiagnostic(Result result){

        parseErrorString(result.error.toString());
    }
    private ModelicaDiagnostic(String str){
        parseErrorString(str);
    }



    public static List<ModelicaDiagnostic>CreateDiagnostics(String str){
        ArrayList<ModelicaDiagnostic> diagnostics = new ArrayList<>();
        diagnostics.add(new ModelicaDiagnostic(str));
        return diagnostics;
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
