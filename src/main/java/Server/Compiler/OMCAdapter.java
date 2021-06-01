package Server.Compiler;


import omc.ZeroMQClient;
import omc.corba.OMCInterface;

import omc.corba.Result;
import omc.corba.ScriptingHelper;
import omc.ior.ZMQPortFileProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import version.Version;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

public class OMCAdapter implements ICompilerAdapter{

    private static final Logger logger = LoggerFactory.getLogger(OMCAdapter.class);
    private final OMCInterface omc;


    @Override
    public String checkModel(String modelName) {
        //TODO
        Result result = omc.sendExpression("loadFile(\"/home/swtp/modelica/LotkaVolterra/LV_Manu/package.mo\")");
        try{
            Optional<String> name = ScriptingHelper.getModelName( Paths.get("/home/swtp/modelica/LotkaVolterra/LV_Manu/LV3Species.mo"));
            String result2 = omc.checkModel(name.orElse("LV_Manu.LV2Species"));
            return "Model " + modelName + " checked\n" + "->" + result2 +"\n->" + result.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Something went wrong";
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
