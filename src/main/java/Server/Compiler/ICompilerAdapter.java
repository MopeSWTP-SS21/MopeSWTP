package Server.Compiler;

public interface ICompilerAdapter {
    String loadFile(String path);
    String checkModel(String modelName);
    String getCompilerVersion();
    Boolean connect();
}
