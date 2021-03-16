package tk.iovr.aidlservice;

import android.os.Parcel;
import android.os.Parcelable;

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
        return "Person{" +
                "name='" + name + '\'' +
                '}';
    }
}
