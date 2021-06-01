package Server.Compiler;


import omc.ZeroMQClient;
import omc.corba.OMCInterface;

import omc.corba.ScriptingHelper;
import omc.ior.ZMQPortFileProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import version.Version;

import java.io.IOException;
import java.util.Optional;

public class OMCAdapter implements ICompilerAdapter{

    private static final Logger logger = LoggerFactory.getLogger(OMCAdapter.class);
    private final OMCInterface omc;

    @Override
    public String checkModel(String modelName) {
        //TODO
        //String result = omc.checkModel(modelName);
        //Result result = omc.sendExpression("model abc Real x=1; end abc;");
        Optional<String> name = ScriptingHelper.getModelName("/home/swtp/modelica/exampleModels/example.mo");
        String result2 = omc.checkModel(name.orElse("abc"));
        return "Model " + modelName + " checked\n" + "->" + result2;
    }

    @Override
    public String getCompilerVersion() {
        logger.info("Requesting OMC Version");
        Version v = omc.getVersion();
        return v.toString();
    }

    @Override
    public Boolean connect() {
        try{
            logger.info("Trying to establish OMC connection");
            omc.connect();
            return true;
        } catch (IOException e) {
            logger.error("Error during OMC Connect" ,e);
            e.printStackTrace();
        }
        return false;
    }

    public OMCAdapter(String omcExecPath, String locale, String fileProviderSuffix){
        omc = new ZeroMQClient(omcExecPath, locale, new ZMQPortFileProvider(fileProviderSuffix));
        logger.info("OMCAdapter initialized");
    }
}
