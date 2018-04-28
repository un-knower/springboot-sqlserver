package com.yingu.project.service.service.simple.impl;

import com.alibaba.fastjson.JSON;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.AnalyseFailRecord;
import com.yingu.project.persistence.mongo.entity.abridged.AbridgedCredit;
import com.yingu.project.persistence.mongo.entity.abridged.credit.creditcard.CreditCard;
import com.yingu.project.persistence.mongo.repository.AbridgedCreditRepository;
import com.yingu.project.persistence.mongo.repository.AnalyseFailRecordRepository;
import com.yingu.project.persistence.mysql.entity.XDOrder_Table;
import com.yingu.project.persistence.mysql.mapper.XDOrder_TableMapper;
import com.yingu.project.service.service.simple.ExtractInfoService;
import com.yingu.project.service.service.simple.ExtractInfoSimilarityService;
import com.yingu.project.service.service.simple.WordService;
import com.yingu.project.persistence.mongo.entity.abridged.BaseInfo;
import com.yingu.project.persistence.mongo.entity.abridged.CreditDetail;
import com.yingu.project.persistence.mongo.entity.abridged.InquiryInfo;
import com.yingu.project.persistence.mongo.entity.abridged.PublicRecord;
import com.yingu.project.service.service.simple.util.SpringValidator;
import com.yingu.project.service.service.simple.cache.ThreadCache;
import com.yingu.project.service.service.simple.xdoc.FailInfo;
import com.yingu.project.service.service.simple.xdoc.XdocService;
import com.yingu.project.util.config.ApiConfig;
import com.yingu.project.util.utils.DateUtil;
import com.yingu.project.util.utils.StringUtil;
import io.swagger.annotations.Api;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 调用正常解析
 * 判断字段是否有缺失
 * 有缺失则调用相似匹配取值逻辑
 * 合并结果
 *
 * @Date: Created in 2018/3/29 17:14
 * @Author: wm
 */
@Service
@Slf4j
public class WordServiceImpl implements WordService {

    @Autowired
    ExtractInfoService extractInfoService;
    @Autowired
    ExtractInfoSimilarityService extractInfoSimilarityService;
    @Autowired
    AbridgedCreditRepository abridgedCreditRepository;
    @Autowired
    AnalyseFailRecordRepository analyseFailRecordRepository;
    @Autowired
    XdocService xdocService;
    @Autowired
    SpringValidator springValidator;
    @Autowired
    ThreadCache threadCache;

    @Autowired
    XDOrder_TableMapper xdOrder_tableMapper;
    @Autowired
    ApiConfig apiConfig;

