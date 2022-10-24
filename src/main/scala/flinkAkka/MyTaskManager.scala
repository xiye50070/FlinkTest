package flinkAkka

import akka.actor.{Actor, ActorSelection, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

class MyTaskManager(val tmhostname: String, val jobmanagerhostname: String, val jobmanagerport: Int, val memory: Int,
                    val cpu: Int) extends Actor {
  var taskManagerId: String = tmhostname
  var rmRef: ActorSelection = _
  // TODO_MA 注释： 会提前执行一次
  // TODO_MA 注释： 当前NM启动好了之后，就应该给 RM 发送一个注册消息
  // TODO_MA 注释： 发给谁，就需要获取这个谁的一个ref实例
  override def preStart(): Unit = {
    // TODO_MA 注释： 获取消息发送对象的一个ref实例
    // 远程path akka.tcp://（ActorSystem的名称）@（远程地址的IP）： （远程地址的端口）/user/（Actor的名称）
    rmRef = context.actorSelection(s"akka.tcp://${
      Constant.JMAS
    }@${jobmanagerhostname}:${jobmanagerport}/user/${Constant.JMA}")
    // TODO_MA 注释： 发送消息
    println(taskManagerId + " 正在注册")
    rmRef ! RegisterTaskManager(taskManagerId, memory, cpu)
  }
  // TODO_MA 注释： 正常服务方法
  override def receive: Receive = {
    // TODO_MA 注释： 接收到注册成功的消息
    case RegisteredTaskManager(masterURL) => {
      println(masterURL);
      // TODO_MA 注释： initialDelay: FiniteDuration, 多久以后开始执行
      // TODO_MA 注释： interval: FiniteDuration, 每隔多长时间执行一次
      // TODO_MA 注释： receiver: ActorRef, 给谁发送这个消息
      // TODO_MA 注释： message: Any 发送的消息是啥
      import scala.concurrent.duration._
      import context.dispatcher
      context.system.scheduler.schedule(0 millis, 4000 millis, self, SendMessage)
    }
    // TODO_MA 注释： 发送心跳
    case SendMessage => {
      // TODO_MA 注释： 向主节点发送心跳信息
      rmRef ! Heartbeat(taskManagerId)
      println(Thread.currentThread().getId)
    }
  }
}

object MyTaskManager {
  def main(args: Array[String]): Unit = {
    // TODO_MA 注释： 远程主机名称
    val HOSTNAME = args(0)
    // TODO_MA 注释： JM 的 hostname 和 port
    val JM_HOSTNAME = args(1)
    val JM_PORT = args(2).toInt
    // TODO_MA 注释： 抽象的内存资源 和 CPU 个数
    val TASKMANAGER_MEMORY = args(3).toInt
    val TASKMANAGER_CORE = args(4).toInt
    // TODO_MA 注释： 当前 TM 的 hostname 和 port
    var TASKMANAGER_PORT = args(5).toInt
    var TMHOSTNAME = args(6)
    // TODO_MA 注释： 指定主机名称和端口号相关的配置
    val str =
      s"""
         |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname = ${HOSTNAME}
         |akka.remote.netty.tcp.port = ${TASKMANAGER_PORT}
""".stripMargin
    val conf = ConfigFactory.parseString(str)
    // TODO_MA 注释： 启动一个 ActorSystem
    val actorSystem = ActorSystem(Constant.JMAS, conf)
    // TODO_MA 注释： 启动一个 Actor
    actorSystem.actorOf(Props(new MyTaskManager(TMHOSTNAME, JM_HOSTNAME, JM_PORT, TASKMANAGER_MEMORY,
      TASKMANAGER_CORE)), Constant.TMA)
  }
}