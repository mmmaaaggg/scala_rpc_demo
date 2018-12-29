package demo.rpc

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

class Master extends Actor{

  override def preStart(): Unit = {
    super.preStart()
    println("preStart invoked")
  }

  override def receive: Receive = {
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
    val master = actorSystem.actorOf(Props(new Master), "Master")
    master ! "hello"
    actorSystem.wait(10)
    actorSystem.terminate()
  }
}