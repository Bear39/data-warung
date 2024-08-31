import TempoObject.Access;
import TempoObject.TempoHelper;

public class TempoCheckerWarung {

    public static void main(String[] args) throws Exception {
        TempoHelper.clearRevisiDir(Access.WARUNG);
        TempoHelper.checkTempo(Access.WARUNG, false, false);
    }

}
