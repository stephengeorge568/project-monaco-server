# Project Monaco server

NOTE: a far more in-depth explanation of this project and its inner workings will be written soon. It will be posted here as well as the public website the project will be hosted on.

Welcome to the Java Spring backend project for my live, collaborative code editor project. Similar in concept to Google Doc's document sharing feature where multiple users can connect to a document session and all edit the same document simultaneously. To manage the conflicts caused by latency in this application, an iteration of [operational transformation](https://en.wikipedia.org/wiki/Operational_transformation) was developed. The wikipedia article linked will be helpful in understanding the engineerings issues related to a true live, collaborative text editor. During this project I faced two main, broad scope problems:
- How to manage document edit change requests
- How to implement algorithm on my document's data model that 

### How to manage document edit change requests
- When a user creates a change on the client document, a change request is generated. This request is added to an outbound queue of requests waiting to be shipped to the server for transformation. During my preparation, it was discovered that only sending one request at a time greatly decreases the complexity of the operational transformation process. For this reason, the outbound queue of change requests only sends one request, then waits for a response before sending more.
- Use a revision id on each client's document state, as well as the document model managed by the server. This is used to determine which change requests are relevant when transforming a given change request.

### How to implement algorithm on my document's data model that
The project's front end utilizes Microsoft's open source Monaco Editor. The very same editor found in VS Code! Here are relevant bits for the data model:
- text: the actual text of the change request
- startColumn: the starting column number of the change request range
- endColumn: the ending column number of the change request range
- startLineNumber: the starting line number of the change request range
- endLineNumber: the ending line number of the change request range
- revisionId: this property I add myself, the revision ID of the document at the time the document change request was created

## Links
[Front end repo](https://github.com/stephengeorge568/OT-editor)

## Status
90% done
Todo:
- general error handling, i.e if error occurs in transmission of change request, notify client
- only one incoming request is transformed at a time. Current method of forcing other threads to wait whilst in a queue is by using a lock and Thread.Sleep. Implement some form of BlockingQueue as better replacement.
