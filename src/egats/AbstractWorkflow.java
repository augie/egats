package egats;

import java.util.List;

/**
 * The building block for workflows (a pipeline of separate processes). Provides methods which make very easy the
 *  process of creating a workflow.
 * @author Augie Hill - augie@umich.edu
 */
public abstract class AbstractWorkflow {

    private Server server = null;
    private List<String> args = null;
    private List<String> processIDs = null;

    /**
     * Sets the server upon which the workflow is being executed.
     * @param server 
     */
    public final void setServer(Server server) {
        this.server = server;
    }

    /**
     * Sets the list to which process IDs should be added by the workflow.
     * @param processIDs 
     */
    public final void setProcessIDList(List<String> processIDs) {
        this.processIDs = processIDs;
    }

    /**
     * Returns the workflow arguments.
     * @return 
     */
    protected final List<String> getArgs() {
        return args;
    }

    /**
     * Sets the workflow arguments.
     * @param args 
     */
    public final void setArgs(List<String> args) {
        this.args = args;
    }

    /**
     * Submits a new process for execution.
     * @param methodPath The path of the method to be executed.
     * @param args The arguments to be used by the method.
     * @return
     * @throws Exception 
     */
    protected final EGATSProcess submitProcess(String name, String methodPath, String[] args) throws Exception {
        // Checks
        if (server == null) {
            throw new NullPointerException("Server is null.");
        }
        if (processIDs == null) {
            throw new NullPointerException("Process ID list is null.");
        }
        if (methodPath == null) {
            throw new NullPointerException("Process method path is null.");
        }
        if (args == null) {
            throw new NullPointerException("Process args list is null.");
        }

        // Create the process and set required parameters
        EGATSProcess process = new EGATSProcess();
        process.setName(name);
        process.setMethodPath(methodPath);
        process.setArgs(args);

        // Cleans the process and adds it to the database
        process = EGATSProcess.create(process.getJSON());
        process.setServer(server);

        // Submit the process for execution
        if (server.getProcessExecutor().submit(process) == null) {
            // TODO: need to execute the process somehow. Occasional checks for processes to queue from the DB?
        }

        // Add to the list of process IDs
        processIDs.add(process.getID());

        return process;
    }

    /**
     * Sleeps until the given process has finished execution.
     * @param process
     * @throws Exception 
     */
    protected final void waitForProcessToFinish(EGATSProcess process) throws Exception {
        // Checks
        if (process == null) {
            throw new NullPointerException("Process is null.");
        }
        // Wait
        while (process.getFinishTime() == null) {
            Thread.sleep(100);
        }
    }

    /**
     * Override this method with your workflow.
     * @throws Exception 
     */
    public abstract void runWorkflow() throws Exception;
}
