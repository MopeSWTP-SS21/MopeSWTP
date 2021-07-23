import Client.ConsoleClientLauncher;

import Server.MopeLSPServer;
import Server.MopeLSPServerLauncher;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;


import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LSPServerTest{


    private static final Logger logger = LoggerFactory.getLogger(MopeLSPServer.class);
    private final CompletableFuture<Boolean> testsFinished = new CompletableFuture<>();

    private String userName;
    private String refPath;

    @BeforeAll
    public void getCurrentUserAndSetRefPath() {
         userName = System.getProperty("user.name");
         refPath = System.getProperty("user.dir") + "/src/test/java/resources/exampleModels";
    }


    MopeLSPServerLauncher serverLauncher;
    {
        try {
            serverLauncher = new MopeLSPServerLauncher();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ConsoleClientLauncher clientLauncher;
    {
        try {
            clientLauncher = new ConsoleClientLauncher("127.0.0.1",4200);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @BeforeAll
    public void startServer(){
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
    @BeforeAll
    public void startClient() {
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

    public void initializeServer() throws InterruptedException {
        Thread.currentThread().sleep(1000);
        clientLauncher.client.initServer();
        Thread.currentThread().sleep(15000);
    }

    // Tests the current OMC Compilerversion and checks the result the server is responding
    @Test
    public void getOMCVersion() throws InterruptedException {
        initializeServer();
        assertEquals("V 1.17.0",clientLauncher.client.compilerVersion());
    }
    // Tests the default modelicapath and checks the result the server is responding
    @Test
    public void showModelicaPath() throws IOException, InterruptedException {
        initializeServer();
        assertEquals("Result [result=\"/usr/bin/../lib/omlibrary:/home/"+userName+"/.openmodelica/libraries/\", error=Optional.empty]", clientLauncher.client.modelicaPath());
    }
    // Tests the adding of a new modelicafolder to the modelicapath and checks the result the server is responding
    @Test
    public void addFolderToModPathAndShow() throws IOException, InterruptedException {
        initializeServer();
        clientLauncher.client.addPath(refPath);
        assertEquals("Result [result=\"/usr/bin/../lib/omlibrary:/home/"+userName+"/.openmodelica/libraries/:"+refPath+"\", error=Optional.empty]",clientLauncher.client.modelicaPath());
    }
    // Tests the loading of a modelica file and checks the result the server is responding
    @Test
    public void loadFile() throws IOException, InterruptedException {
        initializeServer();
        clientLauncher.client.addPath(refPath);
        assertEquals("Result [result=true, error=Optional.empty]",clientLauncher.client.loadFile(refPath+"/"+"FunctionNames.mo"));
    }
    //Tests the loading of a model and checks the result the server is responding
    @Test
    public void loadModel() throws IOException, InterruptedException {
        initializeServer();
        clientLauncher.client.addPath(refPath);
        clientLauncher.client.loadFile(refPath+"/"+"FunctionNames.mo");
        assertEquals("Result [result=true, error=Optional.empty]",clientLauncher.client.loadModel("FunctionNames"));
    }
    //Tests the checking of a correct model and checks the result, the server is responding
    @Test
    public void checkModel() throws IOException, InterruptedException {
        initializeServer();
        clientLauncher.client.addPath(refPath);
        clientLauncher.client.loadFile(refPath+"/"+"FunctionNames.mo");
        clientLauncher.client.loadModel("FunctionNames");
        assertEquals("Model FunctionNames checked\n" +
                "->\"Check of FunctionNames completed successfully.\n" +
                "Class FunctionNames has 3 equation(s) and 3 variable(s).\n" +
                "1 of these are trivial equation(s).\"",clientLauncher.client.checkModel(("FunctionNames")));
    }
    @AfterAll
    public void endTests(){
        logger.info("All tests done... Completing Future");
        testsFinished.complete(true);
    }
}
