package data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Asus- on 2018/7/24.
 *
 * 使用前缀树(字典树)来保证数据的唯一性
 */
public class Trie {

    private Node root;

    class Node {
        public Map<Character, Node> children = new HashMap<Character, Node>();

        private boolean isEnd;

        public boolean isEnd() {
            return isEnd;
        }

        public void setEnd() {
            this.isEnd = true;
        }

        public void put(char key, Node node) {
            children.put(key, node);
        }

        public Node get(char key) {
            return children.get(key);
        }

        public boolean containsKey(char key) {
            return children.containsKey(key);
        }
    }

    public Trie() {
        root = new Node();
    }

    /**
     * 前缀树插入操作
     */
    public void insert(String str) {
        Node node = root;
        for (int i = 0; i < str.length(); i++) {
            char currentChar = str.charAt(i);
            if (!node.containsKey(currentChar)) {
                node.put(currentChar, new Node());
            }
            node = node.get(currentChar);
        }
        node.setEnd();
    }

    /**
     * 前缀树查询操作，查询是否含有该前缀，isEnd()不一定为true
     *
     * @param str
     * @return Node
     */
    public Node searchPrefix(String str) {
        Node node = root;
        for (int i = 0; i < str.length(); i++) {
            char currentChar = str.charAt(i);
            if (node.containsKey(currentChar)) {
                node = node.get(currentChar);
            } else {
                return null;
            }
        }
        return node;
    }

    /**
     * 查询字符串是否在前缀树中
     *
     * @param str
     * @return boolean
     */
    public boolean isIn(String str) {
        Node node = searchPrefix(str);
        return node != null && node.isEnd();
    }
}
