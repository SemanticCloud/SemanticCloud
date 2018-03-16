package owl.sod.hlds;


import java.util.List;
import java.util.ListIterator;


public class Concept {
    public String name;

    public Concept(String name) {
        this.name = name;
    }


    public boolean equals(Object obj) {
        if (!(obj instanceof Concept)) {
            return false;
        }


        return this.name.equals(((Concept) obj).name);
    }


    public static java.util.Map<String, Integer> conceptMap(List<Concept> concepts) {
        int capacity = (int) Math.ceil(concepts.size() / 0.75D + 1.0D);
        java.util.Map<String, Integer> map = new java.util.HashMap(capacity);

        if (!concepts.isEmpty()) {
            ListIterator<Concept> iterator = concepts.listIterator();
            while (iterator.hasNext()) {
                int value = iterator.nextIndex();
                String key = ((Concept) iterator.next()).name;
                map.put(key, Integer.valueOf(value));
            }
        }
        return map;
    }
}


