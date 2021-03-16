package tk.iovr.aidlservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

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

    class MyBinder extends IPayAidl.Stub{
        @Override
        public void pay() throws RemoteException {
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