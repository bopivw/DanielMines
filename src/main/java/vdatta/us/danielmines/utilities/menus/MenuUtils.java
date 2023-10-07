package vdatta.us.danielbox.util.menu;

public class MenuUtils {

    public static int slot(int x, int y) {
        return (y - 1) * 9 + (x - 1);
    }
}