package akka

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object SenderActorObject extends Actor {

  // 当Actor初次被调用化时
  override def preStart(): Unit = {
    println("执行SenderActorObject PreStart()方法")
  }

  override def receive: Receive = {

    case "start" =>
      // 跨进程时可以通过  akka.tcp://actorsystem_name@bigdata02:9527/user/actor_name 来定位actor
      val receiveActor = this.context.actorSelection("/user/receiverActor")
      // 向第二个actor发送消息
      receiveActor ! SubmitTaskMessage("请完成#001任务!")
    case SuccessSubmitTaskMessage(msg) =>
      println(s"接收到来自${sender.path}的消息: $msg")
  }

}

object ReceiverActor extends Actor {

  override def preStart(): Unit = {
    println("执行ReceiverActor()方法")
  }
  // 执行receive方法前会先执行preStart方法
  override def receive: Receive = {
    case SubmitTaskMessage(msg) =>
      println(s"接收到来自${sender.path}的消息: $msg")
      // 又向第一个sender发送消息
      sender ! SuccessSubmitTaskMessage("完成提交")
    case _ => println("未匹配的消息类型")
  }
}

object SimpleAkkaDemo {
  def main(args: Array[String]): Unit = {

    // 创建一个actor系统
    val actorSystem = ActorSystem("SimpleAkkaDemo", ConfigFactory.load())

    //创建一个actor
    val senderActor: ActorRef = actorSystem.actorOf(Props(SenderActorObject), "senderActor")

    //创建一个actor
    val receiverActor: ActorRef = actorSystem.actorOf(Props(ReceiverActor), "receiverActor")

    // 使用actor的引用向actor发送消息
    senderActor ! "start"
  }
}

//消息封装样例类
case class SubmitTaskMessage(message: String)

case class SuccessSubmitTaskMessage(message: String)

