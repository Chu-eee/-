// 实验题 2-24 测试类
// 方法：CirSinglyList<T> union(int i, SinglyList<T> list)
public class CirSinglyListTest
{
    public static void main(String[] args)
    {
        CirSinglyList<Integer> a = new CirSinglyList<Integer>(new Integer[]{1, 2, 3});
        SinglyList<Integer> list = new SinglyList<Integer>(new Integer[]{4, 5});

        System.out.println("this = " + a);
        System.out.println("list = " + list);
        System.out.println();

        check(a, list, 1);       // 插入中间
        check(a, list, 0);       // 头插入
        check(a, list, 3);       // 尾插入
        check(a, list, 99);      // i 越界，尾插入
        check(a, list, -1);      // i < 0，头插入

        System.out.println();
        System.out.println("调用后 this = " + a + "  （应仍为 (1,2,3)）");
        System.out.println("调用后 list = " + list + "  （应仍为 (4,5)）");

        // 深拷贝验证：结果表的结点不能和 this、list 共用
        CirSinglyList<Integer> r = a.union(1, list);
        System.out.println("result 与 this 共用结点: " + shareNode(r, a));
        System.out.println("result 与 list 共用结点: " + shareNode(r, list));

        // 修改结果表，原表不应受影响
        r.set(0, 99);
        System.out.println("修改 result 后：this = " + a + "，list = " + list);

        // 空表测试
        CirSinglyList<Integer> empty = new CirSinglyList<Integer>();
        System.out.println();
        System.out.println("空表 union(0, list) = " + empty.union(0, list));
        System.out.println("a.union(1, 空表) = " + a.union(1, new SinglyList<Integer>()));
    }

    private static void check(CirSinglyList<Integer> a, SinglyList<Integer> list, int i)
    {
        CirSinglyList<Integer> r = a.union(i, list);
        System.out.println("union(" + i + ") = " + r
                + "    size=" + r.size()
                + "    成环=" + isCircle(r));
    }

    // 检查尾结点是否指回头结点
    private static boolean isCircle(CirSinglyList<Integer> list)
    {
        Node<Integer> p = list.head.next;
        int steps = 0;
        while (p != list.head)
        {
            if (p == null || ++steps > 10000)
                return false;
            p = p.next;
        }
        return true;
    }

    // 检查两条表是否共用了同一个Node对象
    private static boolean shareNode(SinglyList<Integer> x, SinglyList<Integer> y)
    {
        for (Node<Integer> p = x.head.next; p != null && p != x.head; p = p.next)
            for (Node<Integer> q = y.head.next; q != null && q != y.head; q = q.next)
                if (p == q)
                    return true;
        return false;
    }
}
