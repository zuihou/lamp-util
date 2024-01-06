package top.tangyh.basic.cache.utils;

/**
 * @author zuihou
 */
public class BytesWrapper<T> implements Cloneable {
    private T value;

    public BytesWrapper() {
    }

    public BytesWrapper(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public BytesWrapper<T> clone() {
        try {
            return (BytesWrapper<T>) super.clone();
        } catch (CloneNotSupportedException e) {
            return new BytesWrapper<>();
        }
    }
}