    @Override
    public AbridgedCredit analysisAbridged(XWPFDocument xdoc, String wordName, String ossFileName) {
        log.info(MessageFormat.format("analysisAbridged start, wordName={0}", wordName));
        String failMessage = null;
        AbridgedCredit abridgedCredit = new AbridgedCredit();
        //设置附加属性
        abridgedCredit.setWordName(wordName);
        abridgedCredit.setOosFileName(ossFileName);
        abridgedCredit.setCreatetime(DateUtil.getDate());
        abridgedCredit.setWordType("简版");
        try {
            //解析对象
            //  报告头
            BaseInfo baseInfo = this.getBaseInfo(xdoc);
//            log.info("====BaseInfo\n" + JSON.toJSONString(baseInfo));
            //若报告编号重复则跳过
            if (null != baseInfo && null != baseInfo.getContractNo()) {
                Long count = this.abridgedCreditRepository.countByReportNo(baseInfo.getContractNo());
                if (count != null && count > 0) {
                    if(!apiConfig.getSave_repeat_report()) {
                        return null;
                    }
                }
            }

            //  信贷记录
            CreditDetail creditDetail = this.getCreditDetail(xdoc);
//            log.info("====CreditDetail\n" + JSON.toJSONString(creditDetail));
            //  公共记录
            PublicRecord publicRecord = this.getPublicRecord(xdoc);
//            log.info("====PublicRecord\n" + JSON.toJSONString(publicRecord));
            //  查询记录明细
            InquiryInfo inquiryInfo = this.getInquiryInfo(xdoc);
//            log.info("====InquiryInfo\n" + JSON.toJSONString(inquiryInfo));

            log.info(MessageFormat.format("analysisAbridged complete, wordName={0}", wordName));

            //设置提取属性
            abridgedCredit.setBaseInfo(baseInfo);
            abridgedCredit.setCreditDetail(creditDetail);
            abridgedCredit.setPublicRecord(publicRecord);
            abridgedCredit.setInquiryInfo(inquiryInfo);

            //设置公共信息
            if (null != baseInfo) {
                /* 报告编号(公共字段) */
                abridgedCredit.setReportNo(baseInfo.getContractNo());
                /* 查询请求时间(公共字段) */
                abridgedCredit.setStrRequestDateTime(baseInfo.getInquiryDateTimeStr());
                abridgedCredit.setRequestDateTime(baseInfo.getInquiryDateTime());
                /* 报告时间(公共字段) */
                abridgedCredit.setStrGenerateDateTime(baseInfo.getGenerationDateTimeStr());
                abridgedCredit.setGenerateDateTime(baseInfo.getGenerationDateTime());
                /* 被查询者姓名(公共字段) */
                abridgedCredit.setName(baseInfo.getClientName());
                /* 被查询者证件类型(公共字段) */
                abridgedCredit.setCardType(baseInfo.getCardType());
                /* 被查询者证件号码(公共字段) */
                abridgedCredit.setIdcard(baseInfo.getCardNo());
            }

            //从金管中修复身份证
            String orderNum=wordName.replace(".docx","");
            XDOrder_Table xdOrder_table= xdOrder_tableMapper.selectByOrderNum(orderNum);
            if(xdOrder_table!=null&&xdOrder_table.getUsecard()!=null){
                abridgedCredit.setJgidcard(xdOrder_table.getUsecard());
            }

            log.info(MessageFormat.format("analysis abridged final data {0} {1} {2}\n{3}", abridgedCredit.getWordType(), abridgedCredit.getWordName(), abridgedCredit.getOosFileName(), JSON.toJSONString(abridgedCredit)));

            StringBuilder failSB = new StringBuilder();
            //验证解析结果 必须有的字段，若解析失败，则记录失败的类和字段
            List<FailInfo> failInfoList = new ArrayList<>();
            Set<ConstraintViolation> validErrorSet = springValidator.validate(abridgedCredit);
            for (ConstraintViolation constraintViolation : validErrorSet) {
                String clazz = constraintViolation.getRootBeanClass().getName();
                clazz = "validate." + clazz.substring(clazz.lastIndexOf("."));
                String property = constraintViolation.getPropertyPath().toString();
                Object value = constraintViolation.getInvalidValue();
                String failMs = constraintViolation.getMessage();
                failInfoList.add(new FailInfo(clazz, property, value, failMs));
            }
            String failMs = this.parseMs(failInfoList);
            if (!StringUtil.isEmpty(failMs)){
                failSB.append(failMs);
            }
            // 可空字段需在业务特定条件判断是否成功解析返回自定义failMessage
            String otherFailMs = this.parseMs(this.threadCache.getFailInfoList());
            if (!StringUtil.isEmpty(otherFailMs)){
                failSB.append(otherFailMs);
            }
            //特殊判断，信用卡不能为null
            if (abridgedCredit.getCreditDetail() != null) {
                if (abridgedCredit.getCreditDetail().getCreditCard() == null) {
                    failSB.append("没有信用卡");
                } else {
                    CreditCard creditCard = abridgedCredit.getCreditDetail().getCreditCard();
                    if (CollectionUtils.isEmpty(creditCard.getCreditCardOverdueDetailsList())
                            && CollectionUtils.isEmpty(creditCard.getOverdraft60DaysDetailsList())
                            && CollectionUtils.isEmpty(creditCard.getUnOverdueAndUnOverdraft60DaysDetailsList())) {
                        failSB.append("没有信用卡");
                    }
                }
            }else{
                failSB.append("没有信用卡");
            }
            failMessage = failSB.toString();
        } catch (Exception e) {
            e.printStackTrace();
            failMessage = this.parseMs(e);
        }


        try {
            //存mongo
            String idcard = abridgedCredit.getIdcard();
            //身份证为空则不保存
            if (!StringUtil.isEmpty(idcard)) {
                log.info(MessageFormat.format("save start {0}", wordName));
                abridgedCredit = abridgedCreditRepository.save(abridgedCredit);
                if (abridgedCredit != null && abridgedCredit.getId() != null) {
                    log.info(MessageFormat.format("AbridgedCredit the word of {0} has been analysed sucess, id={1}", wordName, abridgedCredit.getId()));
                }
            } else {
                if (StringUtil.isEmpty(failMessage)) {
                    failMessage = "analyse idcard is null";
                } else {
                    failMessage = failMessage.concat("\nanalyse idcard is null");
                }
            }
        } catch (Exception e) {
            String errorMs = "save failed.\n" + this.parseMs(e);
            if (StringUtil.isEmpty(failMessage)) {
                failMessage = errorMs;
            } else {
                failMessage = failMessage.concat(errorMs);
            }
        }

        //记录失败的解析文件信息
        if (!StringUtil.isEmpty(failMessage)) {
            AnalyseFailRecord analyseFailRecord = new AnalyseFailRecord();
            analyseFailRecord.setWordName(wordName);
            analyseFailRecord.setCreatetime(DateUtil.getDate());
            analyseFailRecord.setFailMessage("AbridgedCredit ".concat(failMessage));
            analyseFailRecord.setOosFileName(ossFileName);
            analyseFailRecordRepository.save(analyseFailRecord);
            log.error(MessageFormat.format("AbridgedCredit the word of {0} has been analysed failed,message:{1}", wordName, failMessage));
        }

        return abridgedCredit;
    }

