import Client.ConsoleClientLauncher;
import Server.MopeLSPServerLauncher;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public abstract class ServerIntegrationTest {

    //TODO Why does this result in NullPointerException?
    // private Logger logger = getLogger();
    protected static Logger logger =  LoggerFactory.getLogger(ServerIntegrationTest.class);
    private static final CompletableFuture<Boolean> testsFinished = new CompletableFuture<>();
    protected static String userName;
    protected static String refPath;
    protected static String modelicaPath;

    abstract Logger getLogger();

    static MopeLSPServerLauncher serverLauncher;
    {
        try {
            serverLauncher = new MopeLSPServerLauncher();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static ConsoleClientLauncher clientLauncher;
    {
        try {
            clientLauncher = new ConsoleClientLauncher("localhost",4200);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @BeforeAll
    static void setupTestEnvironment(){
        readSystemProperties();
        startServer();
        startClient();
        initializeServer();
        storeOriginalModelicaPath();
    }

    private static void readSystemProperties() {
        userName = System.getProperty("user.name");
        refPath = System.getProperty("user.dir") + "/src/test/java/resources/exampleModels";
    }
    private static void startServer(){
        new Thread(() -> {
            try {
                serverLauncher.LaunchServer();
                testsFinished.get();
                logger.info("Server Thread finishing...");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void startClient() {
        new Thread(() -> {
            try {
                clientLauncher.LaunchClient();
                testsFinished.get();
                logger.info("Client Thread finishing...");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void initializeServer() {
        try {
            //TODO i am sure there is a better way to wait for everything to be set up
            // everything <= 1000 results in NullPointerException
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ConsoleClientLauncher.client.initServer();
    }

    private static void storeOriginalModelicaPath(){
        modelicaPath = "/usr/bin/../lib/omlibrary:/home/"+userName+"/.openmodelica/libraries/";
    }
    @BeforeEach
    public void resetModelicaPath(){
        ConsoleClientLauncher.client.sendExpression("setModelicaPath(\"" + modelicaPath + "\")");
    }

    @AfterAll
    public void endTests(){
        //todo Shutdown server and client properly
        logger.info("All tests done... Completing Future");
        testsFinished.complete(true);
    }
}
