package egats;

/**
 *
 * @author Augie
 */
public class EGATProcessTest extends EGATSTestCase {

    public static final boolean test() {
        return false;
    }

    public void testRun() throws Exception {
        EGATProcess o = new EGATProcess();
        o.setMethodPath("egats.EGATProcessTest.test");
        o.run();
        EGATSObject output = EGATSObject.CACHE.get(o.getOutputID());
        assertEquals(Data.GSON.toJson(Boolean.FALSE), output.getObject());
    }
}
