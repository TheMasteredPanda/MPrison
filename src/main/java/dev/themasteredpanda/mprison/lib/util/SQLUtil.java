package dev.themasteredpanda.mprison.lib.util;

import dev.themasteredpanda.mprison.lib.exception.UtilException;

import java.sql.ResultSet;

public class SQLUtil
{
    public SQLUtil() throws UtilException
    {
       throw new UtilException();
    }

    public static int count(ResultSet set)
    {
        return Try.wrap(() -> {
            set.last();
            int innerRow = set.getRow();
            set.beforeFirst();
            return innerRow;
        });
    }
}
