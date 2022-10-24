package proxy.staticProxy;

// 静态代理对象
public class TeacherDaoProxy implements ITeacherDao{

    private final ITeacherDao target; // 目标对象，通过接口来聚合

    // 构造器
    public TeacherDaoProxy(ITeacherDao target) {
        this.target = target;
    }

    @Override
    public void teach() {
        System.out.println("开始代理。。。");
        target.teach();
        System.out.println("提交。。。");
    }
}
