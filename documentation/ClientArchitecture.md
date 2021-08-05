# Client Architecture
- [Client Architecture](#client-architecture)
  - [ConsoleClientLauncher](#consoleclientlauncher)
  - [ConsoleMenu](#consolemenu)
  - [MopeLSPClient](#mopelspclient)
  - [_UML_](#uml)

## ConsoleClientLauncher
The ConsoleClientLauncher is responsible for starting the MopeLSPClient.
It connects a socket to the given port and host address, launches a instance of MopeLSPClient and connects it In- and Out-putStreams to the socket.
Afterwards it presents the User a ConsoleMenu and keeps running until the User exited that Menu.
## ConsoleMenu
The ConsoleMenu is responsible for:
- Guiding the User so he knows what to input
- Reading the UserInput
- Calling the requested _WrapperMethod_ in the MopeLSPClient
- Printing the Answers from the Server
## MopeLSPClient
MopeLSPClient is an implementation of the LSP4J::LanguageClient. It implements RPC's like:
- showMessage
- publishDiagnostics

For more Information about client side RPC's you can have a look into the official LSP-Specification.

Additionally the MopeLSPClient contains _WrapperMethods_ to call RPC's on  the server side.


## _UML_
![MopeLSPClientUML](uml/png/Client.png)