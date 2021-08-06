package Server;

import Client.MopeLSPClient;
import Server.Compiler.ICompilerAdapter;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MopeDocumentService implements TextDocumentService {
    private ICompilerAdapter compiler;
    private static final Logger logger = LoggerFactory.getLogger(MopeDocumentService.class);

    @Override
    /**
     * <p>Provides a list of possible code-completions</p>
     * @return
     */
    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams completionParams) {
        logger.info("DocumentService->completion");
        return CompletableFuture.supplyAsync(() -> {
            List<CompletionItem> completionItems = new ArrayList<>();
            try {
                completionItems = CompletionProvider.complete(completionParams, compiler);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Either.forLeft(completionItems);
        });
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        logger.info("TestDocumentService->didOpen triggerd...");
        logger.info(params.toString());
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {

    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {

    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {

    }

    @Override
    public CompletableFuture<Hover> hover(HoverParams params) {
        logger.info("TestDocumentService->hover triggerd...");
        logger.info(params.toString());
        Hover h = new Hover();
        h.setContents(new MarkupContent("h1", "hallo"));
        return CompletableFuture.supplyAsync(() -> { return h; });
    }

    public MopeDocumentService(ICompilerAdapter comp){
        super();
        compiler = comp;
    }
}