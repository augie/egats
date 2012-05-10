package egats;

/**
 * A bit of a hack to incorporate Python.
 * 
 * @author Augie Hill - augie@umich.edu
 */
public class EGATSObjectFile extends Object {

    // File name
    public String name;
    // File contents
    public String object;

    /**
     * 
     * @param name
     * @param object 
     */
    public EGATSObjectFile(String name, String object) {
        this.name = name;
        this.object = object;
    }
}
