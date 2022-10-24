package flinkAkka

object Constant {
  val JMAS = "MyJobManagerActorSystem"
  val JMA = "MyJobManagerActor"
  val TMAS = "MyTaskManagerActorSystem"
  val TMA = "MyTaskManagerActor"
}

// TODO_MA 注释： 注册消息 TaskManager -> JobManager
case class RegisterTaskManager(val taskmanagerid: String, val memory: Int, val cpu: Int)
// TODO_MA 注释： 注册完成消息 JobManager -> TaskManager
case class RegisteredTaskManager(val jobmanagerhostname: String)
// TODO_MA 注释： 心跳消息 TaskManager -> JobManager
case class Heartbeat(val taskmanagerid: String)
// TODO_MA 注释： TaskManager 信息类
class TaskManagerInfo(val taskmanagerid: String, val memory: Int, val cpu: Int) {
  // TODO_MA 注释： 上一次心跳时间
  var lastHeartBeatTime: Long = _
  override def toString: String = {
    taskmanagerid + "," + memory + "," + cpu
  }
}
// TODO_MA 注释： 一个发送心跳的信号
case object SendMessage
// TODO_MA 注释： 一个检查信号
case object CheckTimeOut