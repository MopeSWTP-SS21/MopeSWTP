package Client;

import org.eclipse.lsp4j.services.LanguageClient;

public interface IModelicaLanguageClient extends LanguageClient {
    public Object checkModel(String modelName);

    public Object compilerVersion();

}
