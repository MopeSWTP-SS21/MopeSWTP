package Client;

import org.eclipse.lsp4j.services.LanguageClient;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * <p>An interface that provides LanguageClient methods</p>
 *
 */
public interface IModelicaLanguageClient extends LanguageClient {

    public Object checkModel(String modelName);

    public Object compilerVersion();

}
