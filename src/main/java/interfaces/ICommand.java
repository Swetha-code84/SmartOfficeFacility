package com.smartoffice.facility.interfaces;

/**
 * The ICommand interface in the Command Pattern.
 * It declares an interface for executing an operation.
 */
public interface ICommand {

    /**
     * Executes the specific action encapsulated by the concrete command object.
     *
     * @return true if the command was executed successfully, false otherwise.
     */
    boolean execute();
}