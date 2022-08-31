package tesseract.OTserver.objects;

public class Pair<E, T> {

    private E key;

    private T value;

    public Pair() {}

    public Pair(E key, T value) {
        this.key = key;
        this.value = value;
    }

    public E getKey() {
        return this.key;
    }

    public T getValue() {
        return this.value;
    }

    public Pair<E,T> setValue(T value) {
        this.value = value;
        return this;
    }


}
