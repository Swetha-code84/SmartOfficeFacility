// 1. CommandLoggerDecorator.java (in commands package)
package com.smartoffice.facility.commands;
import com.smartoffice.facility.interfaces.ICommand;
import com.smartoffice.facility.main.AppConstants;

/**
 * Structural Pattern: Decorator. Adds unified logging capability to any ICommand object.
 */
public class CommandLoggerDecorator implements ICommand {

    private final ICommand decoratedCommand;

    public CommandLoggerDecorator(ICommand decoratedCommand) {
        this.decoratedCommand = decoratedCommand;
    }

    @Override
    public boolean execute() {
        String commandName = decoratedCommand.getClass().getSimpleName();

        // Pre-Execution Logging
        AppConstants.LOGGER.info("Executing command: " + commandName);

        // Execute the core command logic
        boolean result = decoratedCommand.execute();

        // Post-Execution Logging
        AppConstants.LOGGER.info(commandName + " completed with status: " + (result ? "SUCCESS" : "FAILURE"));

        return result;
    }
}