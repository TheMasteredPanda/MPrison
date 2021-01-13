package dev.themasteredpanda.mprison.lib.exception;

/**
 * Exception made for the circumstance where an attempt to execute
 * an already executed SQL statement is made.
 */
public class SQLAlreadyExecutedException extends Exception
{
    public SQLAlreadyExecutedException()
    {
        super("Already executed statement.");
    }
}
