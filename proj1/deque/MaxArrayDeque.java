package deque;

import java.util.Comparator;

public class MaxArrayDeque<Item> extends ArrayDeque<Item> {

    private Comparator<Item> comparator;

    // 构造函数：接收一个 Comparator<Item> 用于元素的比较
    public MaxArrayDeque(Comparator<Item> c) {
        this.comparator = c;  // 保存 comparator 以便在 max() 方法中使用
    }

    // 默认的 max 方法，使用构造时提供的 Comparator<Item>
    public Item max() {
        if (isEmpty()) {
            return null;
        }

        Item maxItem = get(0);  // 假设第一个元素是最大值
        for (int i = 1; i < size(); i++) {
            Item current = get(i);
            if (comparator.compare(current, maxItem) > 0) {
                maxItem = current;
            }
        }
        return maxItem;
    }

    // 使用指定的 Comparator<Item> 来查找最大值
    public Item max(Comparator<Item> c) {
        if (isEmpty()) {
            return null;
        }

        Item maxItem = get(0);  // 假设第一个元素是最大值
        for (int i = 1; i < size(); i++) {
            Item current = get(i);
            if (c.compare(current, maxItem) > 0) {
                maxItem = current;
            }
        }
        return maxItem;
    }
}
