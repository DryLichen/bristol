import java.util.*;

public class BooleanExpressionEvaluator {

    private static class TreeNode {
        char val;
        TreeNode left, right;

        public TreeNode(char val) {
            this.val = val;
            this.left = null;
            this.right = null;
        }
    }

    public static boolean evaluateExpression(String expression) {
        // Build a binary expression tree
        Stack<TreeNode> stack = new Stack<>();
        for (char c : expression.toCharArray()) {
            if (c == ' ') {
                continue;
            }

            TreeNode node = new TreeNode(c);
            if (c == '!' || c == '&' || c == '|') {
                try {
                    node.right = stack.pop();
                    if (c != '!') {
                        node.left = stack.pop();
                    }
                } catch (EmptyStackException e) {
                    throw new IllegalArgumentException("Invalid expression", e);
                }
            }
            stack.push(node);
        }

        TreeNode root = stack.pop();

        // Evaluate the expression recursively
        return evaluateNode(root);
    }

    private static boolean evaluateNode(TreeNode node) {
        if (node == null) {
            return false;
        }

        switch (node.val) {
            case '!':
                return !evaluateNode(node.right);
            case '&':
                return evaluateNode(node.left) && evaluateNode(node.right);
            case '|':
                return evaluateNode(node.left) || evaluateNode(node.right);
            default:
                return node.val == 'T';
        }
    }

    public static void main(String[] args) {
        String expression = "! ( T & F ) | ( T & F )";
        boolean result = evaluateExpression(expression);
        System.out.println(result); // prints false
    }
}
