public class SyncThreadJava {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		final Bank bank=new Bank();  

        Thread tadd=new Thread(new Runnable() {  

            @Override  
            public void run() {  
                // TODO Auto-generated method stub  
            	int i=0;
                while(i<1000){  
                    try {  
                        Thread.sleep(100);  
                    } catch (InterruptedException e) {  
                        // TODO Auto-generated catch block  
                        e.printStackTrace();  
                    }  
                    bank.addMoney(100);  
//                    bank.lookMoney();  
//                    System.out.println("\n");  
                    i++;
                } 
                bank.lookMoney();
            }  
        });  
        
        Thread tadd1=new Thread(new Runnable() {  

            @Override  
            public void run() {  
                // TODO Auto-generated method stub  
            	int i=0;
                while(i<1000){  
                    try {  
                        Thread.sleep(100);  
                    } catch (InterruptedException e) {  
                        // TODO Auto-generated catch block  
                        e.printStackTrace();  
                    }  
                    bank.addMoney(100);  
//                    bank.lookMoney();  
//                    System.out.println("\n");  
                    i++;
                }  
                bank.lookMoney();
            }  
        });  

        Thread tsub = new Thread(new Runnable() {  

            @Override  
            public void run() {  
                // TODO Auto-generated method stub  
            	int i=0;
                while(i<1000){  
                    bank.subMoney(100);  
//                    bank.lookMoney();  
//                    System.out.println("\n");  
                    try {  
                        Thread.sleep(100);  
                    } catch (InterruptedException e) {  
                        // TODO Auto-generated catch block  
                        e.printStackTrace();  
                    }     
                    i++;
                }
                bank.lookMoney();
            }  
        });  
        tsub.start();  
        tadd1.start();
        tadd.start();  
//        bank.lookMoney();
	}

}

class Bank {  

    private int count =100000;//账户余额  

    //存钱  
    public synchronized void addMoney(int money){  
        count +=money;  
//        System.out.println(System.currentTimeMillis()+"存进："+money);  
    }  

    //取钱  
    public synchronized void subMoney(int money){  
        if(count-money < 0){  
//            System.out.println("余额不足");  
            return;  
        }  
        count -=money;  
//        System.out.println(+System.currentTimeMillis()+"取出："+money);  
    }  

    //查询  
    public void lookMoney(){  
        System.out.println("账户余额："+count);  
    }  
}

同步解决方案：
1.同步方法
即有synchronized关键字修饰的方法。 由于java的每个对象都有一个内置锁，当用此关键字修饰方法时，
内置锁会保护整个方法。在调用该方法前，需要获得内置锁，否则就处于阻塞状态。

2.同步代码块
注：同步是一种高开销的操作，因此应该尽量减少同步的内容。通常没有必要同步整个方法，使用synchronized代码块同步关键代码即可。
//存钱  
    public void addMoney(int money){  

        synchronized (this) {  
            count +=money;  
        }  
        System.out.println(System.currentTimeMillis()+"存进："+money);  
    }  


3.使用特殊域变量（Volatile）实现线程同步
a.volatile关键字为域变量的访问提供了一种免锁机制
b.使用volatile修饰域相当于告诉虚拟机该域可能会被其他线程更新
c.因此每次使用该域就要重新计算，而不是使用寄存器中的值
d.volatile不会提供任何原子操作，它也不能用来修饰final类型的变量

public class Bank {  

    private volatile int count = 0;// 账户余额  

    // 存钱  
    public void addMoney(int money) {  

        count += money;  
        System.out.println(System.currentTimeMillis() + "存进：" + money);  
    }  

    // 取钱  
    public void subMoney(int money) {  

        if (count - money < 0) {  
            System.out.println("余额不足");  
            return;  
        }  
        count -= money;  
        System.out.println(+System.currentTimeMillis() + "取出：" + money);  
    }  

    // 查询  
    public void lookMoney() {  
        System.out.println("账户余额：" + count);  
    }  
}
就是因为volatile不能保证原子操作导致的，因此volatile不能代替synchronized。此外volatile会组织编译器对代码优化，因此能不使用它就不适用它吧。
它的原理是每次要线程要访问volatile修饰的变量时都是从内存中读取，而不是存缓存当中读取，因此每个线程访问到的变量值都是一样的。这样就保证了同步。

4.使用重入锁实现线程同步
在JavaSE50中新增了一个java.util.concurrent包来支持同步。ReentrantLock类是可重入、互斥、实现了Lock接口的锁， 
它与使用synchronized方法和快具有相同的基本行为和语义，并且扩展了其能力。
ReenreantLock类的常用方法有：
ReentrantLock() : 创建一个ReentrantLock实例
lock() : 获得锁
unlock() : 释放锁
注：ReentrantLock()还有一个可以创建公平锁的构造方法，但由于能大幅度降低程序运行效率，不推荐使用
public class Bank {  

    private  int count = 0;// 账户余额  

    //需要声明这个锁  
    private Lock lock = new ReentrantLock();  

    // 存钱  
    public void addMoney(int money) {  
        lock.lock();//上锁  
        try{  
        count += money;  
        System.out.println(System.currentTimeMillis() + "存进：" + money);  

        }finally{  
            lock.unlock();//解锁  
        }  
    }  

    // 取钱  
    public void subMoney(int money) {  
        lock.lock();  
        try{  

        if (count - money < 0) {  
            System.out.println("余额不足");  
            return;  
        }  
        count -= money;  
        System.out.println(+System.currentTimeMillis() + "取出：" + money);  
        }finally{  
            lock.unlock();  
        }  
    }  

    // 查询  
    public void lookMoney() {  
        System.out.println("账户余额：" + count);  
    }  
}
如果synchronized关键字能满足用户的需求，就用synchronized，因为它能简化代码 。
如果需要更高级的功能，就用ReentrantLock类，此时要注意及时释放锁，否则会出现死锁，通常在finally代码释放锁

5.使用局部变量实现线程同步
public class Bank {  

    private static ThreadLocal<Integer> count = new ThreadLocal<Integer>(){  

        @Override  
        protected Integer initialValue() {  
            // TODO Auto-generated method stub  
            return 0;  
        }  

    };  

    // 存钱  
    public void addMoney(int money) {  
        count.set(count.get()+money);  
        System.out.println(System.currentTimeMillis() + "存进：" + money);  

    }  

    // 取钱  
    public void subMoney(int money) {  
        if (count.get() - money < 0) {  
            System.out.println("余额不足");  
            return;  
        }  
        count.set(count.get()- money);  
        System.out.println(+System.currentTimeMillis() + "取出：" + money);  
    }  

    // 查询  
    public void lookMoney() {  
        System.out.println("账户余额：" + count.get());  
    }  
}
看了运行效果，一开始一头雾水，怎么只让存，不让取啊？看看ThreadLocal的原理：

如果使用ThreadLocal管理变量，则每一个使用该变量的线程都获得该变量的副本，副本之间相互独立，这样每一个线程都可以随意修改自己的变量副本，而不会对其他线程产生影响。现在明白了吧，原来每个线程运行的都是一个副本，也就是说存钱和取钱是两个账户，知识名字相同而已。所以就会发生上面的效果。

ThreadLocal与同步机制

a.ThreadLocal与同步机制都是为了解决多线程中相同变量的访问冲突问题
b.前者采用以”空间换时间”的方法，后者采用以”时间换空间”的方式



























