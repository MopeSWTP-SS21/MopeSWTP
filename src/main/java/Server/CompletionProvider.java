package Server;

import Server.Compiler.ICompilerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.CompletionParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class CompletionProvider {

    /**
     * These Characters mark the beginning of a possible symbol
     */
    private static final char[] limiter = {'\t',' ', ';', '\n',};
    private static final Logger logger = LoggerFactory.getLogger(CompletionProvider.class);

    public static List<CompletionItem> complete(CompletionParams params, ICompilerAdapter compiler) throws FileNotFoundException {
        List<CompletionItem> list = new ArrayList<CompletionItem>();
        String path = params.getTextDocument().getUri();
        String triggerChar = params.getContext().getTriggerCharacter();
        logger.info("Completion triggered by Char: '" + triggerChar + "'" );
        int line = params.getPosition().getLine();
        int col = params.getPosition().getCharacter();
        String symbol = findCompletableSymbol(path, line, col);
        int symbolType = getSymbolType(symbol);
        logger.info("Symbol \"" + symbol + "\" is of type " + symbolType);
        switch(symbolType){
            case 1 :
            case 3 :
                list.addAll(
                        generateUncompletePackageCompletion(
                                compiler,
                                symbol
                        )
                );
                break;
            case 2 :
                list.addAll(
                    generateFullPackageCompletions(
                        compiler,
                        prepareSymbolForModelCompletion(symbol)
                    )
                );
                break;
            default:
                logger.info("Can not complete Symbol \"" + symbol + "\" because it is of unknown type" );
                break;
        }
        return list;
    }

    /**
     * Checks if a symbol match a regex and returns an Integer indicating the type of the symbol
     * @param symbol the symbol that should be checked
     * @return 0 for unknown Symbol Type
     */
    private static int getSymbolType(String symbol){
        if(symbol.matches("[A-Za-z0-9_]+")) return 1; //(Uncomplete) FirstLevel Package/Model Identifier
        if(symbol.matches("([A-Za-z0-9_]+\\.)+")) return 2; //Complete Upperlevel Package/Model Identifier
        if(symbol.matches("([A-Za-z0-9_]+\\.)+[A-Za-z0-9]+")) return 3; //Uncomplete Upperlevel Package/Model Identifier
        return 0;
    }

    /**
     * Looks up the File located in the given Path and determines
     * the part that should be completed
     * @param URI URI to file
     * @param line line where completion should happen
     * @param col column where completion should happen
     * @return the uncompleted symbol
     * @throws FileNotFoundException
     */
    private static String findCompletableSymbol(String URI, int line, int col) throws FileNotFoundException {
        col--; //TODO: Not sure how Client count Columns. Gedit Texteditor starts with 1...

        String selectedLine = "";
        try {
            selectedLine = readLine(URI, line);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        String symbol = "";
        StringBuilder builder = new StringBuilder("");
        char current;
        while(col >= 0){
            current = selectedLine.charAt(col);
            if(Arrays.asList(limiter).contains(current) || Character.isSpaceChar(current)) break;
            builder.append(current);
            col--;
        }
        symbol = builder.reverse().toString();
        logger.info("Found Symbol \"" + symbol +"\"");

        return symbol;
    }

    private static String readLine(String filename, int line) throws IOException, URISyntaxException {
        URI uri = new URI(filename);
        Path path = Paths.get(uri.getPath());

        String selectedLine;
        try(Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8)) {
            selectedLine = lines.skip(line).findFirst().orElse("");
        }
        logger.info("Searching through line \"" + line + " : " + selectedLine +"\"");
        return selectedLine;
    }

    /**
     * Chops a possible '.' at the end of the Symbol, to generate searchable Packagename
     * @param symbol
     * @return prepared Symbol
     */

    private static String prepareSymbolForModelCompletion(String symbol){
        if(symbol.charAt(symbol.length() - 1) == '.'){
            symbol = StringUtils.chop(symbol);
        }
        return symbol;
    }
    /**
     * Completes ClassNames after .
     * @param compiler the CompilerAdapter used to search for classes
     * @param toComplete the already typed "Higher-Level-Package-Name"
     * @return List of Lsp-CompletionItems containing ModelNames
     */
    private static List<CompletionItem> generateFullPackageCompletions(ICompilerAdapter compiler, String toComplete){
        List<CompletionItem> result = new ArrayList<CompletionItem>();
        List<String> classes = compiler.getLoadedClassNames(toComplete);
        for (String cl: classes) {
            result.add(generateModelCompletionItem(cl, compiler));
        }
        return result;
    }
    private static List<CompletionItem> generateUncompletePackageCompletion(ICompilerAdapter compiler, String toComplete){
        List<CompletionItem> result = new ArrayList<CompletionItem>();
        List<String> classes = compiler.searchLoadedClassNames(toComplete);
        for (String cl: classes) {
            result.add(generateModelCompletionItem(cl, compiler));
        }
        return result;
    }

    /**
     * Turns Modelica Classname into a CompletionItem
     * @param modelName
     * @param compiler the CompilerAdapter used to obtain class information
     * @return Lsp-CompletionItem
     */
    private static CompletionItem generateModelCompletionItem(String modelName, ICompilerAdapter compiler){
         CompletionItem item = new CompletionItem();
         String[] levels = modelName.split("." , 0);
         String label;
         if(levels.length > 0) label = levels[levels.length - 1];
         else label = modelName;
         item.setKind(CompletionItemKind.Class);
         item.setInsertText(modelName);
         item.setLabel(label);
         String detail = "Comment turned Of for performance"; // compiler.getClassComment(modelName)
         item.setDetail(detail);
         return item;
    }

}
