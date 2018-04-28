package com.yingu.project.service.service.simple;

import com.yingu.project.service.service.simple.xdoc.SimilarityKeyword;
import com.yingu.project.service.service.simple.xdoc.XdocUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Date: Created in 2018/4/19 14:46
 * @Author: wm
 */
@SpringBootTest
@Slf4j
public class XdocUtilTest {

    @Test
    public void substringFuzzyBetween() {
        String text = " oaCL^ioA ^^ r k > w 4 e yoi ~(. !■>'  fb - 个人信用报告以來 报告编号: 2017120600004812665667 査询时间:  2017.12.06 1152:45 报告时间:2017.12.06185928 姓名:邵欣涛 证件类型:身條 证件号码 獅 m 2 511 、, 已婚 信贷记录 这郁分包含您的僧用卡、贷款和其他信贷记录 • 金额类数据均以人民币计算，賴有 信息概要 信用卡 购房贷款 其他贷款 账户数 12 1 6 未结清沫销户账户数 9 1 3 发生过逾期的账户数 2 0 发生过90天以上逾期的账户数 1 0 £ 4 为他人担保笔数 0 0 rt   S1 lit  &括个人住房贷款、个人商用 住两用）贷款和个人住房公积 /k 发生过逾期的信用卡曾经“未按 时还最低还款额卡账户和“透  k 支超过60天”账户 逾期记 多响对您的信用评价。 信用卡 发生过逾期的贷记卡账户明细如下： 1. 2012年7月17日中国银行辽宁省分行发放的贷记卡（人民币 近5^内有1个月处于逾期状态，没有发生过90天以上逾： 透支超过60天的准贷记卡账户明细如下 2. 2013年10月31日中国银行大连市分行发放的准贷记卡（人民币账户） 60天，其中6个月透支超过90天。 从未逾期过的贷记卡及透支未超过 I 2017年6月13日兴业银行发放的贷记卡（人爾^&)。截至2017年 2017年3月27日华夏银行发放的贷记卡（人民。截至 2017_f 2016年9月18日浦发银行信用卡中心发放的贷记卡（人民币账户) j 201伴9月2日中国光大银行股份有限公貝|：阳分行发放的货 99,401,己使用额度0。 201辟9月2日中国光大银行股讼#限公司沈阳分行发放的 用额度168,179。 I 52017年 11 月， 截至 20} 濟户明细: i 销户。最近5年内有7个月透支:超过 .槪 ^^^5,000,已使用额度17,(041。最 额度60,000,已使用额度56,816。 信用额度50,000,已使用额度17,473。 '2017 年11月，信用额度58,939,已使用额度57,169。 芒账户）。截至2017年11月,信用额度折合人民而 币账户）。截至2017年11月，信用额度100,000,已使 2014年6月3曰广发银行大连分佇营业部发放的贷记 M 2010年4月29日中国 g* 故银行辽宁省分行发放的贷 度0。 10.20〗0年4月29日/国|於|银彳^：宁省分行发放|^{5^ (人民币账户）。截至2017年11月，信用额度3,000,已使用额度0。  11.2017 年8月光狞股份有限公司沈发放的贷记卡（人民币账户），截至2017年10月已销户。 12. 2011年5月18日每胃_^银行上海市分行^卡（人民币账户），截至2016年6月已销户。 购房一 的账户明细如下： 账户）。截至2017年11月，信用额度30,000，已使用额度9,951。 元账户）。截至2017年11月,信用额度折合人民币2,822,已使用额 中国工商银行丹东市分行发放的520,000元（人民币）个人住房公积金贷款，2036年5月26日到期。截至2017年 第 I 贞 ft 丨贞";
        String temp;
//        String temp = XdocUtil.substringFuzzyBetween(text, new SimilarityKeyword("报告编号:", 60), new SimilarityKeyword(" ", 100));
//        System.out.println("报告编号:" + temp);
//        temp = XdocUtil.substringFuzzyBetween(text, new SimilarityKeyword("查询时间:", 60), new SimilarityKeyword(" ", 100));
//        System.out.println("查询时间:" + temp);
//        temp = XdocUtil.substringFuzzyAfter(text, new SimilarityKeyword("查询时间", 66));
//        temp = XdocUtil.substringFuzzyBetween(temp, new SimilarityKeyword("报告时间:", 60), new SimilarityKeyword(" ", 100));
//        System.out.println("报告时间:" + temp);
//        temp = XdocUtil.substringFuzzyBetween(text, new SimilarityKeyword("姓名:", 60), new SimilarityKeyword(" ", 100));
//        System.out.println("姓名:" + temp);
//        temp = XdocUtil.substringFuzzyBetween(text, new SimilarityKeyword("类型:", 60), new SimilarityKeyword(" ", 100));
//        System.out.println("证件类型:" + temp);
        temp = XdocUtil.substringFuzzyAfter(text, new SimilarityKeyword("证件号码", 66));
        temp = XdocUtil.substringFuzzyBetween(temp, new SimilarityKeyword(":", 100), new SimilarityKeyword(" ", 100));
        System.out.println("证件号码:" + temp);
    }
}
