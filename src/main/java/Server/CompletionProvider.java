package Server;

import Server.Compiler.ICompilerAdapter;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.CompletionParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CompletionProvider {

    /**
     * These Characters mark the beginning of a possible word
     */
    private static char[] limiter = {' ', ';', '\n'};
    private static final Logger logger = LoggerFactory.getLogger(CompletionProvider.class);

    public static List<CompletionItem> complete(CompletionParams params, ICompilerAdapter compiler) throws FileNotFoundException {
        List<CompletionItem> list;
        String path = params.getTextDocument().getUri();
        String triggerChar = params.getContext().getTriggerCharacter();
        logger.info("Completion triggered by Char: '" + triggerChar + "'" );
        int line = params.getPosition().getLine();
        int col = params.getPosition().getCharacter();
        //if(triggerChar == "."){
            String word = findCompletableItem(path, line, col);
            logger.info("Read word " + word + " from file " + path);
            list = generateModelCompletionsWithPoint(compiler, word);
        //}
       // else{
            //list = new ArrayList<>();
        //}
        return list;
    }
    /**
     * Looks up the File located in the given Path and determines
     * the part that should be completed
     * @param path path to file
     * @param line line where completion should happen
     * @param col column where completion should happen
     * @return the uncompleted word
     * @throws FileNotFoundException
     */
    private static String findCompletableItem(String path, int line, int col) throws FileNotFoundException {

        Scanner s = new Scanner(new File(path));
        while(line > 1){ //TODO maybe Zero
            s.nextLine();
        }
        String selectedLine = s.nextLine();
        logger.info("Searching through line \"" + selectedLine +"\"");
        String symbol = "";
        StringBuilder builder = new StringBuilder("");
        //boolean run = true;
        char current;
        while(col >= 0){
            current = selectedLine.charAt(col);
            if(Arrays.asList(limiter).contains(current) ) break;
            builder.append(current);
            //symbol+= current;
            col--;
        }
        symbol = builder.reverse().toString();
        logger.info("Found Symbol \"" + symbol +"\"");

        return symbol;
    }

    /**
     * Completes ClassNames after .
     * @param compiler the CompilerAdapter used to search for classes
     * @param toComplete the already typed "Higher-Level-Package-Name"
     * @return List of Lsp-CompletionItems containing ModelNames
     */
    private static List<CompletionItem> generateModelCompletionsWithPoint(ICompilerAdapter compiler, String toComplete){
        List<CompletionItem> result = new ArrayList<CompletionItem>();
        List<String> classes = compiler.getLoadedClassNames(toComplete);
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
         String lable;
         if(levels.length > 0) lable = levels[levels.length - 1];
         else lable = modelName;
         item.setKind(CompletionItemKind.Class);
         item.setInsertText(modelName);
         item.setLabel(lable);
         item.setDetail(compiler.getClassComment(modelName));
         return item;
    }

}
