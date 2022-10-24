package MWAkka

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
object MasterTest {
  def main(args: Array[String]): Unit = {

    val actorSystem = ActorSystem("actorSystem", ConfigFactory.load())
    //加载actor
    val masterActor = actorSystem.actorOf(Props(Master), "masterActor")
  }
}


