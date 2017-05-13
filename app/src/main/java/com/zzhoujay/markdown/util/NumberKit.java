package com.zzhoujay.markdown.util;

/**
 * created by zhou on 16-7-17.
 */
public class NumberKit {

    private static final String[] digit = {"", "i", "ii", "iii", "iv", "v", "vi", "vii", "viii", "ix"};
    private static final String[] ten = {"", "x", "xx", "xxx", "xl", "l", "lx", "lxx", "lxxx", "xc"};
    private static final String[] hundreds = {"", "c", "cc", "ccc", "cd", "d", "dc", "dcc", "dccc", "cm"};
    private static final String[] thousand = {"", "m", "mm", "mmm"};

    private static final int ROMAN_MAX = 4996;

    public static String toRomanNumerals(int num) {
        while (num > ROMAN_MAX) {
            num -= ROMAN_MAX;
        }
        String th = thousand[num / 1000];
        num %= 1000;
        String hu = hundreds[num / 100];
        num %= 100;
        String te = ten[num / 10];
        num %= 10;
        String di = digit[num];
        return String.format("%s%s%s%s", th, hu, te, di);
    }

    public static String toABC(int num) {
        int a = num / 26;
        int b = num % 26;
        StringBuilder sb = new StringBuilder();
        if (a > 26) {
            sb.append(toABC(a - 1)).append((char) (b + 'a'));
        } else if (a == 0) {
            sb.append((char) (b + 'a'));
        } else {
            sb.append((char) (a + 'a')).append((char) (b + 'a'));
        }
        return sb.toString();
//        while (num < 0) {
//            num += 26;
//        }
//        while (num > 26) {
//            num -= 26;
//        }
//        return String.valueOf((char) (num + 'a'));
    }

}
