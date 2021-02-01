package dev.themasteredpanda.mprison.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PrisonDoesNotExistException extends Exception
{
    private int prisonId;
}
