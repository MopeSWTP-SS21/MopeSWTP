package Client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class ConsoleMenu {

    public MopeLSPClient client;
    private static final Logger logger = LoggerFactory.getLogger(ConsoleClientLauncher.class);
    private final Scanner sc = new Scanner(System.in);
    private boolean running;

    private final String[] menuItems = new String[] {
            "1: Initialize server",
            "2: Get compiler version",
            "3: Load File",
            "4: Load model",
            "5: Check Model",
            "6: Instantiate Model",
            "7: Add Folder to ModelicaPath",
            "8: Show ModelicaPath",
            "9 : Exit",
            "10 : Complete",
            "11 : Get Documentation",
            "12 : send Expression"
    };
    private void switchCommand(){
        int command= sc.nextInt();
        switch(command){
            case 1:
                initializeServer();
                break;
            case 2:
                getCompilerVersion();
                break;
            case 3:
                loadFile();
                break;
            case 4:
                loadModel();
                break;
            case 5:
                checkModel();
                break;
            case 6:
                instantiateModel();
                break;
            case 9:
                running=false;
                break;
            case 7:
                addFolder();
                break;
            case 8:
                showPath();
                break;
            case 10:
                complete();
                break;
            case 11:
                getDocumentation();
                break;
            case 12:
                sendExpression();
                break;
            default:
                logger.info("wrong entry");
                break;
        }
    }

    private void initializeServer(){
        client.initServer();
    }
    private void getCompilerVersion(){
        System.out.println(client.compilerVersion());
    }
    private void loadFile(){
        System.out.print("path: ");
        System.out.println(
                client.loadFile(readUserInput())
        );
    }
    private void loadModel(){
        System.out.print("modelName: ");
        System.out.println(
                client.loadModel(readUserInput())
        );
    }
    private void checkModel(){
        System.out.print("modelName: ");
        System.out.println(
                client.checkModel(readUserInput())
        );
    }
    private void instantiateModel(){
        System.out.println("not implemented");
    }
    private void addFolder(){
        System.out.print("path: ");
        System.out.println(
                client.addPath(readUserInput())
        );
    }
    private void showPath(){
        System.out.println(client.modelicaPath());
    }
    private void complete(){
        System.out.print("File: ");
        String file = readUserInput();
        System.out.print("Line: ");
        int line = sc.nextInt();
        System.out.print("Column: ");
        int col = sc.nextInt();
        System.out.println(client.complete(file, line, col));
    }
    private void getDocumentation(){
        System.out.print("className: ");
        System.out.println(
                client.getDocumentation(readUserInput())
        );
    }
    private void sendExpression(){
        System.out.print("expression: ");
        System.out.println(
                client.executeCommand(readUserInput())
        );
    }

    /**
     * this Methods reads the user input that is intended to be send to the lspServer
     * TODO sanitize user Input
     * @return the input string
     */
    private String readUserInput(){
        var input = sc.next();
        return input;
    }
    private void printMenu(){
        for (String item: menuItems ) {
            System.out.println(item);
        }
        System.out.print(">");
    }

    public void run(){
        running = true;
        while(running)
        {
            printMenu();
            switchCommand();
        }
    }

    public ConsoleMenu(MopeLSPClient client){
        this.client = client;
    }
}
