import Client.ConsoleClientLauncher;
import Server.MopeLSPServer;
import Server.MopeLSPServerLauncher;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class ServerIntegrationTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final CompletableFuture<Boolean> testsFinished = new CompletableFuture<>();

    protected String userName;
    protected String refPath;
    protected String modelicaPath;



    MopeLSPServerLauncher serverLauncher;
    {
        try {
            serverLauncher = new MopeLSPServerLauncher(1234);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ConsoleClientLauncher clientLauncher;
    {
        try {
            clientLauncher = new ConsoleClientLauncher("localhost",1234);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @BeforeAll
    public void setupTestEnvironment(){
        readSystemProperties();
        startServer();
        startClient();
        initializeServer();
        storeOriginalModelicaPath();
    }

    private void readSystemProperties() {
        userName = System.getProperty("user.name");
        refPath = System.getProperty("user.dir") + "/src/test/java/resources/exampleModels";
    }
    private void startServer(){
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

    private void startClient() {
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

    private void initializeServer() {
        try {
            //TODO i am sure there is a better way to wait for everything to be set up
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ConsoleClientLauncher.client.initServer();
    }

    private void storeOriginalModelicaPath(){
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
