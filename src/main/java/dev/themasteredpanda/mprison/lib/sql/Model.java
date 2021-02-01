package dev.themasteredpanda.mprison.lib.sql;

import java.sql.ResultSet;
import java.util.HashMap;

/**
 * Used to serialize and deserialize ResultSets and Models into
 * and from objects.
 */
public interface Model
{
    HashMap<String, Object> deserialize();

    <V extends Model> V serialize(ResultSet set);
}