    private String parseMs(Exception e) {
        if (null == e) {
            return null;
        }
        StringBuilder errorMs = new StringBuilder();
        if (e.getMessage()==null){
            errorMs.append(e.getClass().getName());
        }else{
            errorMs.append(e.getMessage());
        }
        Throwable tempEx = e;
        for (int i = 0; i < 10; i++) {
            if (tempEx.getCause() == null) {
                break;
            } else {
                tempEx = tempEx.getCause();
                errorMs.append("\n").append(tempEx.getMessage());
            }
        }
        return errorMs.toString();
    }

    private String parseMs(List<FailInfo> failInfoList) {
        if (null==failInfoList){
            return null;
        }
        StringBuilder validFailMs = new StringBuilder();
        for (FailInfo failInfo : failInfoList) {
            validFailMs.append(failInfo.getClazz()).append(".").append(failInfo.getProperty()).append("=").append(failInfo.getValue()).append(". ").append(failInfo.getFailMessage()).append("\n");
        }
        return validFailMs.toString();
    }

//    /**
//     * 结果对象转换为PersonCredit
//     *
//     * @param baseInfo
//     * @param creditDetail
//     * @param inquiryInfo
//     * @return
//     */
//    private PersonCredit convert(BaseInfo baseInfo, CreditDetail creditDetail, InquiryInfo inquiryInfo) {
//        PersonCredit personCredit = new PersonCredit();
//        if (null != baseInfo) {
//            //常用查询信息
//            personCredit.setReportNo(baseInfo.getContractNo());
//            personCredit.setStrRequestDateTime(baseInfo.getInquiryDateTimeStr());
//            personCredit.setRequestDateTime(baseInfo.getInquiryDateTime());
//            personCredit.setStrGenerateDateTime(baseInfo.getGenerationDateTimeStr());
//            personCredit.setGenerateDateTime(baseInfo.getGenerationDateTime());
//            personCredit.setName(baseInfo.getClientName());
//            personCredit.setCardType(baseInfo.getCardType());
//            personCredit.setIdcard(baseInfo.getCardNo());
//            //基本信息
//            com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.BaseInfo info = new com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.BaseInfo();
//            info.setName(baseInfo.getClientName());
//            info.setCardType(baseInfo.getCardType());
//            info.setIdcard(baseInfo.getCardNo());
//            personCredit.setBaseInfo(info);
//            //身份信息
//            IdentityInfo identityInfo = new IdentityInfo();
//            identityInfo.setMaritalStatus(baseInfo.getIsMarried());
//            //【需解析
////            identityInfo.setGender();
////            identityInfo.setBirthDay();
////            identityInfo.setStrBirthDay();
//        }
//
//        if (creditDetail != null) {
//            //信用提示
//            if (creditDetail.getSummaryInfo() != null) {
//                SummaryInfo summaryInfo = creditDetail.getSummaryInfo();
//                CreditHint creditHint = new CreditHint();
//
//                ////简版补充字段 start
//                /** 资产处置信息汇总 */
//                AssetDisposeInfo assetDisposeInfo = new AssetDisposeInfo();
//                assetDisposeInfo.setAssetDisposeNum(summaryInfo.getAssetDisposeNum());
//                personCredit.setAssetDisposeInfo(assetDisposeInfo);
//                /** 保证人代偿信息汇总 */
//                AssetCompensatoryInfo assetCompensatoryInfo = new AssetCompensatoryInfo();
//                assetCompensatoryInfo.setAssetCompensatoryNum(summaryInfo.getAssetCompensatoryNum());
//                personCredit.setAssetCompensatoryInfo(assetCompensatoryInfo);
//                /** 担保信息汇总 */
//                GuaranteeInfo guaranteeInfo = new GuaranteeInfo();
//                guaranteeInfo.setCreditCardGuaranteeNum(summaryInfo.getCreditCardGuaranteeNum());
//                guaranteeInfo.setHouseLoanGuaranteeNum(summaryInfo.getHouseLoanGuaranteeNum());
//                guaranteeInfo.setOtherLoanGuaranteeNum(summaryInfo.getOtherLoanGuaranteeNum());
////                personCredit.setGuaranteeInfo(guaranteeInfo);
////
////                //【数据冗余 需确定存储标准 start 银行版是按类别单独对象存储汇总信息
////                /**信用卡 账户数（包括：贷记卡、准贷记卡） */
////                creditHint.setCreditCardAccountNum(summaryInfo.getCreditCardAccountNum());
////                /**购房贷款 账户数（包括：个人住房贷款、商用房贷款） */
////                creditHint.setHouseLoanAccountNum(summaryInfo.getHouseLoanAccountNum());
////
////                /** 信用卡 未结清/未销户账户数 （包括：贷记卡、准贷记卡）*/
////                creditHint.setCreditCardUnclearedNum(summaryInfo.getCreditCardUnclearedNum());
////                /** 购房贷款  未结清/未销户账户数（包括：个人住房贷款、商用房贷款） */
////                creditHint.setHouseLoanUnclearedNum(summaryInfo.getHouseLoanUnclearedNum());
////                /** 其他贷款  未结清/未销户账户数 */
////                creditHint.setOtherLoanUnclearedNum(summaryInfo.getOtherLoanUnclearedNum());
////
////                /** 信用卡 发生过逾期的账户数（包括：贷记卡、准贷记卡） */
////                creditHint.setCreditCardOverdueNum(summaryInfo.getCreditCardOverdueNum());
////                /** 购房贷款  发生过逾期的账户数（包括：个人住房贷款、商用房贷款） */
////                creditHint.setHouseLoanOverdueNum(summaryInfo.getHouseLoanOverdueNum());
////                /** 其他贷款  发生过逾期的账户数 */
////                creditHint.setOtherLoanOverdueNum(summaryInfo.getOtherLoanOverdueNum());
////
////                /** 信用卡 发生过逾期90天以上的账户数（包括：贷记卡、准贷记卡） */
////                creditHint.setCreditCardOverdue90DayNum(summaryInfo.getCreditCardOverdue90DayNum());
////                /** 购房贷款  发生过逾期90天以上的账户数（包括：个人住房贷款、商用房贷款） */
////                creditHint.setHouseLoanOverdue90DayNum(summaryInfo.getHouseLoanOverdue90DayNum());
////                /** 其他贷款  发生过逾期90天以上的账户数 */
////                creditHint.setOtherLoanOverdue90DayNum(summaryInfo.getOtherLoanOverdue90DayNum());
////                //【数据冗余 需确定存储标准 end
////                ////简版补充字段 end
//
//
//                /* 住房贷款笔数  【需统计 拆分个人住房和商用房*/
//                String houseLoanCount;
//                Integer iHouseLoanCount;
//                /* 商用(包括商住两用)房贷款笔数  【需统计 拆分个人住房和商用房*/
//                String businessHouseLoanCount;
//                Integer iBusinessHouseLoanCount;
//                //其他贷款
//                if (null != summaryInfo.getOtherLoanAccountNum()) {
//                    creditHint.setIOtherLoanCount(summaryInfo.getOtherLoanAccountNum());
//                    creditHint.setOtherLoanCount(summaryInfo.getOtherLoanAccountNum().toString());
//                }
//                //贷记卡账户数    【需统计    信贷记录拆分出贷记卡账户数
////                if (summaryInfo.getCreditCardAccountNum()!=null) {
////                    creditHint.setICreditCardCount(summaryInfo.getCreditCardAccountNum());
////                    creditHint.setCreditCardCount(summaryInfo.getCreditCardAccountNum().toString());
////                }
//                //首张贷记卡发卡月份 【需统计    信贷记录
//                //准贷记卡账户数   【需统计    信贷记录
//                //首张准贷记卡发卡月份    【需统计    信贷记录
//            }
//
//            //未结清贷款信息汇总 【需统计 汇总所有房贷和其他贷款
//            UnClearLoanInfo unClearLoanInfo = new UnClearLoanInfo();
//            /* 笔数 */
//            Integer loanCount = null;
//            if (null != creditDetail.getSummaryInfo()) {
//                SummaryInfo summaryInfo = creditDetail.getSummaryInfo();
//                loanCount = XdocUtil.intAdditive(summaryInfo.getHouseLoanUnclearedNum(), summaryInfo.getOtherLoanUnclearedNum());
//            }
//            //  loanCount若未null，则统计明细
//            unClearLoanInfo.setILoanCount(loanCount);
//            unClearLoanInfo.setLoanCount(XdocUtil.toString(loanCount));
//
//            //未销户贷记卡信息汇总    【需统计    信贷记录
//            UnCancelCreditCardInfo unCancelCreditCardInfo = new UnCancelCreditCardInfo();
//
//            //未销户准贷记卡信息汇总   【需统计    信贷记录
//            UnCancelPermitCreditCardInfo unCancelPermitCreditCardInfo;
//
//            //贷款逾期   【需统计    信贷记录
//            LoanOverdue loanOverdue;
//
//            //贷记卡逾期   【需统计    信贷记录
//            CreditCardOverdue creditCardOverdue;
//
//            //准贷记卡60天以上透支
//            PermitCreditCardOverdue permitCreditCardOverdue;
//
//            //对外担保信息汇总   【需统计    信贷记录
//            ForeignEnsure foreignEnsure;
//
//            //贷记卡   【需统计    信贷记录 汇总正常和非正常，并提取贷记卡
//            List<CreditCard> creditCardList = new ArrayList<>();
//            //准贷记卡   【需统计    信贷记录 汇总正常和非正常，并提取准贷记卡
//            List<SemiCreditCard> semiCreditCardList = new ArrayList<>();
//            //贷款   【需统计    信贷记录 汇总房贷和其他贷、正常和非正常
//            List<LoanInfo> loanInfoList = new ArrayList<>();
//
////            //保证人代偿信息
////            personCredit.setCompensatoryList(creditDetail.getCompensatoryList());
////            //为他人担保信息
////            personCredit.setWarrantList(creditDetail.getWarrantList());
//        }
//
//        //企业查询记录
//        if (null != inquiryInfo) {
//            List<InquiryRecord> inquiryRecordList = new ArrayList<>();
//            for (InquiryDetails inquiryDetails : inquiryInfo.getAuthInquiryDetailsList()) {
//                InquiryRecord inquiryRecord = new InquiryRecord();
//                inquiryRecord.setQueryDate(inquiryDetails.getDateStr());
//                inquiryRecord.setQueryOperator(inquiryDetails.getOperator());
//                inquiryRecord.setQueryCause(inquiryDetails.getCause());
//                inquiryRecordList.add(inquiryRecord);
//            }
//            personCredit.setInquiryRecordList(inquiryRecordList);
//        }
//
//        return personCredit;
//    }

