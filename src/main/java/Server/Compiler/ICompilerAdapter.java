package Server.Compiler;

public interface ICompilerAdapter {
    String loadFile(String path);
    String checkModel(String modelName);

    String addFolderToModelicaPath(String path);

    String getModelicaPath();

    String loadModel(String name);

    String getCompilerVersion();
    Boolean connect();
}
