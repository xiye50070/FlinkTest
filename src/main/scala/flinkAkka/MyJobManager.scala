package flinkAkka

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import scala.collection.mutable

class MyJobManager(var hostname: String, var port: Int) extends Actor {
  // TODO_MA 注释： 用来存储每个注册的NodeManager节点的信息
  private var id2taskManagerInfo = new mutable.HashMap[String, TaskManagerInfo]()
  // TODO_MA 注释： 对所有注册的NodeManager进行去重，其实就是一个HashSet
  private var taskManagerInfos = new mutable.HashSet[TaskManagerInfo]()
  // TODO_MA 注释： actor在最开始的时候，会执行一次
  override def preStart(): Unit = {
    import scala.concurrent.duration._
    import context.dispatcher
    // TODO_MA 注释： 调度一个任务， 每隔五秒钟执行一次
    context.system.scheduler.schedule(0 millis, 5000 millis, self, CheckTimeOut)
  }
  // TODO_MA 注释： 正经服务方法
  override def receive: Receive = {
    // TODO_MA 注释： 接收 注册消息
    case RegisterTaskManager(nodemanagerid, memory, cpu) => {
      val nodeManagerInfo = new TaskManagerInfo(nodemanagerid, memory, cpu)
      println(s"节点 ${nodemanagerid} 上线")
      // TODO_MA 注释： 对注册的NodeManager节点进行存储管理
      id2taskManagerInfo.put(nodemanagerid, nodeManagerInfo)
      taskManagerInfos += nodeManagerInfo
      // TODO_MA 注释： 把信息存到zookeeper
      // TODO_MA 注释： sender() 谁给我发消息，sender方法返回的就是谁
      sender() ! RegisteredTaskManager(hostname + ":" + port)
    }
    // TODO_MA 注释： 接收心跳消息
    case Heartbeat(nodemanagerid) => {
      val currentTime = System.currentTimeMillis()
      val nodeManagerInfo = id2taskManagerInfo(nodemanagerid)
      nodeManagerInfo.lastHeartBeatTime = currentTime
      id2taskManagerInfo(nodemanagerid) = nodeManagerInfo
      taskManagerInfos += nodeManagerInfo
    }
    // TODO_MA 注释： 检查过期失效的 NodeManager
    case CheckTimeOut => {
      val currentTime = System.currentTimeMillis()
      // TODO_MA 注释： 15 秒钟失效
      taskManagerInfos.filter(nm => {
        val heartbeatTimeout = 15000
        val bool = currentTime - nm.lastHeartBeatTime > heartbeatTimeout
        if (bool) {
          println(s"节点 ${nm.taskmanagerid} 下线")
        }
        bool
      }).foreach(deadnm => {
        taskManagerInfos -= deadnm
        id2taskManagerInfo.remove(deadnm.taskmanagerid)
      })
      println("当前注册成功的节点数" + taskManagerInfos.size + "\t分别是：" + taskManagerInfos.map(x => x.toString)
        .mkString(","));
    }
  }
}

object MyJobManager {
  def main(args: Array[String]): Unit = {
    // 地址参数
    val host = "localhost"
    val port = 6789
    val str =
      s"""
         |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname = ${host}
         |akka.remote.netty.tcp.port = ${port}
""".stripMargin
    val conf = ConfigFactory.parseString(str)
    val actorSystem = ActorSystem(Constant.JMAS, conf)
      // 启动了一个actor ： MyResourceManager
      actorSystem.actorOf(Props(new MyJobManager(host, port)), Constant.JMA)
  }
}

