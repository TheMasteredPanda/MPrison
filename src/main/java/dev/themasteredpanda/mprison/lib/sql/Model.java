package dev.themasteredpanda.mprison.lib.sql;

import java.sql.ResultSet;

public interface Model
{
    String deserialize();

    <V extends Model> V serialize(ResultSet set);
}
