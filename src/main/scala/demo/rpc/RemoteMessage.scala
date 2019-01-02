package demo.rpc

trait RemoteMessage extends Serializable

/**
  * Worker -> Master
  * @param id
  * @param memory
  * @param cores
  */
case class RegisterWorker(id:String, memory:Int, cores:Int) extends RemoteMessage

// Master -> Worker
case class RegisteredWorker(masterUrl: String) extends RemoteMessage

// worker -> self
case object SendHeardBeat

case class Heartbeat(id:String)