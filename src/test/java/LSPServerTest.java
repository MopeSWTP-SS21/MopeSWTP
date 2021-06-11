import Client.ConsoleClientLauncher;
import Client.MopeLSPClient;
import Server.MopeLSPServer;
import Server.MopeLSPServerLauncher;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LSPServerTest{

    private  Future<Void> serverListening;
    private  Future<Void> clientListening;

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
    public void startServer() throws IOException {
        new Thread(() -> {
            try {
                serverListening = serverLauncher.LaunchServer();
                //test.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    @BeforeAll
    public void startClient() throws IOException {
        new Thread(() -> {
            try {
                clientListening = clientLauncher.LaunchClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    public void initializeServer() throws IOException, InterruptedException {
        Thread.currentThread().sleep(1000);
        clientLauncher.client.initServer();
        Thread.currentThread().sleep(15000);
    }
    // Tests the current OMC Compilerversion and checks the result the server is responding
    @Test
    public void getOMCVersion() throws IOException, InterruptedException {
        initializeServer();
        assertEquals("V 1.17.0",clientLauncher.client.compilerVersion());
    }
    // Tests the default modelicapath and checks the result the server is responding
    @Test
    public void showModelicaPath() throws IOException, InterruptedException {
        initializeServer();
        assertEquals("Result [result=\"/usr/bin/../lib/omlibrary:/home/swtp/.openmodelica/libraries/\", error=Optional.empty]", clientLauncher.client.modelicaPath());
    }
    // Tests the adding of a new modelicafolder to the modelicapath and checks the result the server is responding
    @Test
    public void addFolderToModPathAndShow() throws IOException, InterruptedException {
        initializeServer();
        clientLauncher.client.addPath("/home/swtp/modelica/exampleModels");
        assertEquals("Result [result=\"/usr/bin/../lib/omlibrary:/home/swtp/.openmodelica/libraries/:/home/swtp/modelica/exampleModels\", error=Optional.empty]",clientLauncher.client.modelicaPath());
    }
    // Tests the loading of a modelica file and checks the result the server is responding
    @Test
    public void loadFile() throws IOException, InterruptedException {
        initializeServer();
        clientLauncher.client.addPath("/home/swtp/modelica/exampleModels");
        assertEquals("Result [result=true, error=Optional.empty]",clientLauncher.client.loadFile("/home/swtp/modelica/exampleModels/FunctionNames.mo"));
    }
    //Tests the loading of a model and checks the result the server is responding
    @Test
    public void loadModel() throws IOException, InterruptedException {
        initializeServer();
        clientLauncher.client.addPath("/home/swtp/modelica/exampleModels");
        clientLauncher.client.loadFile("/home/swtp/modelica/exampleModels/FunctionNames.mo");
        assertEquals("Result [result=true, error=Optional.empty]",clientLauncher.client.loadModel("FunctionNames"));
    }
    //Tests the checking of a correct model and checks the result, the server is responding
    @Test
    public void checkModel() throws IOException, InterruptedException {
        initializeServer();
        clientLauncher.client.addPath("/home/swtp/modelica/exampleModels");
        clientLauncher.client.loadFile("/home/swtp/modelica/exampleModels/FunctionNames.mo");
        clientLauncher.client.loadModel("FunctionNames");
        assertEquals("Model FunctionNames checked\n" +
                "->\"Check of FunctionNames completed successfully.\n" +
                "Class FunctionNames has 3 equation(s) and 3 variable(s).\n" +
                "1 of these are trivial equation(s).\"",clientLauncher.client.checkModel(("FunctionNames")));
    }
}