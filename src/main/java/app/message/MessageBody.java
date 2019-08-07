package app.message;

import app.util.Pagination;

import javax.sql.DataSource;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Map;

/**
 *
 * a Message body.
 *
 * @author OAK
 *
 */
public interface MessageBody {

    final Integer rtype = 2;

    final Integer ptype = 3;

    /**
     * Thread local attributes.
     */
    ThreadLocal<Map> attrs = new ThreadLocal<>();

    /**
     * a Message data source.
     * @return data source.
     */
    DataSource getSource();

    /**
     * Put attributes to a multiple value map.
     * @param multiValueMap a multiple value map.
     */
    default void putAttrs(Map multiValueMap){
        attrs.set(multiValueMap);
    }

    /**
     * Get a attributes.
     * @return a multiple value map.
     */
    default Map getAttrs(){
        return attrs.get();
    }

    /**
     * Get a message.
     * @return a message.
     */
    String getMessage();

    /**
     * Call back function.
     * @param target target object.
     * @param pagination page.
     * @param attrs attributes collections.
     */
    <T> void callBack(T target, Pagination pagination, Map attrs);

    default <T> void invoke(Object target, String methodName, T arguments, Class<?>... argumentTypes) throws Throwable {
        MethodType methodType = null;
        if(argumentTypes.length < rtype){
            methodType = MethodType.methodType(argumentTypes[0]);
        }else if(argumentTypes.length < ptype){
            methodType = MethodType.methodType(argumentTypes[0], argumentTypes[1]);
        }
        MethodHandle produceMethod = MethodHandles.lookup().findVirtual(target.getClass(), methodName, methodType);
        produceMethod.invoke(target, arguments);
    }

}
