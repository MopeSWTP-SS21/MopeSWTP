package Server.Compiler;


import Server.DiagnosticHandler;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class OMCAdapter implements ICompilerAdapter{

    private static final Logger logger = LoggerFactory.getLogger(OMCAdapter.class);
    private final OMCInterface omc;
    private DiagnosticHandler diagnosticHandler;


    @Override
    public String loadFile(String path) {
        Result result = omc.sendExpression("loadFile(\"" + path + "\")");
        return result.toString();
    }

    @Override
    public String checkModel(String modelName) {
        Result result = omc.sendExpression("loadModel(" + modelName + ")");
        return "Model " + modelName + " checked\n" + "->" + result.toString();
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
        diagnosticHandler.addDiagnostics(ModelicaDiagnostic.CreateDiagnostics(result));
        return result.toString();
    }


    @Override
    public String getCompilerVersion() {
        logger.info("Requesting OMC Version");
        Version v = omc.getVersion();
        return v.toString();
    }

    @Override
    public List<String> searchLoadedClassNames(String search){
        logger.info("Searching loaded Classes for " + search);
        Result result = omc.sendExpression("searchClassNames(\"" + search + "\")"); //Todo \" ?
        String[] classes = result.result.substring(1, result.result.length() -1 ).split(",");
        if(classes.length <= 0 || classes[0].length() <= 0  ) return new ArrayList<String>();
        return Arrays.asList(classes);
    }
    @Override
    public List<String> getAvailableLibraries(){
        logger.info("Find all available Libraries in ModelicaPath");
        Result result = omc.sendExpression("getAvailableLibraries()");
        String[] libs = result.result.split(",");
        return Arrays.asList(libs);
    }
    @Override
    public List<String> getLoadedClassNames(String classPackage){
        String info = "Searching for classNames";

        String expression;
        if(classPackage == null || classPackage.trim().length() == 0){
            expression = "getClassNames()";
        } else {
            expression = "getClassNames(" + classPackage + ")";
            info += " in " + classPackage;
        }
        logger.info(info);
        Result result = omc.sendExpression(expression);
        String[] libs = result.result.substring(1, result.result.length() -1 ).split(",");
        if(libs.length <= 0 || libs[0].length() <= 0  ) return new ArrayList<String>();
        return Arrays.asList(libs);
    }

    @Override
    public String getClassComment(String className){
        Result result = omc.sendExpression("getClassComment(" + className + ")");
        return result.result;
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

    public OMCAdapter(String omcExecPath, String locale, String fileProviderSuffix, DiagnosticHandler diagnosticHandler){
        omc = new ZeroMQClient(omcExecPath, locale, new ZMQPortFileProvider(fileProviderSuffix));
         this.diagnosticHandler = diagnosticHandler;
        logger.info("OMCAdapter initialized");
    }
}
