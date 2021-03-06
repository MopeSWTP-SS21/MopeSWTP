package Client;

import org.eclipse.lsp4j.services.LanguageClient;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface IModelicaLanguageClient extends LanguageClient {

    public Object checkModel(String modelName);

    public Object compilerVersion();

}
