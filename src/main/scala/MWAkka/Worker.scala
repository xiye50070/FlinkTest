package MWAkka

import akka.actor.Actor
object Worker extends Actor {override def receive: Receive = {
  case "setup" => {
    println("WorkerActor：接收到消息setup")
    // 发送消息给Master
    // 1. 获取到MasterActor的引用
    // Master的引用路径：akka.tcp://actorSystem@127.0.0.1:8888/user/masterActor
    val masterActor = context.actorSelection("akka.tcp://actorSystem@127.0.0.1:8888/user/masterActor")

    // 2. 再发送消息给MasterActor
    masterActor ! "connect"
  }
  case "success" => {
    println("WorkerActor：收到success消息")
  }
}
}