    @SneakyThrows
    public BaseInfo getBaseInfo(XWPFDocument xdoc) {
//        //正常解析
//        BaseInfo baseInfo = new BaseInfo();
//        Map<String, Object> map = new HashMap<String, Object>();
//        String text = xdocService.getWordText(xdoc);
//        this.extractInfoService.getBaseInfo(text, map);
//        BeanUtils.populate(baseInfo, map);

        //判断是否有无法解析的字段


        //相似解析
        BaseInfo baseInfoSimilarity = this.extractInfoSimilarityService.getBaseInfo(xdoc);

        //合并字段结果；仍有null字段，则记录日志

        return baseInfoSimilarity;
    }

//    @Override
//    public SummaryInfo getCreditInfo(XWPFDocument xdoc) {
//        //正常解析
////        BaseInfo baseInfo = new BaseInfo();
////        Map<String, Object> map = new HashMap<String, Object>();
////        String text = xdocService.getWordText(xdoc);
////        this.extractInfoService.getSummaryInfo(text, map);
////        SummaryInfo summaryInfo = this.convertToSummaryInfo(map);
//
//        //判断是否有无法解析的字段
//
//        //相似解析
//        SummaryInfo summaryInfoSimilarity = this.extractInfoSimilarityService.getSummaryInfo(xdoc);
//
//        //合并字段结果；仍有null字段，则记录日志
//
//        return summaryInfoSimilarity;
//    }

