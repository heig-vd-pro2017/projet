package ch.tofind.commusica.database;

public interface DatabaseObject {
    public void update();
    public boolean equals(Object object);
    public int hashCode();
}

