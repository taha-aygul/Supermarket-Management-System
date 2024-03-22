public interface Node<T> {
    T getData();

    void setData(T data);

    Node<T> getNext();

    void setNext(Node<T> next);

    boolean hasNext();
}