    public CreditDetail getCreditDetail(XWPFDocument xdoc) {
        CreditDetail creditDetail = this.extractInfoSimilarityService.getCreditDetail(xdoc);
        return creditDetail;
    }

    public InquiryInfo getInquiryInfo(XWPFDocument xdoc) {
        //正常解析
//        BaseInfo baseInfo = new BaseInfo();
//        Map<String, Object> map = new HashMap<String, Object>();
//        String text = xdocService.getWordText(xdoc);
//        this.extractInfoService.getInquiryInfo(text, map);
//        InquiryInfo inquiryInfo = this.convertToInquiryInfo(map);

        //判断是否有无法解析的字段

        //相似解析
        InquiryInfo inquiryInfoSimilarity = this.extractInfoSimilarityService.getInquiryInfo(xdoc);

        //合并字段结果；仍有null字段，则记录日志

        return inquiryInfoSimilarity;
    }

    public PublicRecord getPublicRecord(XWPFDocument xdoc) {
        PublicRecord publicRecords = this.extractInfoSimilarityService.getPublicRecord(xdoc);
        return publicRecords;
    }

//    private SummaryInfo convertToSummaryInfo(Map<String, Object> map) {
//        SummaryInfo summaryInfo = new SummaryInfo();
//        String creditCardKey = "creditCard";
//        String houseLoanKey = "houseLoan";
//        String otherLoanKey = "otherLoan";
//        //账户数
//        Map totalAccount = (Map) map.get("totalAccount");
//        if (null != totalAccount) {
//            Object creditCard = totalAccount.get(creditCardKey);
//            Object houseLoan = totalAccount.get(houseLoanKey);
//            Object otherLoan = totalAccount.get(otherLoanKey);
//            summaryInfo.setCreditCardAccountNum(XdocUtil.getIntegerValue(creditCard));
//            summaryInfo.setHouseLoanAccountNum(XdocUtil.getIntegerValue(houseLoan));
//            summaryInfo.setOtherLoanAccountNum(XdocUtil.getIntegerValue(otherLoan));
//        }
//        //未结清/未销户账户数
//        Map activeAccount = (Map) map.get("activeAccount");
//        if (null != activeAccount) {
//            Object creditCard = activeAccount.get(creditCardKey);
//            Object houseLoan = activeAccount.get(houseLoanKey);
//            Object otherLoan = activeAccount.get(otherLoanKey);
//            summaryInfo.setCreditCardUnclearedNum(XdocUtil.getIntegerValue(creditCard));
//            summaryInfo.setHouseLoanUnclearedNum(XdocUtil.getIntegerValue(houseLoan));
//            summaryInfo.setOtherLoanUnclearedNum(XdocUtil.getIntegerValue(otherLoan));
//        }
//        //发生过逾期的账户数
//        Map overdueAccount = (Map) map.get("overdueAccount");
//        if (null != overdueAccount) {
//            Object creditCard = overdueAccount.get(creditCardKey);
//            Object houseLoan = overdueAccount.get(houseLoanKey);
//            Object otherLoan = overdueAccount.get(otherLoanKey);
//            summaryInfo.setCreditCardOverdueNum(XdocUtil.getIntegerValue(creditCard));
//            summaryInfo.setHouseLoanOverdueNum(XdocUtil.getIntegerValue(houseLoan));
//            summaryInfo.setOtherLoanOverdueNum(XdocUtil.getIntegerValue(otherLoan));
//        }
//        //发生过90天以上逾期的账户数
//        Map overdue90DaysAccount = (Map) map.get("overdue90DaysAccount");
//        if (null != overdue90DaysAccount) {
//            Object creditCard = overdue90DaysAccount.get(creditCardKey);
//            Object houseLoan = overdue90DaysAccount.get(houseLoanKey);
//            Object otherLoan = overdue90DaysAccount.get(otherLoanKey);
//            summaryInfo.setCreditCardOverdue90DayNum(XdocUtil.getIntegerValue(creditCard));
//            summaryInfo.setHouseLoanOverdue90DayNum(XdocUtil.getIntegerValue(houseLoan));
//            summaryInfo.setOtherLoanOverdue90DayNum(XdocUtil.getIntegerValue(otherLoan));
//        }
//        //为他人担保笔数
//        Map warrantAccount = (Map) map.get("warrantAccount");
//        if (null != warrantAccount) {
//            Object creditCard = warrantAccount.get(creditCardKey);
//            Object houseLoan = warrantAccount.get(houseLoanKey);
//            Object otherLoan = warrantAccount.get(otherLoanKey);
//            summaryInfo.setCreditCardGuaranteeNum(XdocUtil.getIntegerValue(creditCard));
//            summaryInfo.setHouseLoanGuaranteeNum(XdocUtil.getIntegerValue(houseLoan));
//            summaryInfo.setOtherLoanGuaranteeNum(XdocUtil.getIntegerValue(otherLoan));
//        }
//        return summaryInfo;
//    }
}
