# AIDLIPC
AIDL 实现 进程间通信



AIDL有这些优势：

1. 跨进程调用服务端的方法；
2. 服务端并发处理数据时



## 实现步骤

### 1、编写AIDL暴露接口

1. 先实现需要序列化的对象 Person 类，实现 Parcelable 接口

   ```java
   public class Person implements Parcelable {
       //实现 Parcelable 接⼝是为了后序跨进程通信时使⽤
       private String name;
       public Person(String name) {
           this.name = name;
       }
       public Person(Parcel in ) {
           this.name = in.readString();
       }
       public static final Creator<Person> CREATOR = new Creator<Person>() {
           @Override
           public Person createFromParcel(Parcel in) {
               return new Person(in);
           }
           @Override
           public Person[] newArray(int size) {
               return new Person[size];
           }
       };
       @Override
       public int describeContents() {
           return 0;
       }
       @Override
       public void writeToParcel(Parcel dest, int flags) {
           dest.writeString(this.name);
       }
       @Override
       public String toString() {
           return "Person{" + "name='" + name + '\'' +'}';
       }
   }
   ```
   
2. 上面类写好之后新建一个 Person.aidl 文件，内容如下

   ```java
   // Person.aidl 文件   //这个 Person.aidl 的包名要和实体类包名⼀致。
   package tk.iovr.aidlservice;
   parcelable Person;//还要和声明的实体类在⼀个包⾥
   ```

3. 接下来是业务接口 IPayAidl.aidl ，包含所有的 服务端暴露给客户端的方法，如果用到序列化对象， 需要导入路径

      ```java
      // IPayAidl.aidl
      package tk.iovr.aidlservice;
import tk.iovr.aidlservice.Person;//⾮基本类型的数据需要导⼊全路径， 如 Person 类
      interface IPayAidl {
          void pay(); //
      		//除了基本数据类型，其他类型的参数都需要标上⽅向类型：in(输⼊), out(输出), inout(输⼊输出)
          void addPerson(in Person person);
          List<Person> getPersonList();
      }
      ```
      
### 2. 服务端编写

这里主要是将 aidl 接口 中的方法实现，客户端通过 Binder 对象的调用， 来执行具体的实现方法

```java
public class MyService extends Service {
    private ArrayList<Person> mPersons;
    public MyService() {}
    @Override
    public IBinder onBind(Intent intent) {
        mPersons = new ArrayList<>();
        MyBinder s =  new MyBinder();
        if (s ==null){
            Log.i("MyService", "MyBinder is null");
        }
        return s;   //return MyBinder通过ServiceConnection在activity中拿到MyBinder
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
    public void  payServices(){
        Log.i("MyService", "payService: --------");
    }
    // 接口方法的 具体实现 放在 MyBinder 中
    class MyBinder extends IPayAidl.Stub{
        @Override
        public void  pay(){
            payServices();
        }
        @Override
        public void addPerson(Person person) throws RemoteException {
            Log.i("MyService", "addPerson:"+person.toString());
            mPersons.add(person);
        }
        @Override
        public List<Person> getPersonList() throws RemoteException {
            return mPersons;
        }
    }
}
```

注意要在 AndroidManifest.xml 指定服务的 具体 包名

```xml
        <service
            android:name=".MyService"
            android:enabled="true"
            android:exported="true">
            <intent-filter>
                <action android:name="tk.iovr.aidlservice.MyService" />
            </intent-filter>
        </service>
```

### 3.客户端编写

将 服务端 暴露的AIDL接口和 Person.java ，拷贝一份 到客户端对应路径，

在Activity 中绑定 服务端的 服务，ServiceConnection   中 获得服务端实例

```java
public class MainActivity extends AppCompatActivity {
  
    Button btnPay;
    private IPayAidl myBinder;//获取 服务端 的实例

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder =  IPayAidl.Stub.asInterface(service);
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            myBinder = null;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent();
        intent.setAction("tk.iovr.aidlservice.MyService");
        intent.setPackage("tk.iovr.aidlservice");
        bindService(intent,connection,BIND_AUTO_CREATE);// 开启服务

        btnPay = findViewById(R.id.btn_paly);
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    myBinder.pay();
                    myBinder.addPerson(new Person("王武"));
                    myBinder.addPerson(new Person("赵四"));
                    myBinder.addPerson(new Person("李三"));
                    List<Person>  peopleList =myBinder.getPersonList();
                    for (Person p : peopleList) {
                        Log.d("AIDLClient","   "+p.toString());
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();//因为是跨程序调用服务，可能会出现远程异常
                }
            }
        });
    }
}
```

客户端 在  BindService 时，Android 11 上面 查询其他 应用信息 需要进行权限申请：

- 第一种方式：只指定需要查询 的应用包名

  ```xml
      <queries>
          <package android:name="tk.iovr.messagehandlerservice"/>
      </queries>
  ```

  第二种方式：声明 查询所有应用信息的权限

- ```xml
  <uses-permission android:name="android.permission.QUERY_ALL_PACKAGES"
  								 tools:ignore="QueryAllPackagesPermission" />
  ```

  