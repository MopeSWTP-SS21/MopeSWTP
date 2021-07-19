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

    // Tests the current OMC Compilerversion and checks the result the server is responding
    @Test
    public void getOMCVersion() {
        assertEquals("V 1.17.0", ConsoleClientLauncher.client.compilerVersion());
    }
    // Tests the default modelicapath and checks the result the server is responding
    @Test
    public void showModelicaPath() {

        assertEquals("Result [result=\"/usr/bin/../lib/omlibrary:/home/"+userName+"/.openmodelica/libraries/\", error=Optional.empty]", ConsoleClientLauncher.client.modelicaPath());
    }
    // Tests the adding of a new modelicafolder to the modelicapath and checks the result the server is responding
    @Test
    public void addFolderToModPathAndShow() {
        //TODO: I don't want to initialize the Server, i just need a "clean" ModelicaPath...
        ConsoleClientLauncher.client.initServer();
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
        ConsoleClientLauncher.client.loadFile(refPath+"/"+"FunctionNames.mo");
        assertEquals("Result [result=true, error=Optional.empty]", ConsoleClientLauncher.client.loadModel("FunctionNames"));
    }
    //Tests the checking of a correct model and checks the result, the server is responding
    @Test
    public void checkModel() {

        ConsoleClientLauncher.client.addPath(refPath);
        ConsoleClientLauncher.client.loadFile(refPath+"/"+"FunctionNames.mo");
        ConsoleClientLauncher.client.loadModel("FunctionNames");
        assertEquals("Model FunctionNames checked\n" +
                "->\"Check of FunctionNames completed successfully.\n" +
                "Class FunctionNames has 3 equation(s) and 3 variable(s).\n" +
                "1 of these are trivial equation(s).\"", ConsoleClientLauncher.client.checkModel(("FunctionNames")));
    }

    /**
     * This test sends an executeCommand command to the server to request the OMC version
     */
    @Test
    public void executeCommand1(){
        assertEquals("true", ConsoleClientLauncher.client.executeCommand("loadModel(Modelica, {\"3.2.3\"})") );

    }
    @Test
    public void executeCommand2(){
        assertEquals("true", ConsoleClientLauncher.client.executeCommand("setCommandLineOptions(\"-d=newInst,nfAPI\")") );

    }
    @Test
    public void executeCommand3(){
        assertEquals("true", ConsoleClientLauncher.client.executeCommand("setCommandLineOptions(\"--unitChecking\")") );

    }
    @Test
    public void executeCommand4(){
        assertEquals("(0.0,1.0,1e-06,500,0.002)", ConsoleClientLauncher.client.executeCommand("getSimulationOptions(Modelica.Electrical.Analog.Examples.Rectifier)") );

    }
    @Test
    public void executeCommand5(){
        assertEquals(
                "record SimulationResult\n" +
                        "    resultFile = \"/tmp/OpenModelica/Modelica.Electrical.Analog.Examples.Rectifier_res.mat\",\n" +
                        "    simulationOptions = \"startTime = 0.0, stopTime = 0.1, numberOfIntervals = 10000, tolerance = 1e-06, method = 'dassl', fileNamePrefix = 'Modelica.Electrical.Analog.Examples.Rectifier', options = '', outputFormat = 'mat', variableFilter = '.*', cflags = '', simflags = ''\",\n" +
                        "    messages = \"LOG_SUCCESS       | info    | The initialization finished successfully without homotopy method.\n" +
                        "LOG_SUCCESS       | info    | The simulation finished successfully.\n" +
                        "\",\n" +
                        "    timeFrontend = 1.364086431,\n" +
                        "    timeBackend = 0.487607568,\n" +
                        "    timeSimCode = 0.05728809099999999,\n" +
                        "    timeTemplates = 0.181370335,\n" +
                        "    timeCompile = 9.320577482999999,\n" +
                        "    timeSimulation = 0.966985235,\n" +
                        "    timeTotal = 12.396849296\n" +
                        "end SimulationResult;\n",
                ConsoleClientLauncher.client.executeCommand("simulate(Modelica.Electrical.Analog.Examples.Rectifier)")
        );
    }
    @Test
    public void executeCommand6(){
        assertEquals("\"/home\"", ConsoleClientLauncher.client.executeCommand("cd(\"/home\")") );

    }
    @Test
    public void executeCommand7(){
        assertEquals("\"/home\"", ConsoleClientLauncher.client.executeCommand("cd()") );
    }
    @Test
    public void executeCommand8(){
        assertEquals(
                "[<interactive>:1:1-1:18:writable] Error: Class unknownAPIMethod not found in scope <global scope> (looking for a function or record).\n",
                ConsoleClientLauncher.client.executeCommand("unknownAPIMethod()")
        );
    }
    @Test void executeCommand9(){
        assertEquals(
                "[/home/swtp/.openmodelica/libraries/index.json:0:0-0:0:readonly] Error: The package index /home/swtp/.openmodelica/libraries/index.json could not be parsed.\n" +
                        "Error: Failed to load package FooBar (default) using MODELICAPATH /usr/bin/../lib/omlibrary:/home/swtp/.openmodelica/libraries/.\n" +
                        "",
                ConsoleClientLauncher.client.executeCommand("loadModel(FooBar)")
        );
    }



    @AfterAll
    public void endTests(){
        //todo Shutdown server and client properly
        logger.info("All tests done... Completing Future");
        testsFinished.complete(true);
    }
}
