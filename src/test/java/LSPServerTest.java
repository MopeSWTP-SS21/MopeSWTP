import Client.ConsoleClientLauncher;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LSPServerTest extends ServerIntegrationTest{
    private Logger _logger = LoggerFactory.getLogger(this.getClass());
    
    @Override
    protected Logger getLogger() {
        return _logger;

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
    public void loadFile() throws ExecutionException {
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

    public LSPServerTest(){
        this.logger = LoggerFactory.getLogger(this.getClass());
    }
}
