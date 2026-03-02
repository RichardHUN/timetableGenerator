package hu.unideb.inf.timetableGenerator.domain.runner.parser;

import hu.unideb.inf.timetableGenerator.domain.model.Preference;
import hu.unideb.inf.timetableGenerator.domain.runner.Main;

/**
 * Parses constraints from command line arguments and adds them to the main preferences.
 */
public class ConstraintParser implements ArgParser {
    private final Main main;

    public ConstraintParser(Main main) {
        this.main = main;
    }

    /*
     * "5:Jane Doe:noClassesBefore:10:00"
     */
    @Override
    public void parse(String arg) {
        if (arg.equals("-")) {
            return;
        }

        main.getPreferences().add(Preference.of(arg));
    }
}
