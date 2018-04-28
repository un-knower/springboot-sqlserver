package com.yingu.project.rest.app;

import com.yingu.project.api.ResponseOneResult;
import com.yingu.project.api.Status;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.PersonCredit;
import com.yingu.project.rest.config.RequestLog;
import com.yingu.project.service.service.PersonCreditService;
import com.yingu.project.util.config.ApiConfig;
import com.yingu.project.util.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.PostConstruct;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@ComponentScan(value = {"com.yingu.project"})
@MapperScan("com.yingu.project.persistence.mysql.mapper")

@EnableEurekaClient
@EnableFeignClients(basePackages = {"com.yingu.project.rest.rpc"})

@Slf4j
public class Application extends WebMvcConfigurerAdapter {
    private static Application application;
    @Autowired
    ApiConfig apiConfig;
    @Autowired
    PersonCreditService personCreditService;

    @PostConstruct
    public void init() {
        application = this;
        File file = new File(apiConfig.getCreditword_path());
        if (!file.exists()) {
            file.mkdirs();
        }
        File fileSucess = new File(apiConfig.getCreditword_sucess_path());
        if (!fileSucess.exists()) {
            fileSucess.mkdirs();
        }
        File fileFail = new File(apiConfig.getCreditword_fail_path());
        if (!fileFail.exists()) {
            fileFail.mkdirs();
        }

        File pdfSource = new File(apiConfig.getCreditpdf_source());
        if (!pdfSource.exists()) {
            pdfSource.mkdirs();
        }
        File pdfSuccess = new File(apiConfig.getCreditpdf_success());
        if (!pdfSuccess.exists()) {
            pdfSuccess.mkdirs();
        }
        File pdfFail = new File(apiConfig.getCreditpdf_fail());
        if (!pdfFail.exists()) {
            pdfFail.mkdirs();
        }
        log.info("analyse application start...");
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        try {
            List<String> list = new ArrayList<>();

            while (true) {
                //扫描word文件夹,解析其中word
                File file = new File(application.apiConfig.getCreditword_path());
                list.clear();
                //todo: 开发阶段暂时关闭扫描文件夹
                if (application.apiConfig.getOpen_analyse_word()) {
                    analyseFiles(file, list);
                }
                if (list.size() == 0) {

                    Thread.sleep(application.apiConfig.getScan_delay_time());
                    log.info(MessageFormat.format("there is no word,folder is {0}", application.apiConfig.getCreditword_path()));

                }
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }

    }

    private static void analyseFiles(File file, List<String> list) {
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            for (File fileTemp : files) {
                if (fileTemp.isDirectory()) {
                    analyseFiles(fileTemp, list);
                } else {
                    String filePath = fileTemp.getPath();
                    String wordName = fileTemp.getName();
                    list.add(filePath);
                    long startTime = System.currentTimeMillis();
                    ResponseOneResult<PersonCredit> responseOneResult = application.personCreditService.getPersonCredit(filePath, wordName);
                    long endTime = System.currentTimeMillis();
                    long executeTime = endTime - startTime;
                    log.info(MessageFormat.format("the word of {0} analysed run time:{1}ms", wordName, executeTime));
                    Boolean moveSucess = true;
                    if (responseOneResult.getStatus() == Status.SUCCESS) {
                        moveSucess = FileUtil.fileMoveTo(fileTemp, application.apiConfig.getCreditword_sucess_path(), wordName);
                    } else {
                        moveSucess = FileUtil.fileMoveTo(fileTemp, application.apiConfig.getCreditword_fail_path(), wordName);
                    }
                    log.info(MessageFormat.format("the word of {0} move:{1}", wordName, moveSucess));
                }
            }
        }
    }

    // 增加拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RequestLog());
    }


}
