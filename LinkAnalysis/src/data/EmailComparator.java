package data;

import java.util.Comparator;

public class EmailComparator implements Comparator<Email>{
    public int compare(Email p1, Email p2)
    {
        return Double.compare(p1.getbVal(),p2.getbVal());
    }
}
