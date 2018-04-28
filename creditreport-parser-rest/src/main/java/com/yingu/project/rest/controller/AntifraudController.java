package com.yingu.project.rest.controller;

import cn.cecr.econtract.web.app.ws.CreditDTO;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import com.yingu.project.api.*;
import com.yingu.project.api.NoneBankAntifraud.BlackListParam;
import com.yingu.project.persistence.mongo.entity.Customer;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.AnalyseFailRecord;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.PersonCredit;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.PersonCreditBase;
import com.yingu.project.persistence.mongo.entity.abridged.AbridgedCredit;
import com.yingu.project.persistence.mysql.entity.CourseEntity;
import com.yingu.project.persistence.mysql.entity.StudentEntity;
import com.yingu.project.service.service.*;
import com.yingu.project.util.config.ApiConfig;
import com.yingu.project.util.config.OssConfig;
import com.yingu.project.util.utils.OssUtil;
import com.yingu.project.util.utils.PoiUtil;
import com.yingu.project.util.utils.StringUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xml.security.utils.resolver.implementations.ResolverLocalFilesystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Created by MMM on 2018/01/29.
 * 反欺诈rest接口
 */
@RestController
@RequestMapping(value="/creditreport-parser-api")
@Slf4j
public class AntifraudController {
    @Autowired
    StudentService studentService;
    @Autowired
    CourseService courseService;
    @Autowired
    CustomerService customerService;
    @Autowired
    PersonCreditService personCreditService;
    @Autowired
    AbridgedCreditService abridgedCreditService;
    @Autowired
    AnalyseFailRecordService analyseFailRecordService;
    @Autowired
    ApiConfig apiConfig;
    @Autowired
    OssConfig ossConfig;


    //@ApiOperation(value="mongo查询接口",notes="返回结果,SUCCESS:100,FAILED:200",httpMethod = "POST")
    //@RequestMapping(value = "/getBlankListInfo",method = RequestMethod.POST)
    public ResponseResult<Customer> getBlankListInfo(@RequestBody BlackListParam blackListParam) throws Exception{
        ResponseResult<Customer> result = new ResponseResult<>();
        try {
            if(blackListParam.getIntoinfono()==null||blackListParam.getIntoinfono()==""){
                result.setStatus(Status.FAILED);
                result.setMessage("参数进件编号不能为空");
                return result;
            }
            long t1 = System.currentTimeMillis();
            List<Customer> customerList= customerService.findAllByIntoInfoNo(blackListParam.getIntoinfono());
            result.setStatus(Status.SUCCESS);
            result.setData(customerList);
            result.setMessage("查询成功");
            long t2 = System.currentTimeMillis();
            log.info("creditreport-parser-api.getBlankListInfo is success,excute time is:"+(t2-t1)+"millis"
                    +",param is:"+ JSON.toJSONString(blackListParam));
            return result;
        }
        catch (Exception ex){
            log.error(ex.getMessage(),ex);
            result.setStatus(Status.FAILED);
            result.setMessage("数据获取异常,请重试");
            return result;
        }
    }

    //@ApiOperation(value="jpa插入学生表",notes="返回结果,SUCCESS:100,FAILED:200",httpMethod = "POST")
    //@RequestMapping(value = "/addStudent",method = RequestMethod.POST)
    public ResponseResult<CreditDTO> addStudent(@RequestBody StudentEntity studentEntity) throws Exception {
        ResponseResult<CreditDTO> result = new ResponseResult<>();
        try {
            studentEntity.setName(studentEntity.getName()+"jpa");
            studentService.add(studentEntity);
            result.setStatus(Status.SUCCESS);
            result.setMessage("查询成功");
            return result;
        }
        catch (Exception ex){
            log.error(ex.getMessage(),ex);
            result.setStatus(Status.FAILED);
            result.setMessage("执行异常,请重试");
            return result;
        }

    }
    //@ApiOperation(value="mybatis插入学生表",notes="返回结果,SUCCESS:100,FAILED:200",httpMethod = "POST")
    //@RequestMapping(value = "/insertStudent",method = RequestMethod.POST)
    public ResponseResult<CreditDTO> insertStudent(@RequestBody StudentEntity studentEntity) throws Exception {
        ResponseResult<CreditDTO> result = new ResponseResult<>();
        try {

            studentEntity.setName(studentEntity.getName()+"mybatis");
            //mybatis
            studentService.insert(studentEntity);

            result.setStatus(Status.SUCCESS);
            result.setMessage("查询成功");
            return result;
        }
        catch (Exception ex){
            log.error(ex.getMessage(),ex);
            result.setStatus(Status.FAILED);
            result.setMessage("执行异常,请重试");
            return result;
        }

    }
    //@ApiOperation(value="mybatis分页查询课程表",notes="返回结果,SUCCESS:100,FAILED:200",httpMethod = "POST")
    //@RequestMapping(value = "/selectCourcePager",method = RequestMethod.POST)
    public ResponsePagerResult<CourseEntity> selectCourcePager(@RequestBody CourcePagerParam courcePagerParam) throws Exception {
        ResponsePagerResult<CourseEntity> result = new ResponsePagerResult<>();
        try {

            PageInfo<Map> pageInfo= courseService.selectPager(courcePagerParam.getPageIndex(),courcePagerParam.getPageSize());
            result.setRowCount(pageInfo.getTotal());
            result.setData(pageInfo.getList());
            result.setStatus(Status.SUCCESS);
            result.setMessage("查询成功");
            return result;
        }
        catch (Exception ex){
            log.error(ex.getMessage(),ex);
            result.setStatus(Status.FAILED);
            result.setMessage("执行异常,请重试");
            return result;
        }

    }

