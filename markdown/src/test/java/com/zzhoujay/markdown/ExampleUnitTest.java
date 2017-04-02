package com.zzhoujay.markdown;

import com.zzhoujay.markdown.util.NumberKit;

import org.junit.Test;

import java.io.IOException;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {

    private static final String testString = "# MarkDown\n" +
            "\n" +
            "> Android平台的Markdown解析器\n" +
            "\n" +
            "\n" +
            "```````````````````````\n" +
            "ASDFSDAFASDF\n" +
            "```````````````````````\n" +
            "\n" +
            "1. 111111\n" +
            "2. 222222\n" +
            "3. 333333\n" +
            "\n" +
            "* asdfasdf\n" +
            "* dfsgsdfa\n" +
            "\n" +
            "hello`gg`world\n" +
            "\n" +
            "__by zzhoujay__\n" +
            "\n";

    @Test
    public void addition_isCorrect() throws Exception {

    }

    @Test
    public void test() throws IOException {
//        System.out.println(NumberKit.toRomanNumerals(999));
//        for(int i=0;i<100;i++){
//            System.out.println(NumberKit.toABC(i));
//        }
        System.out.println(NumberKit.toABC(0));
        System.out.println((int) 'a');
        System.out.println(Integer.toString(24, 26));
    }
}