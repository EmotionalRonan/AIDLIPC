// IPayAidl.aidl
package tk.iovr.aidlservice;


//⾮基本类型的数据需要导⼊，⽐如上⾯的 Person，需要导⼊它的全路径
import tk.iovr.aidlservice.Person;
// Declare any non-default types here with import statements

interface IPayAidl {

/**
 * 除了基本数据类型，其他类型的参数都需要标上⽅向类型：in(输⼊), out(输出), inout(输⼊输出)
 */
    void pay();

    void addPerson(in Person person);

    List<Person> getPersonList();

}