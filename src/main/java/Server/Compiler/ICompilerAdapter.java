package Server.Compiler;

public interface ICompilerAdapter {
    public String checkModel(String modelName);
    public String getCompilerVersion();
    public Boolean connect();
}
