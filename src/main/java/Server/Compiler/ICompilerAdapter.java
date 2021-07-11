package Server.Compiler;

import java.io.IOException;
import java.util.List;
import omc.corba.Result;

public interface ICompilerAdapter {


    /** documentation
     * loads the File located at the given path into the compiler
     * */
    String loadFile(String path);

    void exit() throws IOException;

    String checkModel(String modelName);

    String addFolderToModelicaPath(String path);

    String getModelicaPath();

    Result loadModel(String name);

    Result existClass(String className);

    String getCompilerVersion();

    /**
     * Searches through all Loaded Models
     * @param search the filter applied to search
     * @return A loaded ClassNames containing the searchParameter.
     */
    List<String> searchLoadedClassNames(String search);

    /**
     * Searches for all Libraries that ar available in the current ModelicaPath
     * @return List with library names as string
     */
    List<String> getAvailableLibraries();

    /**
     * Searches for all loaded Classes
     * @param classPackage
     * @return List of loaded Classes on the lowest possible Level
     */
    List<String> getLoadedClassNames(String classPackage);

    String getClassComment(String className);

    Boolean connect();
}
