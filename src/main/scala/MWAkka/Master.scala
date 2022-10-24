package MWAkka

import akka.actor.Actor
object Master extends Actor {
  override def receive: Receive = {
    case "connect" => {
      println("MasterActor: 接收到的connect的消息")
      //获取发送者Actor的引用
      sender !"success"
    }
  }
}


