package Server;

import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.jsonrpc.services.JsonSegment;

import java.util.concurrent.CompletableFuture;

@JsonSegment("modelica")
public interface ModelicaService {
    @JsonRequest
    /**
     * This method performs basic checks on a model and returns the number of variables and equations in it.
     * @param modelName has to be a name of a fully qualified Modelica class that has been loaded before
     * @return a string formatted output with a number of variables and equations in the model-file
     */
    default CompletableFuture<String> checkModel(String modelName){
        throw new UnsupportedOperationException();
    }
    @JsonRequest
    /**
     * <p>Loads a model from the Modelica library</p>
     * @param name is a Modelica model-name
     * @return waits if necessary for a future to complete, and then returns its result in a string format
     */
    default CompletableFuture<String> loadModel(String modelName){
        throw new UnsupportedOperationException();
    }

    @JsonRequest
    /**
     * <p>This method allows sending arbitrary OM-API commands to the OMC</p>
     * @param command is an OMShell command to be executed
     * @return an output formatted as a string
     */
    CompletableFuture<String> sendExpression(String command);

    @JsonRequest
    /**
     * <p>This method loads a model by adding its path to the Modelica-library.</p>
     * @param path is the absolute path of the Modelica-file
     * @return waits if necessary for a future to complete, and then returns its result in a string format
     */
    default CompletableFuture<String> loadFile(String path){
        throw new UnsupportedOperationException();
    }
    @JsonRequest
    /**
     * not implemented
     */
    default CompletableFuture<String> initializeModel(String modelName){
        throw new UnsupportedOperationException();
    }
    @JsonRequest
    /**
     * <p>This method returns a HTML-formatted Documentation of a given Class.</p>
     * @param className has to be a valid classname
     * @return the html-formatted documentation by given classname
     */
    CompletableFuture<String> getDocumentation(String className);

    @JsonRequest
    /**
     * <p>This method prints the Modelica library to the console.</p>
     * @return waits if necessary for a future to complete, and then returns its result in a string format
     */
    default CompletableFuture<String> getModelicaPath(){
        throw new UnsupportedOperationException();
    }
    @JsonRequest
    /**
     * <p>This method adds a folder path (where the modelica-file is located) to the Modelica library.</p>
     * @param path that has to be an absolute path to the modelica file
     * @return waits if necessary for a future to complete, and then returns its result in a string format
     */
    default CompletableFuture<String> addModelicaPath(String path){
        throw new UnsupportedOperationException();
    }
    @JsonRequest
    /**
     * <p>Provides an output with current Modelica Compiler Version</p>
     * @return waits if necessary for a future to complete, and then returns its result in a string format
     */
    default CompletableFuture<String> getCompilerVersion(){
        throw new UnsupportedOperationException();
    }
}
