package Server.Compiler;

import java.io.IOException;
import java.util.List;
import omc.corba.Result;

public interface ICompilerAdapter {


    /**
     * <p>This method loads a model or many models in a file by adding its path to the Modelica-library.</p>
     * @see Client.MopeLSPClient
     */
    String loadFile(String path);

    /**
     * Terminates the omc process
     * @see Client.MopeLSPClient
     */
    void exit() throws IOException;

    /**
     * This model performs basic checks on a model and returns the number of variables and equations in it.
     * @see Client.MopeLSPClient
     */
    String checkModel(String modelName);

    /**
     * <p>This method adds a folder path (where the modelica-file is located) to the Modelica library.</p>
     * @param path that has to be an absolute path to a folder containing Modelica files
     * @return a string notifying whether is was successful or not
     */
    String addFolderToModelicaPath(String path);

    /**
     * <p>This method prints the Modelica library to the console.</p>
     * @return an output containing the result string of Modelica library and an optional error-message.
     */
    String getModelicaPath();

    /**
     * <p>Loads a model from the Modelica library</p>
     * @param name has to be a modelica model file
     * @return an output containing the result whether it was successful or not and an optional error-message where the error occured.
     */
    Result loadModel(String name);

    /**
     * <p>check if the class given by the classname has been loaded</p>
     * @param className one is looking is for
     * @return an output containing the result string and an optional error-message.
     */
    Result existClass(String className);

    /**
     * <p>Provides an output with current Modelica Compiler Version</p>
     * @return the current version of OMC
     */
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

    /**
     * <p>This method returns a HTML-formatted Documentation of a given Class.</p>
     * @param className has to be a valid classname
     * @return the html-formatted documentation by given classname
     */
    String getDocumentation(String className);

    /**
     * <p>This method allows to send expressions without starting the OMShell</p>
     * @param command is an OMShell command to be executed
     * @return an output containing the result string and an optional error-message.
     */
    Result sendExpression(String command);

    Boolean connect();
}
