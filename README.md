# scala_rpc_demo
学习Scala语言，通过AKKA实现RPC通讯

## 调用方法

### Master 端
```cmd
java -jar scala-rpc-2.1-Master.jar 10.0.3.66 8888
```

### Worker 端
```cmd
java -jar scala-rpc-2.1-Worker.jar 10.0.3.66 8889 10.0.3.66 8888 2000 4
```
