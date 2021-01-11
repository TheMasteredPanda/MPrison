package dev.themasteredpanda.mprison.lib.command;

import dev.themasteredpanda.mprison.lib.util.NumberUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang.NotImplementedException;

@Getter
@NonNull
@AllArgsConstructor
public class ArgumentField
{
    private final String name;
    private final Type type;
    private final boolean required;

    public boolean isValid(Object o)
    {
        if (type.equals(Type.STRING)) {
            return (o instanceof String);
        }

        if (type.equals(Type.PLAYER_NAME)) {
            throw new NotImplementedException();
        }

        if (type.equals(Type.BYTE)) {
            return NumberUtil.parse((String) o, byte.class) != null;
        }

        if (type.equals(Type.SHORT)) {
            return NumberUtil.parse((String) o, short.class) != null;
        }

        if (type.equals(Type.INT)) {
            return NumberUtil.parse((String) o, int.class) != null;
        }

        if (type.equals(Type.DOUBLE)) {
            return NumberUtil.parse((String) o, double.class) != null;
        }

        if (type.equals(Type.FLOAT)) {
            return NumberUtil.parse((String) o, float.class) != null;
        }

        if (type.equals(Type.LONG)) {
            return NumberUtil.parse((String) o, long.class) != null;
        }

        return false;
    }

    public enum Type {
        BYTE,
        SHORT,
        INT,
        DOUBLE,
        LONG,
        FLOAT,
        STRING,
        PLAYER_NAME
    }
}
