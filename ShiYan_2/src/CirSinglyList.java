// 循环单链表类，T表示数据元素的数据类型
// 与普通单链表的唯一结构差别：尾结点的next不再为null，而是指向头结点head
public class CirSinglyList<T> extends SinglyList<T>
{
    //（1）构造方法
    public CirSinglyList()                       // 构造空循环单链表
    {
        super();                                 // 先创建头结点，此时head.next==null
        this.head.next = this.head;              // 空循环表：头结点指向自己
    }

    public CirSinglyList(T[] values)             // 尾插入values数组元素
    {
        this();
        Node<T> rear = this.head;
        for (int i = 0; i < values.length; i++)
            if (values[i] != null)
            {
                rear.next = new Node<T>(values[i], this.head);   // 新结点直接指向头结点
                rear = rear.next;
            }
    }

    public CirSinglyList(SinglyList<T> list)     // 深拷贝list，兼容普通单链表和循环单链表
    {
        this();
        this.copy(list);
    }

    //（2）以下方法都要把“p != null”改成“p != this.head”，
    //     否则循环单链表会永远走不到null而死循环

    @Override
    public boolean isEmpty()
    {
        return this.head.next == this.head;
    }

    @Override
    public int size()
    {
        int n = 0;
        for (Node<T> p = this.head.next; p != this.head; p = p.next)
            n++;
        return n;
    }

    @Override
    public T get(int i)
    {
        if (i < 0)
            return null;
        Node<T> p = this.head.next;
        for (int j = 0; j < i && p != this.head; j++)
            p = p.next;
        return p != this.head ? p.data : null;
    }

    @Override
    public void set(int i, T x)
    {
        if (x == null)
            throw new NullPointerException("x==null");
        if (i < 0)
            throw new IndexOutOfBoundsException(i + "");
        Node<T> p = this.head.next;
        for (int j = 0; j < i && p != this.head; j++)
            p = p.next;
        if (p == this.head)
            throw new IndexOutOfBoundsException(i + "");
        p.data = x;
    }

    @Override
    public String toString()
    {
        String str = "(";
        Node<T> p = this.head.next;
        while (p != this.head)
        {
            str += p.data.toString();
            p = p.next;
            if (p != this.head)
                str += ",";
        }
        return str + ")";
    }

    @Override
    public Node<T> insert(int i, T x)            // 对i容错：i<0头插入，i>=长度尾插入
    {
        if (x == null)
            throw new NullPointerException("x==null");
        Node<T> front = this.head;
        for (int j = 0; front.next != this.head && j < i; j++)
            front = front.next;
        front.next = new Node<T>(x, front.next);
        return front.next;
    }

    @Override
    public Node<T> insert(T x)
    {
        return insert(Integer.MAX_VALUE, x);
    }

    @Override
    public T remove(int i)
    {
        if (i < 0)
            return null;
        Node<T> front = this.head;
        for (int j = 0; front.next != this.head && j < i; j++)
            front = front.next;
        if (front.next == this.head)             // i越界
            return null;
        T x = front.next.data;
        front.next = front.next.next;
        return x;
    }

    @Override
    public void clear()
    {
        this.head.next = this.head;              // 注意不是null
    }

    @Override
    public Node<T> search(T key)
    {
        for (Node<T> p = this.head.next; p != this.head; p = p.next)
            if (key.equals(p.data))
                return p;
        return null;
    }

    @Override
    public T remove(T key)
    {
        Node<T> front = this.head;
        Node<T> p = this.head.next;
        while (p != this.head && !key.equals(p.data))
        {
            front = p;
            p = p.next;
        }
        if (p != this.head)
        {
            front.next = p.next;
            return p.data;
        }
        return null;
    }

    //（3）深拷贝，兼容普通单链表和循环单链表
    @Override
    public void copy(SinglyList<T> list)
    {
        this.clear();
        Node<T> rear = this.head;
        // 普通单链表以null结束；循环单链表以list.head结束
        Node<T> stop = (list instanceof CirSinglyList<?>) ? list.head : null;
        for (Node<T> p = list.head.next; p != stop; p = p.next)
        {
            rear.next = new Node<T>(p.data, this.head);
            rear = rear.next;
        }
        rear.next = this.head;                   // list为空时也能正确成环
    }

    //（4）比较相等，兼容普通单链表和循环单链表
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (!(obj instanceof SinglyList<?>))
            return false;

        SinglyList<T> list = (SinglyList<T>) obj;
        Node<T> p = this.head.next;
        Node<T> q = list.head.next;

        if (list instanceof CirSinglyList<?>)
        {
            Node<T> stopQ = list.head;
            while (p != this.head && q != stopQ)
            {
                if (!p.data.equals(q.data))
                    return false;
                p = p.next;
                q = q.next;
            }
            return p == this.head && q == stopQ;
        }
        else
        {
            while (p != this.head && q != null)
            {
                if (!p.data.equals(q.data))
                    return false;
                p = p.next;
                q = q.next;
            }
            return p == this.head && q == null;
        }
    }

    //（5）本题要求实现的方法
    // 返回并集（this + list），把list的所有元素插入到this的第i个位置之前。
    // 要求：this不变，参数list不变，返回值中的结点全部是深拷贝。
    public CirSinglyList<T> union(int i, SinglyList<T> list)
    {
        CirSinglyList<T> result = new CirSinglyList<T>();
        Node<T> rear = result.head;              // rear始终指向结果表的尾结点

        // 第1步：深拷贝this的前i个结点（i<0时一个也不拷）
        Node<T> p = this.head.next;
        int count = 0;
        while (p != this.head && count < i)
        {
            rear.next = new Node<T>(p.data, result.head);
            rear = rear.next;
            p = p.next;
            count++;
        }

        // 第2步：深拷贝list的全部结点
        // 兼容list是普通单链表（null结尾）或循环单链表（head结尾）两种情况
        Node<T> stop = (list instanceof CirSinglyList<?>) ? list.head : null;
        for (Node<T> q = list.head.next; q != stop; q = q.next)
        {
            rear.next = new Node<T>(q.data, result.head);
            rear = rear.next;
        }

        // 第3步：深拷贝this剩余的结点
        while (p != this.head)
        {
            rear.next = new Node<T>(p.data, result.head);
            rear = rear.next;
            p = p.next;
        }

        // 第4步：尾结点指回头结点，形成循环
        rear.next = result.head;
        return result;
    }
}
