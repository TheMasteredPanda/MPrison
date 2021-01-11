package dev.themasteredpanda.mprison.lib.util;

import dev.themasteredpanda.mprison.lib.exception.DeveloperException;
import dev.themasteredpanda.mprison.lib.exception.UtilException;

/**
 * Utility class for parsing strings to numbers.
 */
public class NumberUtil
{
    public NumberUtil() throws UtilException
    {
        throw new UtilException();
    }

    public static <V extends Number> V parse(String number, Class<?> type)
    {
        try {
            if (type.equals(byte.class)) {
                return GenericUtil.cast( Byte.parseByte(number));
            }

           if (type.equals(short.class)) {
               return GenericUtil.cast(Short.parseShort(number));
           }

           if (type.equals(int.class)) {
               return GenericUtil.cast(Integer.parseInt(number));
           }

           if (type.equals(double.class)) {
               return GenericUtil.cast(Double.parseDouble(number));
           }

           if (type.equals(long.class)) {
               return GenericUtil.cast(Long.parseLong(number));
           }

           if (type.equals(float.class)) {
               return GenericUtil.cast(Float.parseFloat(number));
           }

           throw new DeveloperException("Couldn't parse number " + number + ", type " + type.toString() + " not supported.");
        } catch (NumberFormatException | DeveloperException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
