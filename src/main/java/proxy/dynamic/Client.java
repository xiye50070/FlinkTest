package proxy.dynamic;

public class Client {
    public static void main(String[] args) {
        // 创建目标对象
        ITeacherDao teacherDao = new TeacherDao();

        // 给目标对象创建代理对象
        ProxyFactory proxyFactory = new ProxyFactory(teacherDao);
        ITeacherDao proxyInstance = (ITeacherDao) proxyFactory.getProxyInstance();

        // 内存中生成了代理对象
        System.out.println("proxyInstance= " + proxyInstance.getClass());

        // 通过代理对象调用目标对象方法
        proxyInstance.teach();
    }
}
