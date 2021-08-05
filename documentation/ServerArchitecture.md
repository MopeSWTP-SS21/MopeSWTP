# Server Architecture
- [Server Architecture](#server-architecture)
  - [ICompilerAdapter](#icompileradapter)
    - [OMCAdapter](#omcadapter)
  - [MopeServerLauncher](#mopeserverlauncher)
  - [MopeLSPServer](#mopelspserver)
  - [MopeWorkspaceService](#mopeworkspaceservice)
  - [MopeDocumentService](#mopedocumentservice)
  - [MopeModelicaService](#mopemodelicaservice)
  - [CompletionProvider](#completionprovider)
  - [DiagnosticHandler](#diagnostichandler)
    - [ModelicaDiagnostic](#modelicadiagnostic)
  - [_UML_](#uml)

## ICompilerAdapter
This Interface acts as a wrapper for a Modelica Compiler.
It contains Methods like:
- connect  (launching a compiler instance and connecting to it)
- exit  (disconnecting from the compiler and exiting the process)
- checkModel
- getCompilerPath
- sendExpression
- and quite a few more
### OMCAdapter

This is the implementation of the ICompilerAdapter-Interface responsible for managing requests to the OpenModelicaCompiler(OMC). It uses the omc-java-api to interact with the OMC.

It requires a valid path to the OMC-loaction. This path gets injected via the constructor.

## MopeServerLauncher
The MopeServerLauncher is responsible for starting the MopeLSPServer.
To do so it 
- tries to read a configuration-file
- opens a serverSocket
- launches an instance of MopeLSPServer
- adds clients that connect to the Socket
  
Furthermore this class is responsible to free all the used resources after the Server is shut down. 
## MopeLSPServer
The MopeLSPServer is an implementation of the LSP4J::LanguageServer. It contains Methods like:
- initialize (initializes the Server, starts compilerProcess and exchanges capabilities with the Client)
- shutdown (Shuts down the Server Instance and exits the compilerProcess)
- get<_ServiceName_>Service (is used to call methods from the underlying service(JSON-RPC-Segment))

## MopeWorkspaceService
MopeWorkspaceService is responsible for all workspace-related operations. For more information consider taking a look at the official LSP-Specification.

To make the integration of Modelica-related commands on the client side as easy as possible the 'executeCommand' method is used to execute methods located in the MopeModelicaService. 

## MopeDocumentService
The responsibilities of this Service are everything related to TextDocuments.
For example:
- Opening a document
- Changing a document

Also this time refer to the official LSP-Specification for more information.

For now the MopeDocumentService only supports the `complete` operation.
## MopeModelicaService
The MopeModelicaService supplies Modelica specific functionality like semantic and syntactic analysis of models.
The Service uses an implementation to of [ICompilerAdapter](#icompileradapter) to interact with a Modelica compiler.
During some operations it uses the [ModelicaDiagnosticClass](#modelicadiagnostic) to generate Diagnostics and forward them to the [DiagnosticHandler](#diagnostichandler).

## CompletionProvider
The CompletionProvider is called from the MopeDocumentService to generate a List of LSP4J::CompletionItems which is forwarded to the Client calling the Complete-Request.
To do so the CompletionProvider opens the file where the completion is requested and searches for a keyword on the given position(line,character).
Depending on the type of the keyword, the CompletionProvider uses the ICompilerAdapter to search for the names of loaded Models.
## DiagnosticHandler
The DiagnosticHandler maintains multiple lists of Diagnostics organized in a hash map. One list for each file that contains Diagnostics.
On every change in any of the Diagnostics the Handler triggers the `publishDiagnosticsToAllClients` method provided by MopeLSPServer.   

### ModelicaDiagnostic
ModelicaDiagnostics extend the LSP4J::Diagnostic. It adds a String `URI` which indicates the file the Diagnostic is located in and is used by the DiagnosticHandler to add the Diagnostic to the correct list.
Furthermore it provides static Methods to create Diagnostics based on the result returned from ICompilerAdapter Methods like `checkModel` or `loadModel`.

## _UML_
![MopeLSPServerUML](uml/png/Server.png)

