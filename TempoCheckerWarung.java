import TempoObject.Access;
import TempoObject.TempoHelper;

// Interface for Tempo Checking Strategy
interface TempoCheckStrategy {
    void execute(Access access, boolean csvFlag, boolean summaryFlag) throws Exception;
}

// Concrete implementation of Tempo Checking Strategy
class DefaultTempoCheckStrategy implements TempoCheckStrategy {
    @Override
    public void execute(Access access, boolean csvFlag, boolean summaryFlag) throws Exception {
        TempoHelper.clearRevisiDir(access);
        TempoHelper.checkTempo(access, csvFlag, summaryFlag);
    }
}

// Configuration class for Tempo Checking
class TempoCheckConfiguration {
    private TempoCheckStrategy strategy;
    private Access access;
    private boolean csvFlag;
    private boolean summaryFlag;

    public TempoCheckConfiguration(TempoCheckStrategy strategy, Access access) {
        this(strategy, access, false, false);
    }

    public TempoCheckConfiguration(TempoCheckStrategy strategy, Access access, 
                                   boolean csvFlag, boolean summaryFlag) {
        this.strategy = strategy;
        this.access = access;
        this.csvFlag = csvFlag;
        this.summaryFlag = summaryFlag;
    }

    public void execute() throws Exception {
        strategy.execute(access, csvFlag, summaryFlag);
    }
}

public class TempoCheckerWarung {
    public static void main(String[] args) throws Exception {
        // Dependency Injection: Strategy and Configuration
        TempoCheckStrategy strategy = new DefaultTempoCheckStrategy();
        TempoCheckConfiguration config = new TempoCheckConfiguration(strategy, Access.WARUNG);
        config.execute();
    }
}
