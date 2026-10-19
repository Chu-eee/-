// 单链表节点类，T指定结点的元素类型
public class Node<T>
{
    public T data;                        // 数据域，存储数据元素
    public Node<T> next;                  // 地址域，引用后继结点，存箭头

    public Node(T data, Node<T> next)     // 节点的构造方法。构造节点，data指定数据元素，next指定后继结点
    {
        this.data = data;
        this.next = next;
    }

    public Node()                         // 默认构造
    {
        this(null, null);
    }
}
