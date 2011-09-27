package egats;

/**
 *
 * @author Augie Hill - augman85@gmail.com
 */
public class InputObject extends DataObject {

    public InputObject() throws Exception {
        Data.insert(Data.INPUT_OBJECTS, this);
    }
}
