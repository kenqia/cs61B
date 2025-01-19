package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {

    private Comparator<T> comparator;

    // 构造函数：接收一个 Comparator<T> 用于元素的比较
    public MaxArrayDeque(Comparator<T> c) {
        this.comparator = c;  // 保存 comparator 以便在 max() 方法中使用
    }

    // 默认的 max 方法，使用构造时提供的 Comparator<T>
    public T max() {
        if (isEmpty()) {
            return null;
        }

        T maxT = get(0);  // 假设第一个元素是最大值
        for (int i = 1; i < size(); i++) {
            T current = get(i);
            if (comparator.compare(current, maxT) > 0) {
                maxT = current;
            }
        }
        return maxT;
    }

    // 使用指定的 Comparator<T> 来查找最大值
    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }

        T maxT = get(0);  // 假设第一个元素是最大值
        for (int i = 1; i < size(); i++) {
            T current = get(i);
            if (c.compare(current, maxT) > 0) {
                maxT = current;
            }
        }
        return maxT;
    }
}
