package com.smartoffice.facility.commands;
import com.smartoffice.facility.interfaces.ICommand;
import com.smartoffice.facility.main.AppConstants;
public class CommandLoggerDecorator implements ICommand {
    private final ICommand decoratedCommand;
    public CommandLoggerDecorator(ICommand decoratedCommand) {
        this.decoratedCommand = decoratedCommand;
    }
    @Override
    public boolean execute() {
        String commandName = decoratedCommand.getClass().getSimpleName();
        AppConstants.LOGGER.info("Executing command: " + commandName);
        boolean result = decoratedCommand.execute();
        AppConstants.LOGGER.info(commandName + " completed with status: " + (result ? "SUCCESS" : "FAILURE"));
        return result;
    }
}
