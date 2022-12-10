# OT-Editor
This is the backend API and server for a web based collaborative code editor. Users can create documents and edit those documents with anyone that can access a browser. The editor supports features like cloud save, downloading the file, syntax highlighting, some simple intellisense, and more! 
## How to use

1. Download code. 
2. Download Docker. 
3. In dockerfile, change value of --spring.profiles.active to dev 
4. Perform command ‘docker build –t ot-server .’ on root folder 
5. Perform command ‘docker run -p 8080:8080 ot-server' 

## Release Notes
September 2022: Only one document at a time can be supported. Some minor bugs with operational transformation process. No way to save document model to filesystem or database.

October 2022: Generalization nearly complete. Multiple documents can be created, and mulitiple documents can be editted at once. Documents can be opened and editted. Database access is implemented. No way to save model to filesystem.

November 2022: Generalization complete. Filesystem cloud saves enabled. Minor bug fixes.
