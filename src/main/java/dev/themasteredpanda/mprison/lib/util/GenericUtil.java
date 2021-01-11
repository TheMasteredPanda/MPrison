package dev.themasteredpanda.mprison.lib.util;

import dev.themasteredpanda.mprison.lib.exception.UtilException;

public class GenericUtil
{
    public GenericUtil() throws UtilException
    {
        throw new UtilException();
    }

    public static <O> O cast(Object o)
    {
        return (O) o;
    }
}
