package demo.rpc

import akka.actor.{Actor, ActorSelection, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

class Worker extends Actor{

  var master: ActorSelection = _
  /**
    * 建立连接
    */
  override def preStart(): Unit = {
    super.preStart()
    println("client start")
    // akka.tcp://MasterSystem@10.0.3.66:8888
    master = context.actorSelection("akka.tcp://MasterSystem@10.0.3.66:8888/user/Master")
    master ! "connect"
  }

  override def receive: Receive = {
    case "connect" => {
      println("client connected")
    }
    case "reply" => {
      println("a reply from master")
    }
  }
}

/**
  * Worker send connect to Master
  * Master send back reply to Workder
  *
  */
object Worker{
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
//    val master = actorSystem.actorOf(Props(new Master), "Master")
//    master ! "hello"
//    actorSystem.wait(10000)
//    actorSystem.terminate()
    actorSystem.actorOf(Props[Worker], "Worker")
    actorSystem.wait(10)
    actorSystem.terminate()
  }
}
