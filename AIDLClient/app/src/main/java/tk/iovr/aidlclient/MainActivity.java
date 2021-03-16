package tk.iovr.aidlclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.List;

import tk.iovr.aidlservice.IPayAidl;
import tk.iovr.aidlservice.Person;

public class MainActivity extends AppCompatActivity {

    Button btnPay;
    private IPayAidl myBinder;//定义AIDL


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
                    ////因为是跨程序调用服务，可能会出现远程异常
                    e.printStackTrace();
                }
            }
        });


    }
}