import Client.ConsoleClientLauncher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SendExpressionIntegrationTest extends ServerIntegrationTest {
    @Test
    public void setCommandLineOptions_d_newInst_nfAPI(){
        assertEquals("true", ConsoleClientLauncher.client.sendExpression("setCommandLineOptions(\"-d=newInst,nfAPI\")") );

    }
    @Test
    public void setCommandLineOptions_unitChecking(){
        assertEquals("true", ConsoleClientLauncher.client.sendExpression("setCommandLineOptions(\"--unitChecking\")") );

    }
    @Test
    public void getSimulationOptionsForValidModel(){
        assertEquals("(0.0,0.1,1e-06,10000,1e-05)", ConsoleClientLauncher.client.sendExpression("getSimulationOptions(Modelica.Electrical.Analog.Examples.Rectifier)") );

    }

    //TODO Why wont this work? "record SimulationResult[\\n.]+end SimulationResult;"
    @Test
    public void retrieveSimulationResultsForValidModel(){
        Assertions.assertTrue(ConsoleClientLauncher.client.sendExpression("simulate(Modelica.Electrical.Analog.Examples.Rectifier)").toString().matches(
                "record SimulationResult\\n.+\\n.+\\n.+\\n.+\\n.+\\n.+\\n.+\\n.+\\n.+\\n.+\\n.+\\n.+\\nend SimulationResult;"
        ));
    }
    @Test
    public void cdToHome(){
        assertEquals("\"/home\"", ConsoleClientLauncher.client.sendExpression("cd(\"/home\")") );

    }
    @Test
    public void readCurrentPathWithCD(){
        assertEquals("\"/home\"", ConsoleClientLauncher.client.sendExpression("cd()") );
    }
    @Test
    public void retrieveErrorForExecutingUnknownAPIMethod(){
        assertEquals(
                "[<interactive>:1:1-1:18:writable] Error: Class unknownAPIMethod not found in scope <global scope> (looking for a function or record).\n",
                ConsoleClientLauncher.client.sendExpression("unknownAPIMethod()")
        );
    }
    @Test void retrieveErrorForLoadingNonExistentModel(){
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
    public void successfullyLoadMSL_3_2_3(){
        assertEquals("true", ConsoleClientLauncher.client.sendExpression("loadModel(Modelica, {\"3.2.3\"})") );
    }
}
