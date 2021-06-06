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
    public String loadFile(String path) {
        Result result = omc.sendExpression("loadFile(\"" + path + "\")");
        return result.toString();
    }

    @Override
    public String checkModel(String modelName) {
        //TODO
        //Optional<String> name = ScriptingHelper.getModelName( Paths.get("/home/swtp/modelica/LotkaVolterra/LV_Manu/LV3Species.mo"));
        //String result2 = omc.checkModel(name.orElse("LV_Manu.LV2Species"));
        String result = omc.checkModel(modelName);
        return "Model " + modelName + " checked\n" + "->" + result;
    }

    @Override
    public String addFolderToModelicaPath(String path){
        Result result = omc.sendExpression("setModelicaPath(getModelicaPath()+\":\"+\"" + path + "\")");
        return result.toString();
    }

    @Override
    public String getModelicaPath(){
        Result result = omc.sendExpression("getModelicaPath()");
        return result.toString();
    }

    @Override
    public String loadModel(String name){
        Result result = omc.sendExpression("loadModel(" + name + ")");
        return result.toString();
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
