package com.coolrandy.jvm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by randy on 2016/5/11.
 *
 */
public class ReflectionTest {

    private String name;
    private int age;

    public String interests;

    public ReflectionTest(String name, int age, String interests) {
        this.name = name;
        this.age = age;
        this.interests = interests;

        System.out.println("name: " + name + ", age: " + age + ", interests: " + interests);
    }

    public ReflectionTest(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public ReflectionTest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void hello(){
        System.out.println("Hello Reflect");
    }

    public void hello(String str, int temp){
        System.out.println("hello " + str + ", give me " + temp + " flowers.");
    }

    private void testPrivate(){
        System.out.println("test method for private");
    }

    protected void testProtected(){
        System.out.println("test method for protected");
    }


    public static void main(String[] args){

        ReflectionTest test = new ReflectionTest();

        Class c1 = ReflectionTest.class;//任何一个类都有一个隐含的静态成员变量class
        Class c2 = test.getClass();//通过一个类的对象的getClass方法来获取Class的对象
        try {
            Class c3 = Class.forName("com.coolrandy.jvm.ReflectionTest");//采用Class类的forName方法获取，即采用一个类的全量限定名
            System.out.println("c3: " + c3.getName());
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        //上面的c1、c2、c3三个变量都是一样的，都称之为ReflectionTest的类类型（class type），即类的类型，用于描述一个类是什么，都包含那些东西

        //test
        System.out.println("c1: " + c1.getName());
        System.out.println("c2: " + c2.getName());


        //通过该Class可以获取成员方法Method、成员变量Field、构造方法Constructor
        //这里以c1为例一次说明：
        try {
            Object object = c1.newInstance();//初始化一个Class类的实例
            Method method = c1.getMethod("hello", String.class, int.class);//获取hello方法
            method.invoke(object, "jack", 10);//触发c1这个Class类对象实例的hello方法，并分别传入两个参数
            //从上面的运行结果可以知道，这个效果和直接使用new ReflectionTest().hello("jack", 10);是一样的

            //继续获取所有方法的数组
            Method[] methods = c1.getDeclaredMethods();//得到该类所有的方法(无论是public、private或protected)，但是不包括父类的（默认是继承于Object的）
            //或者
            Method[] methods1 = c1.getMethods();//得到该类所有的public方法，并且包括父类的
            //test
            for (Method m: methods){
                System.out.println("declared method: " + m);
            }

            for (Method m: methods1){
                System.out.println("method: " + m);
            }

            //获取成员变量信息，类似获取Method，同样有两类方法,这里以c2为例说明
            //getDeclaredField： 获取该类自身声明的所有变量，不包括其父类的变量
            //getField: 获取来自该类自身声明的所有public变量，并且包括其父类变量（同样也是public的）
            Field field = c2.getDeclaredField("name");//这里name变量是private的，不能用getField来获取
            Object object1 = c2.newInstance();
            field.setAccessible(true);//设置是否允许访问，由于name变量是私有的，所以需要手动设置可访问，对于interests变量由于是公有的，可不设置
            Object name = field.get(object1);
            System.out.println("name: " + name);
            //获取所有的field数组
            Field[] fields = c2.getDeclaredFields();
            Field[] fields1 = c2.getFields();
            //test
            for (Field f: fields){
                System.out.println("declared field: " + f);
            }
            for (Field f: fields1){
                System.out.println("field: " + f);
            }

            //获取构造方法,依然类似
            //public Constructor<T> getDeclaredConstructor(Class<?>... parameterTypes) //  获得该类所有的构造器，不包括其父类的构造器
            //public Constructor<T> getConstructor(Class<?>... parameterTypes) // 获得该类所以public构造器，包括父类
            Constructor constructor = c1.getConstructor(String.class, int.class, String.class);
            constructor.setAccessible(true);
            constructor.newInstance("marry", 24, "play football");
            //获取所有构造方法数组,测试省略
            Constructor[] constructors = c1.getConstructors();
            Constructor[] constructors1 = c1.getDeclaredConstructors();

            //泛型，类型擦除
            List list = new ArrayList<>();
            List<String> list1 = new ArrayList<>();//含有泛型
            //正常添加元素
            list1.add("apple");//运行正常
//            list1.add(29);//编译期检查类型错误
            System.out.println("list size: " + list1.size());//size为1
            //通过反射添加元素
            Class clz1 = list.getClass();
            Class clz2 = list1.getClass();
            System.out.println(clz1==clz2);//输出为true，表明类类型完全一样
            //通过方法的反射添加元素,这样就可以绕过编译检查
            Method m = clz2.getMethod("add", Object.class);//这里list的add方法是继承于Object类的，即当前类的超类，需要调用getMethod方法
            m.invoke(list1, 10);
            System.out.println("after invoke list size: " + list1.size());//size为2，说明10添加进去了，并没有报错，成功绕过编译器的类型检查
            //上述也说明了泛型在运行期存在类型擦除的特性，也就是说在编译器泛型会限制集合元素为指定类型，比如上面的String类型，但是到了运行期，类型擦除掉了
            //也就是说List<String>变成了List，并没有类型的限制了，这样就可以添加任意类型元素了
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
