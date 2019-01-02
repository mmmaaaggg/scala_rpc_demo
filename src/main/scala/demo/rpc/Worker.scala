package demo.rpc

import java.util.UUID

import akka.actor.{Actor, ActorSelection, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class Worker(val masterHost:String, val masterPort:Int, val memory:Int, val cores:Int) extends Actor{

  var master: ActorSelection = _
  val workerId = UUID.randomUUID().toString
  val CHECK_INTERVAL = 10000

  /**
    * 建立连接
    */
  override def preStart(): Unit = {
    super.preStart()
    println("client start")
    // akka.tcp://MasterSystem@10.0.3.66:8888
    master = context.actorSelection(s"akka.tcp://MasterSystem@$masterHost:$masterPort/user/Master")
    master ! RegisterWorker(workerId, memory, cores)
  }

  override def receive: Receive = {
    case RegisteredWorker(masterUrl)=>{
      println("masterUrl:" + masterUrl)
      // 启动定时器发送心跳
      context.system.scheduler.schedule(0.millis, CHECK_INTERVAL.millis, self, SendHeardBeat)
    }
    case  SendHeardBeat => {
      println("receive SendHeardBeat")
      master ! Heartbeat(workerId)
    }
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
    val masterHost = args(2)
    val masterPort = args(3).toInt
    val memory = args(4).toInt
    val cores = args(4).toInt
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
    actorSystem.actorOf(Props(new Worker(masterHost, masterPort, memory, cores)), "Worker")
    actorSystem.wait(10)
    actorSystem.terminate()
  }
}
