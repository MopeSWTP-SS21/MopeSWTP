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
    private String modelicaPath;



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
    // Tests the current OMC Compilerversion and checks the result the server is responding
    @Test
    public void getOMCVersion() {
        assertEquals("V 1.17.0", ConsoleClientLauncher.client.compilerVersion());
    }
    // Tests the default modelicapath and checks the result the server is responding
    @Test
    public void showModelicaPath() {
        assertEquals("Result [result=\""+modelicaPath+"\", error=Optional.empty]", ConsoleClientLauncher.client.modelicaPath());
    }
    // Tests the adding of a new modelicafolder to the modelicapath and checks the result the server is responding
    @Test
    public void addFolderToModPathAndShow() {
        ConsoleClientLauncher.client.addPath(refPath);
        assertEquals("Result [result=\"/usr/bin/../lib/omlibrary:/home/"+userName+"/.openmodelica/libraries/:"+refPath+"\", error=Optional.empty]", ConsoleClientLauncher.client.modelicaPath());
    }


    // Tests the loading of a modelica file and checks the result the server is responding
    @Test
    public void loadFile()  {
        ConsoleClientLauncher.client.addPath(refPath);
        assertEquals("Result [result=true, error=Optional.empty]", ConsoleClientLauncher.client.loadFile(refPath+"/"+"FunctionNames.mo"));
    }
    //Tests the loading of a model and checks the result the server is responding
    @Test
    public void loadModel(){
        ConsoleClientLauncher.client.addPath(refPath);
        assertEquals("Result [result=true, error=Optional.empty]", ConsoleClientLauncher.client.loadModel("FunctionNames"));
    }
    //Tests the checking of a correct model and checks the result, the server is responding
    @Test
    public void checkModel() {

        ConsoleClientLauncher.client.addPath(refPath);
        ConsoleClientLauncher.client.loadModel("FunctionNames");
        assertEquals("Model FunctionNames checked\n" +
                "->\"Check of FunctionNames completed successfully.\n" +
                "Class FunctionNames has 3 equation(s) and 3 variable(s).\n" +
                "1 of these are trivial equation(s).\"", ConsoleClientLauncher.client.checkModel(("FunctionNames")));
    }

    @Test
    public void sendExpression02(){
        assertEquals("true", ConsoleClientLauncher.client.sendExpression("setCommandLineOptions(\"-d=newInst,nfAPI\")") );

    }
    @Test
    public void sendExpression03(){
        assertEquals("true", ConsoleClientLauncher.client.sendExpression("setCommandLineOptions(\"--unitChecking\")") );

    }
    @Test
    public void sendExpression04(){
        assertEquals("(0.0,0.1,1e-06,10000,1e-05)", ConsoleClientLauncher.client.sendExpression("getSimulationOptions(Modelica.Electrical.Analog.Examples.Rectifier)") );

    }

    //TODO Why wont this work? "record SimulationResult[\\n.]+end SimulationResult;"
    @Test
    public void sendExpression05(){
        Assertions.assertTrue(ConsoleClientLauncher.client.sendExpression("simulate(Modelica.Electrical.Analog.Examples.Rectifier)").toString().matches(
                "record SimulationResult\\n.+\\n.+\\n.+\\n.+\\n.+\\n.+\\n.+\\n.+\\n.+\\n.+\\n.+\\n.+\\nend SimulationResult;"
        ));
    }
    @Test
    public void sendExpression06(){
        assertEquals("\"/home\"", ConsoleClientLauncher.client.sendExpression("cd(\"/home\")") );

    }
    @Test
    public void sendExpression07(){
        assertEquals("\"/home\"", ConsoleClientLauncher.client.sendExpression("cd()") );
    }
    @Test
    public void sendExpression08(){
        assertEquals(
                "[<interactive>:1:1-1:18:writable] Error: Class unknownAPIMethod not found in scope <global scope> (looking for a function or record).\n",
                ConsoleClientLauncher.client.sendExpression("unknownAPIMethod()")
        );
    }
    @Test void sendExpression09(){
        assertEquals(
                "[/home/"+userName+"/.openmodelica/libraries/index.json:0:0-0:0:readonly] Error: The package index /home/"+userName+"/.openmodelica/libraries/index.json could not be parsed.\n" +
                        "Error: Failed to load package FooBar (default) using MODELICAPATH /usr/bin/../lib/omlibrary:/home/"+userName+"/.openmodelica/libraries/.",
                ConsoleClientLauncher.client.sendExpression("loadModel(FooBar)")
        );
    }
    /**
     * This test sends a sendExpression command to the server to load ModelicaStandardLibrary Version 3.2.3
     */
    @Test
    public void sendExpression10(){
        assertEquals("true", ConsoleClientLauncher.client.sendExpression("loadModel(Modelica, {\"3.2.3\"})") );
    }



    @AfterAll
    public void endTests(){
        //todo Shutdown server and client properly
        logger.info("All tests done... Completing Future");
        testsFinished.complete(true);
    }
}
