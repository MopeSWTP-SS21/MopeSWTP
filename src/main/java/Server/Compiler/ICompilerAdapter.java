package Server.Compiler;

import java.util.List;

public interface ICompilerAdapter {


    /** documentation
     * loads the File located at the given path into the compiler
     * */
    String loadFile(String path);

    String checkModel(String modelName);

    String addFolderToModelicaPath(String path);

    String getModelicaPath();

    String loadModel(String name);

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
