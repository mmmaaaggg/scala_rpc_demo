package demo.rpc

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.collection.mutable

class Master(val host:String, val port:Int) extends Actor{

  // workerId -> WorkerInfo
  val idToWorker = new mutable.HashMap[String, WorkerInfo]()
  // WorkerInfo
  val workers = new mutable.HashSet[WorkerInfo]()

  override def preStart(): Unit = {
    super.preStart()
    println("preStart invoked")
  }

  override def receive: Receive = {
    case RegisterWorker(id, memory, cores) => {
      // 判断是否已经注册
      if (!idToWorker.contains(id)){
        // 添加注册
        val workerInfo = new WorkerInfo(id, memory, cores)
        idToWorker(id) = workerInfo
        workers += workerInfo
      }
      sender ! RegisteredWorker(s"akka.tcp://MasterSystem@$host:$port/user/Master")
    }
    case "connect" => {
      println("a client connected")
      sender ! "reply"
    }
    case "hello" => {
      println("a client send hello")
    }
  }
}

object Master{


  def main(args: Array[String]): Unit = {

    val host = args(0)
    val port = args(1).toInt
    val configStr: String =
      s"""
         |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname = "$host"
         |akka.remote.netty.tcp.port = "$port"
      """.stripMargin

    val config = ConfigFactory.parseString(configStr)
    // ActorSystem 老大， 辅助创建和监控 下面的 actor，他是单例的
    val actorSystem = ActorSystem("MasterSystem", config)
    // 创建 actor
    val master = actorSystem.actorOf(Props(new Master(host, port)), "Master")
    master ! "hello"
    actorSystem.wait(10)
    actorSystem.terminate()
  }
}