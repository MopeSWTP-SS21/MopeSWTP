import Client.ConsoleClientLauncher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
/**
 * This TestClass contains a lot of tests to test the functionality of the sendExpression Command
 * In each Test the client sends an ModelicaScriptingExpression to the MopeLSPServer
 * The Answer from the Server is used to validate if the Test has passed
 */
public class SendExpressionIntegrationTest extends ServerIntegrationTest {
    private final Logger _logger = LoggerFactory.getLogger(SendExpressionIntegrationTest.class);
    @Override
    protected Logger getLogger() {
        return _logger;
    }

    /**
     * this Test tries to set the CommandLineOption "--unitChecking"
     * passes if returns true
     */
    @Test
    public void setCommandLineOptions_d_newInst_nfAPI(){
        assertEquals("true", ConsoleClientLauncher.client.sendExpression("setCommandLineOptions(\"-d=newInst,nfAPI\")") );

    }

    /**
     * this Test tries to set the CommandLineOption "--unitChecking"
     * passes if returns true
     */
    @Test
    public void setCommandLineOptions_unitChecking(){
        assertEquals("true", ConsoleClientLauncher.client.sendExpression("setCommandLineOptions(\"--unitChecking\")") );

    }

    /**
     * this Test requests the simulationOptions for Model Modelica.Electrical.Analog.Examples.Rectifier
     * passes if the returned SimulationOptions are correct
     */
    @Test
    public void getSimulationOptionsForValidModel(){
        assertEquals("(0.0,0.1,1e-06,10000,1e-05)", ConsoleClientLauncher.client.sendExpression("getSimulationOptions(Modelica.Electrical.Analog.Examples.Rectifier)") );

    }

    /**
     * simulates Model Modelica.Electrical.Analog.Examples.Rectifier
     * Passes if a SimulationResult is returned
     */
    @Test
    public void retrieveSimulationResultsForValidModel(){
        //TODO Why wont this work? "record SimulationResult[\\n.]+end SimulationResult;"
        Assertions.assertTrue(ConsoleClientLauncher.client.sendExpression("simulate(Modelica.Electrical.Analog.Examples.Rectifier)").toString().matches(
                "record SimulationResult\\n.+\\n.+\\n.+\\n.+\\n.+\\n.+\\n.+\\n.+\\n.+\\n.+\\n.+\\n.+\\nend SimulationResult;"
        ));
    }

    /**
     * Sets the current OMCpath to "/home"
     * Passes if it worked
     */
    @Test
    public void cdToHome(){
        assertEquals("\"/home\"", ConsoleClientLauncher.client.sendExpression("cd(\"/home\")") );
    }

    /**
     * this test sets the current omcPath to "/home" and retrieves the new current omcPath
     * passes if "/home" is returned
     */
    @Test
    public void readCurrentPathWithCD(){
        ConsoleClientLauncher.client.sendExpression("cd(\"/home\")");
        assertEquals("\"/home\"", ConsoleClientLauncher.client.sendExpression("cd()") );
    }
    /**
     * This Test tries to execute the non existing ApiMethod unknownAPIMethod()
     * Passes if an Error is returned
     */
    @Test
    public void retrieveErrorForExecutingUnknownAPIMethod(){
        String result = ConsoleClientLauncher.client.sendExpression("unknownAPIMethod()").toString();
        Assertions.assertTrue(result.contains("Error: Class unknownAPIMethod not found in scope "));
    }

    /**
     * This Test tries to load the non existent model "FooBar"
     * Passes if an Error is Returned
     */
    @Test void retrieveErrorForLoadingNonExistentModel(){
        String result = ConsoleClientLauncher.client.sendExpression("loadModel(FooBar)").toString();
        assertTrue(result.contains("Error: Failed to load package FooBar (default) using MODELICAPATH"));
    }
    /**
     * This test sends a sendExpression command to the server to load ModelicaStandardLibrary Version 3.2.3
     * Passes when the Answer from the Server is "true"
     */
    @Test
    public void successfullyLoadMSL_3_2_3(){
        assertEquals("true", ConsoleClientLauncher.client.sendExpression("loadModel(Modelica, {\"3.2.3\"})") );
    }
}
