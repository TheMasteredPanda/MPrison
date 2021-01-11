package dev.themasteredpanda.mprison.lib.util;

public class Try
{

    public static <V> V wrap(TryMethod method)
    {
        try {
           return (V) method.exec();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public interface TryMethod<V>
    {
       V exec() throws Exception;
    }
}
