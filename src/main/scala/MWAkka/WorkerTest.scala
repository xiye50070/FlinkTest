package MWAkka

import com.typesafe.config.ConfigFactory
import akka.actor.{Actor, ActorSystem, Props}

object WorkerTest {
  def main(args: Array[String]): Unit = {
    // 1. 创建一个ActorSystem
    val actorSystem = ActorSystem("actorSystem", ConfigFactory.load())

    // 2. 加载Actor
    val workerActor = actorSystem.actorOf(Props(Worker), "workerActor")

    // 3. 发送消息给Actor
    workerActor ! "setup"
  }
}


