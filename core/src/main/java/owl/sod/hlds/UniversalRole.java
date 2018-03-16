package owl.sod.hlds;

public class UniversalRole extends Concept {
    public SemanticDescription filler;

    public UniversalRole() {
        this("", new SemanticDescription());
    }

    public UniversalRole(String name) {
        this(name, new SemanticDescription());
    }

    public UniversalRole(String name, SemanticDescription filler) {
        super(name);
        this.filler = filler;
    }

    public static UniversalRole UniversalRoleFactory(String name, SemanticDescription sd) {
        return new UniversalRole(name, sd);
    }

    public String toString() {
        String outputString = this.filler.toString();
        String app = replaceString(outputString, "(", "");
        app = replaceString(app, ")", "");
        if (app.length() > 0) {
            outputString = '¥' + this.name + "." + outputString + ",";
            return outputString;
        }
        return "";
    }

    public static String replaceString(String _text, String _searchStr, String _replacementStr) {
        StringBuffer sb = new StringBuffer();

        int searchStringPos = _text.indexOf(_searchStr);
        int startPos = 0;
        int searchStringLength = _searchStr.length();

        while (searchStringPos != -1) {
            sb.append(_text.substring(startPos, searchStringPos)).append(_replacementStr);
            startPos = searchStringPos + searchStringLength;
            searchStringPos = _text.indexOf(_searchStr, startPos);
        }

        sb.append(_text.substring(startPos, _text.length()));

        return sb.toString();
    }

    public boolean isTheSame(UniversalRole r) {
        boolean flag = true;
        if (r.name.equals(this.name)) {
            if ((r.filler.getConcepts() != null) && (this.filler.getConcepts() != null)) {
                for (int i = 0; i < r.filler.getConcepts().size(); i++) {
                    AtomicConcept val1 = (AtomicConcept) r.filler.getConcepts().get(i);
                    boolean uguale = false;
                    for (int j = 0; j < this.filler.getConcepts().size(); j++) {
                        AtomicConcept val2 = (AtomicConcept) this.filler.getConcepts().get(j);
                        uguale = (uguale) || (val2.equals(val1));
                    }
                    if (!uguale)
                        return false;
                }
            }
            if ((r.filler.getComplements() != null) && (this.filler.getComplements() != null)) {
                for (int i = 0; i < r.filler.getComplements().size(); i++) {
                    AtomicConcept val1 = (AtomicConcept) r.filler.getComplements().get(i);
                    boolean uguale = false;
                    for (int j = 0; j < this.filler.getComplements().size(); j++) {
                        AtomicConcept val2 = (AtomicConcept) this.filler.getComplements().get(j);
                        uguale = (uguale) || (val2.equals(val1));
                    }
                    if (!uguale)
                        return false;
                }
            }
            if ((r.filler.greaterThanRoles != null) && (this.filler.greaterThanRoles != null)) {
                for (int i = 0; i < r.filler.greaterThanRoles.size(); i++) {
                    GreaterThanRole val1 = (GreaterThanRole) r.filler.greaterThanRoles.get(i);
                    boolean uguale = false;
                    for (int j = 0; j < this.filler.greaterThanRoles.size(); j++) {
                        GreaterThanRole val2 = (GreaterThanRole) this.filler.greaterThanRoles.get(j);
                        uguale = (uguale) || (val2.isTheSame(val1));
                    }
                    if (!uguale)
                        return false;
                }
            }
            if ((r.filler.lessThanRoles != null) && (this.filler.lessThanRoles != null)) {
                for (int i = 0; i < r.filler.lessThanRoles.size(); i++) {
                    LessThanRole val1 = (LessThanRole) r.filler.lessThanRoles.get(i);
                    boolean uguale = false;
                    for (int j = 0; j < this.filler.lessThanRoles.size(); j++) {
                        LessThanRole val2 = (LessThanRole) this.filler.lessThanRoles.get(j);
                        uguale = (uguale) || (val2.isTheSame(val1));
                    }
                    if (!uguale)
                        return false;
                }
            }
            if ((r.filler.universalRoles != null) && (this.filler.universalRoles != null)) {
                for (int i = 0; i < r.filler.universalRoles.size(); i++) {
                    UniversalRole val1 = (UniversalRole) r.filler.universalRoles.get(i);
                    boolean uguale = false;
                    for (int j = 0; j < this.filler.universalRoles.size(); j++) {
                        UniversalRole val2 = (UniversalRole) this.filler.universalRoles.get(j);
                        uguale = (uguale) || (val2.isTheSame(val1));
                    }
                    if (!uguale) {
                        return false;
                    }
                }
            }
        } else {
            flag = false;
        }

        return flag;
    }
}


