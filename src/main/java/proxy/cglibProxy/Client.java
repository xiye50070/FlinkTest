package proxy.cglibProxy;

public class Client {
    public static void main(String[] args) {
        // 创建目标对象
        TeacherDao teacherDao = new TeacherDao();

        // 传递给代理对象
        ProxyFactory proxyFactory = new ProxyFactory(teacherDao);
        TeacherDao proxyInstance = (TeacherDao) proxyFactory.getProxyInstance();

        // 执行代理对象的方法，触发intercept方法，从而实现对目标对象的调用
        proxyInstance.teach();

    }
}
