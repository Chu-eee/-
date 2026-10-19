// 单链表类，T表示数据元素的数据类型；默认继承Object
public class SinglyList<T> extends Object
{
    public Node<T> head;                  // 定义一个head指针，指向单链表的头结点

    //（1）构造方法
    public SinglyList()                   // 构造空单链表
    {
        this.head = new Node<T>();        // 头结点 = 默认data和next值均为null
    }

    public SinglyList(T[] values)         // 尾插入values数组元素
    {
        this();                           // 创建空单链表，只有头结点
        Node<T> rear = this.head;         // 指向单链表最后一个结点
        for (int i = 0; i < values.length; i++)
            if (values[i] != null)        // 元素非空尾插入
            {
                rear.next = new Node<T>(values[i], null);
                rear = rear.next;
            }
    }

    public SinglyList(SinglyList<T> list) // 深拷贝构造方法，复制单链表list的所有结点
    {
        this();
        this.copy(list);
    }

    //（2）判空、存取元素、求长度、返回描述字符串
    public boolean isEmpty()              // 判空
    {
        return this.head.next == null;
    }

    public int size()                     // 返回单链表长度，O(n)
    {
        int n = 0;
        for (Node<T> p = this.head.next; p != null; p = p.next)
            n++;
        return n;
    }

    public T get(int i)                   // 返回第i个元素，若i越界则返回null。O(n)
    {
        Node<T> p = this.head.next;
        for (int j = 0; p != null && j < i; j++)
            p = p.next;
        if (i >= 0 && p != null)
            return p.data;
        return null;
    }

    public void set(int i, T x)           // 设置第i个元素为x，0≤i<长度且x!=null。O(n)
    {
        if (x == null)
            throw new NullPointerException("x==null");
        Node<T> p = this.head.next;
        for (int j = 0; p != null && j < i; j++)
            p = p.next;
        if (i >= 0 && p != null)
            p.data = x;
        else
            throw new IndexOutOfBoundsException(i + "");
    }

    public String toString()              // 返回所有元素的描述字符串，形式为“(,)”
    {
        String str = "(";
        for (Node<T> p = this.head.next; p != null; p = p.next)
        {
            str += p.data.toString();
            if (p.next != null)
                str += ",";
        }
        return str + ")";
    }

    //（3）插入
    public Node<T> insert(int i, T x)     // 插入x作为第i个元素，返回插入结点。对i容错
    {
        if (x == null)
            throw new NullPointerException("x==null");
        Node<T> front = this.head;        // front指向第i-1个结点
        for (int j = 0; front.next != null && j < i; j++)
            front = front.next;
        front.next = new Node<T>(x, front.next);
        return front.next;
    }

    public Node<T> insert(T x)            // 尾插入x，重载
    {
        return insert(Integer.MAX_VALUE, x);
    }

    //（4）删除
    public T remove(int i)                // 删除第i个元素，返回被删除元素；若i越界则返回null
    {
        Node<T> front = this.head;
        for (int j = 0; front.next != null && j < i; j++)
            front = front.next;
        if (i >= 0 && front.next != null)
        {
            T x = front.next.data;
            front.next = front.next.next;
            return x;
        }
        return null;
    }

    public void clear()                   // 删除单链表所有结点
    {
        this.head.next = null;
    }

    //（5）查找
    public Node<T> search(T key)          // 顺序查找，返回首个与key相等元素的结点；失败返回null
    {
        for (Node<T> p = this.head.next; p != null; p = p.next)
            if (key.equals(p.data))
                return p;
        return null;
    }

    public T remove(T key)                // 删除首个与key相等的元素，返回被删除元素；失败返回null
    {
        Node<T> front = this.head;
        Node<T> p = this.head.next;
        while (p != null && !key.equals(p.data))
        {
            front = p;
            p = p.next;
        }
        if (p != null)
        {
            front.next = p.next;
            return p.data;
        }
        return null;
    }

    //（6）深拷贝
    public void copy(SinglyList<T> list)  // 复制list所有元素到this，放弃this原结点。O(n)
    {
        this.clear();
        Node<T> rear = this.head;
        for (Node<T> p = list.head.next; p != null; p = p.next)
        {
            rear.next = new Node<T>(p.data, null);   // 深拷贝结点
            rear = rear.next;
        }
    }

    //（7）比较相等
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj)     // 比较this与obj引用的两条单链表是否相等
    {
        if (this == obj)
            return true;
        if (obj instanceof SinglyList<?>)
        {
            SinglyList<T> list = (SinglyList<T>) obj;
            Node<T> p = this.head.next;
            Node<T> q = list.head.next;
            while (p != null && q != null && p.data.equals(q.data))
            {
                p = p.next;
                q = q.next;
            }
            return p == null && q == null;
        }
        return false;
    }
}
