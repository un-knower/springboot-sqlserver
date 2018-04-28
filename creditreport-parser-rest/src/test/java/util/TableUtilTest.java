package util;

import com.yingu.project.service.service.simple.xdoc.OutlineUtil;
import com.yingu.project.util.utils.TableUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

@Slf4j
public class TableUtilTest {

    @Test
    public void typoCommaSymbol_outline() {
        String str = "1.2013年09月27H机构“CT发放的570, 000元（人民币）个人住房贷款，业务号X，抵押担保，〗.80期，按乃归还, 2028年09月27 R到期•截至2014年08月27日，";
        log.info(str);
        String aa = TableUtil.typoCommaSymbol_outline(str);
        log.info(aa);

        String sss = "1.2013年09月27H机构“CT发放的570, 000元（人民币）个人住房贷款，业务号X，抵押担保，〗.80期，按乃归还, 2028年09月27 R到期•截至2014年08月27日，";
        log.info(sss);
        String cc = TableUtil.typoCommaSymbol_outline(TableUtil.typo(sss));
        log.info(cc);
    }

    @Test
    public void typoOutline() {
        String str = "截M截M截M，乃乃";
        log.info(str);
        String sss = TableUtil.typoOutline(str);
        log.info(sss);
    }

    @Test
    public void substring() {
        String aa = "bbb";
        log.info(aa);
        String bb = StringUtils.substringBefore(aa, "a");
        log.info(bb);
    }

    @Test
    public void substring2() {
        String aa = "度折合人民币10000";
        aa = "10000";
        log.info(aa);
//        String bb = StringUtils.substringAfter(aa, "币");
        String bb = StringUtils.substringBefore(aa, "币");
        log.info(bb);
    }

    @Test
    public void string() {
        String DllFolder = "aaa";
        String DeveloperSN = "bb";
        log.info(String.format("loadEngine,%s,%s", DllFolder, DeveloperSN));
    }

    @Test
    public void string2() {
        String aa = "机构“中国民生银行”发放";
        log.info(aa);
        String[] begin = {"机", "构"};
        String bb = OutlineUtil.substringAfter(aa, begin);
        log.info(bb);
        String[] end = {"发", "放"};
        String cc = OutlineUtil.substringBefore(bb, end);
        log.info(cc);
    }
    @Test
    public void LineOfCredit() {
        String outline = "6.2015年10月19日机构“招商银行”发放的贷记卡（美元账户〉,业务号0000000000000000000000141264240001001840,授信额度折合人民币20, 000元，共享授信额度折合人民币0元，信用/ 免担保^截至2015年12月14田，";
        log.info(outline);
        String bb = OutlineUtil.LineOfCredit(outline);
        log.info(bb);
    }

    @Test
    public void isNumeric() {
        System.out.println(StringUtils.isNumeric("a"));
        System.out.println(StringUtils.isNumeric("000"));
        System.out.println(StringUtils.isNumeric("0"));
        System.out.println(StringUtils.isNumeric("1"));
        System.out.println(StringUtils.isNumericSpace("000"));
        System.out.println(StringUtils.isAlphanumeric("000"));
        System.out.println(StringUtils.isAlphanumericSpace("000"));
    }

    @Test
    public void deleteWhitespace() {
        String str = "1.2013年09月27H机构“CT发放的570, 000元（人民币）个人住房贷款，业务号X，抵押担保，〗.80期，按乃归还, 2028年09月27 R到期•截至2014年08月27日，";
        log.info(str);
        String sss = StringUtils.deleteWhitespace(str);
        log.info(sss);
        String ccc = TableUtil.typo(str);
        log.info(ccc);
    }

    @Test
    public void blank_spacing() {
        String str = "0期，按乃 归还, 2028年 09月27 R到";
        log.info(str);
        String sss = StringUtils.deleteWhitespace(str);
        log.info(sss);
        String ccc = TableUtil.typo(str);
        log.info(ccc);
        String bb = str.replaceAll(" ", "");
        log.info(bb);
    }

    @Test
    public void deleteWhitespace2() {
        String str = " A B C ";
        log.info(str);
        String sss = StringUtils.deleteWhitespace(str);
        log.info(str);
        log.info(sss);
        log.info("AAA");
    }
}
