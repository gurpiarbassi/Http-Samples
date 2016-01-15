#demo

##setup
You will need Java 1.8

##To Run
`mvn jetty:run`

Sample GET request: curl http://localhost:8080/test?a=1&b=2&c=4
Sample POST request curl -X POST http://localhost:8080/test --data "param1=param1val&param2=param2val"

##Backlog
1.Test for multithreading file writing.
2.Refactor servlet further to move out file writing into seperate collaborator.
3.Test behaviour when no request parameters supplied. Should checksum still be written?
