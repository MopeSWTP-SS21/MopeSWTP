package Server.Compiler;

import omc.corba.Result;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModelicaDiagnostic extends Diagnostic {
    final static Pattern errorMessage = Pattern.compile("Error:\\s[.]*\\]");
    final static Pattern location = Pattern.compile("\\[ [.*]\\.mo:[0-9]*:[0-9]*-[0-9]*:[0-9]*:.*\\]");
    final static Pattern range = Pattern.compile(":[0-9]*:[0-9]*-[0-9]*:[0-9]*:");


    public ModelicaDiagnostic(Result result){
        parseErrorString(result.error.toString());
    }

    public static List<ModelicaDiagnostic>CreateDiagnostics(Result result){
        ArrayList<ModelicaDiagnostic> diagnostics = new ArrayList<>();
        if(result.error.isPresent()){
            diagnostics.add(new ModelicaDiagnostic(result));
        }
        return diagnostics;
    }

    public static void parseResultString(String str){

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
        if(m.find()) setMessage(m.group(1));
    }

    private void _parseErrorLocation(String str){
        //TODO setUri
        Matcher m = location.matcher(str);
        if(m.find()){
            String[] loc = m.group(1).split(":", 2);
            _parseErrorRange(loc[1]);
        }
    }

    private void _parseErrorRange(String str){
        Range errorRange = new Range();
        Position start = new Position();
        Position end = new Position();
        String[] rangeValues = str.split(":");

        start.setLine(Integer.parseInt(rangeValues[0]));
        start.setCharacter(Integer.parseInt(rangeValues[1]));
        end.setLine(Integer.parseInt(rangeValues[2]));
        end.setCharacter(Integer.parseInt(rangeValues[3]));

        errorRange.setStart(start);
        errorRange.setEnd(end);
        setRange(errorRange);
    }
}
