package org.colosseumer.java.utils;

import lombok.extern.apachecommons.CommonsLog;
import org.colosseumer.java.fileupload.utils.UploadSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * @Description: 定时任务 ，fixedDelay 执行完，间隔一定时间后执行。fixedRate不管是否执行完，每隔一定时间执行。
 * @Author: zhaoyu
 * @Date: 2019/11/26
 */
@CommonsLog
@Component
public class Jobs {

    @Autowired
    private UploadSupport uploadSupport;

    public final static long ONE_Minute =  60 * 1000;

    @Scheduled(fixedDelay=2*ONE_Minute)
    public void fixedDelayJob(){
        log.info(DateUtil.dateTimeNow()+" >>清除临时文件job开始....");
        uploadSupport.cleanTempFile(LocalDate.of(2019,11,26));
        log.info(DateUtil.dateTimeNow()+" >>清除临时文件job结束....");
    }

    /*@Scheduled(fixedRate=ONE_Minute)
    public void fixedRateJob(){
        System.out.println(DateUtil.dateTimeNow()+" >>fixedRate执行....");
    }*/

    /*@Scheduled(cron="0 15 3 * * ?")
    public void cronJob(){
        System.out.println(DateUtil.dateTimeNow()+" >>cron执行....");
    }*/
}
