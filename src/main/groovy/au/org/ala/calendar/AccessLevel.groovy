package au.org.ala.calendar

/**
 * Enum for access levels
 *
 * Note: "starred" might need to be moved as its not a perfect fit here
 *
 * @author "Nick dos Remedios <Nick.dosRemedios@csiro.au>"
 */
public enum AccessLevel {
    admin(100),
    caseManager(60),
    editor(40),
    projectParticipant(30),
    starred(20)

    private int code
    private AccessLevel(int c) {
        code = c;
    }

    public int getCode() {
        return code;
    }
}