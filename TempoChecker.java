import TempoObject.Access;
import TempoObject.TempoHelper;

public class TempoChecker {

    public static void main(String[] args) throws Exception {
        TempoHelper.clearRevisiDir(Access.BIASA);
        TempoHelper.checkTempo(Access.BIASA, false, false);
    }

}