    @ApiOperation(value="解析个人信用word",notes="返回结果,SUCCESS:100,FAILED:200",httpMethod = "POST")
    @RequestMapping(value = "/analyseWord",method = RequestMethod.POST)
    public ResponseResult<CreditDTO> analyseWord(@RequestBody String wordName) throws Exception {
        ResponseResult<CreditDTO> result = new ResponseResult<>();
        try {
            String str1= "出生日期";
            String str2="出生曰期";
//           String str1="ABCBDAB";
//           String str2="BDCABA";
            boolean isSame= StringUtil.isSimilar(str1,str2,7);
            //SimilarInfo similarInfo= StringUtil.getSimilarInfo(str1,str2,6,2);
            //return result;
            //方法体
            File file = new File("D:\\word\\"+wordName);
            String str = "";
            try {
                FileInputStream fis = new FileInputStream(file);
                XWPFDocument xdoc = new XWPFDocument(fis);
                //读取段落,不包含表格
                List<XWPFParagraph> paras = xdoc.getParagraphs();
                for (XWPFParagraph para : paras) {
                    //System.out.println(para.getText());
                    log.info(StringUtil.trim(para.getText()));
                }
                //获取文档中所有的表格
                List<XWPFTable> tables = xdoc.getTables();
                List<XWPFTableRow> rows;
                List<XWPFTableCell> cells;
                XWPFTableRow row;
                XWPFTableCell cell;
                for (XWPFTable table : tables) {
                    rows = table.getRows();
                    for(int i=0;i<rows.size();i++){
                        row=rows.get(i);
                        cells = row.getTableCells();
                        for(int j=0;j<cells.size();j++){
                            cell=cells.get(j);
                            String cellValue=cell.getText();
                            log.info(cellValue);
                            if(cellValue.equals("被査询者证件号码")){
                                //读取下一行的同列单元格内容
                                log.info("被査询者证件号码:"+rows.get(i+1).getCell(j).getText());

                            }
                        }

                    }
                }
                fis.close();
            } catch (Exception e) {
                log.error(e.getMessage(),e);
            }


            result.setStatus(Status.SUCCESS);
            result.setMessage("查询成功");
            return result;
        }
        catch (Exception ex){
            log.error(ex.getMessage(),ex);
            result.setStatus(Status.FAILED);
            result.setMessage("执行异常,请重试");
            return result;
        }

    }
    @ApiOperation(value="解析个人信用word,返回解析实体",notes="返回结果,SUCCESS:100,FAILED:200",httpMethod = "POST")
    @RequestMapping(value = "/analyseCreditWord",method = RequestMethod.POST)
    public ResponseOneResult<PersonCredit> analyseCreditWord(@RequestBody String wordName) throws Exception {
        ResponseOneResult<PersonCredit> result = new ResponseOneResult<>();
        String filePath= apiConfig.getCreditword_path()+wordName;
        return personCreditService.getPersonCredit(filePath,wordName);
    }
    @ApiOperation(value="根据证件编号查询信报列表",notes="返回结果,SUCCESS:100,FAILED:200",httpMethod = "POST")
    @RequestMapping(value = "/findPersonCreditListByIdCard",method = RequestMethod.POST)
    public ResponseResult<PersonCreditBase> findPersonCreditListByIdCard(@RequestParam String idCard) throws Exception {
        ResponseResult<PersonCreditBase> result = new ResponseResult<>();
        try{
            List<PersonCreditBase> personCreditBaseList=new ArrayList<>();
            List<PersonCredit> personCreditList=personCreditService.findAllByIdcard(idCard);
            for(PersonCredit personCredit:personCreditList){
                personCreditBaseList.add(personCredit);
            }
            List<AbridgedCredit> abridgedCreditList=abridgedCreditService.findAllByIdcard(idCard);
            for(AbridgedCredit abridgedCredit:abridgedCreditList){
                personCreditBaseList.add(abridgedCredit);
            }
            result.setData(personCreditBaseList);
            result.setStatus(Status.SUCCESS);
            result.setMessage("查询成功");
            return result;
        }
        catch (Exception ex){
            log.error(ex.getMessage(),ex);
            result.setStatus(Status.FAILED);
            result.setMessage("数据获取异常,请重试");
            return result;
        }
    }
    @ApiOperation(value="根据id和信报类型(详版,简版)查询信报",notes="返回结果,SUCCESS:100,FAILED:200",httpMethod = "POST")
    @RequestMapping(value = "/findPersonCreditById",method = RequestMethod.POST)
    public ResponseOneResult<PersonCreditBase> findPersonCreditById(@RequestParam String id,@RequestParam String wordType) throws Exception {
        ResponseOneResult<PersonCreditBase> result = new ResponseOneResult<>();
        try{
            PersonCreditBase personCreditBase=new PersonCreditBase();
            if(wordType.equals("详版")){
                personCreditBase=personCreditService.findFirstById(id);
            }
            else if(wordType.equals("简版")){
                personCreditBase=abridgedCreditService.findFirstById(id);
            }
            else{
                personCreditBase=personCreditService.findFirstById(id);
            }
            result.setData(personCreditBase);
            result.setStatus(Status.SUCCESS);
            result.setMessage("查询成功");
            return result;
        }
        catch (Exception ex){
            log.error(ex.getMessage(),ex);
            result.setStatus(Status.FAILED);
            result.setMessage("数据获取异常,请重试");
            return result;
        }
    }
    @ApiOperation(value="根据报告文件名查询异常报告",notes="返回结果,SUCCESS:100,FAILED:200",httpMethod = "POST")
    @RequestMapping(value = "/findByWordNameOrderByCreatetimeAsc",method = RequestMethod.POST)
    public ResponseResult<AnalyseFailRecord> findByWordNameOrderByCreatetimeAsc(@RequestParam String wordName) throws Exception {
        ResponseResult<AnalyseFailRecord> result = new ResponseResult<>();
        try{
            List<AnalyseFailRecord> analyseFailRecordList=analyseFailRecordService.findByWordNameOrderByCreatetimeAsc(wordName);
            result.setData(analyseFailRecordList);
            result.setStatus(Status.SUCCESS);
            result.setMessage("查询成功");
            return result;
        }
        catch (Exception ex){
            log.error(ex.getMessage(),ex);
            result.setStatus(Status.FAILED);
            result.setMessage("数据获取异常,请重试");
            return result;
        }
    }
    @ApiOperation(value="相似度匹配算法测试",notes="返回结果,SUCCESS:100,FAILED:200",httpMethod = "POST")
    @RequestMapping(value = "/similar",method = RequestMethod.POST)
    public SimilarInfo similar(@RequestParam String strSource,
                               @RequestParam String strTarget,
                               @RequestParam Integer level,
                               @RequestParam Integer matchRangeIndex) throws Exception {
    try {
        String str1 = "查询时间：2016.01.0810:41:22证件类型：身份证报告编号：2016010800002567196125姓名：秦艳刚报告时间：2016.01.0810:41:22证件号码：231004197303281219己婚";
        String str2 = "证件类型";
        if (!StringUtil.isEmpty(strSource)) {
            str1 = strSource;
        }
        if (!StringUtil.isEmpty(strTarget)) {
            str2 = strTarget;
        }
//           String str1="ABCBDAB";
//           String str2="BDCABA";
        //boolean isSame= StringUtil.isSimilar(str1,str2,7);
        SimilarInfo similarInfo = StringUtil.getSimilarInfo(str1, str2, level, matchRangeIndex);
        SimilarInfo similarInfo2 = StringUtil.getSimilarDetailsInfo(str1, str2, level, matchRangeIndex);
        return similarInfo2;
    }
    catch (Exception ex){
        return null;
    }

    }
}
