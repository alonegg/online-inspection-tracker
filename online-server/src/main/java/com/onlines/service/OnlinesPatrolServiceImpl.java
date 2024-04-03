package com.onlines.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.onlines.entiry.CaseInfoDto;
import com.onlines.entiry.DataReqDto;
import com.onlines.entiry.OnlineSaleDto;
import com.onlines.mapper.OnlinesPatrolMapper;
import com.onlines.pojo.*;
import com.onlines.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OnlinesPatrolServiceImpl implements IOnlinesPatrolService {
    @Autowired
    private OnlinesPatrolMapper onlinesPatrolMapper;

    public void insertInfoTest(OnlinesPatrol onlinesPatrol) {
        onlinesPatrol.setCreatetime(new Date());
        onlinesPatrolMapper.insert(onlinesPatrol);
    }


    public PageInfo selectAll(OnlineSaleDto onlineSaleDto) {
        PageInfo pageInfo = null;
        PageHelper.startPage(onlineSaleDto.getPageNum(), onlineSaleDto.getPageSize());
        Page<OnlinesPatrol> patrolPageInfo = onlinesPatrolMapper.getOnlineList(onlineSaleDto);
        pageInfo = new PageInfo<>(patrolPageInfo);
        return pageInfo;
    }

    @Override
    public int updataCase(OnlinesPatrol onlinesPatrol) {
        return onlinesPatrolMapper.updateByPrimaryKeySelective(onlinesPatrol);
    }


    public int deleteById(int id) {
        return onlinesPatrolMapper.deleteByPrimaryKey(id);
    }

    public int resetDatum(int id){
        OnlinesPatrol onlinesPatrol= onlinesPatrolMapper.selectByPrimaryKey(id);
        onlinesPatrol.setDatumAddress(null);
        return onlinesPatrolMapper.updateByPrimaryKey(onlinesPatrol);
    }

    public PageInfo getCaseExecInfo(CaseInfoDto caseInfoDto) {
        OnlinesPatrolExample onlinesPatrolExample = null;
        PageInfo pageInfo = null;
        PageHelper.startPage(caseInfoDto.getPageNum(), caseInfoDto.getPageSize());
        System.out.println("分页参数====" + caseInfoDto.getPageNum() + "," + caseInfoDto.getPageSize());
        // 最多查询近30天
        Date endDate = DateUtil.getToday();
        Date beginDate =   DateUtil.getAfterDayDate(endDate, -30);

        Page<CaseInfo> res = onlinesPatrolMapper.getCaseInfo(caseInfoDto.getCaseId(), beginDate ,endDate );
        pageInfo = new PageInfo<>(res);
        System.out.println("查询结果====" + JSON.toJSONString(res));
        return pageInfo;
    }

    public OnlinesPatrol getCaseBaseInfo(CaseInfoDto caseInfoDto) {
        OnlinesPatrol res = onlinesPatrolMapper.getCaseBaseInfo(caseInfoDto.getCaseId());
        return res;
    }

    public PageInfo getFailedCaseInfo(Date beginDate, Date endDate, DataReqDto params) {
        System.out.println("开始日期" + beginDate.toString());
        System.out.println("结束日期" + endDate.toString());
        OnlinesPatrolExample onlinesPatrolExample = null;
        PageInfo pageInfo = null;
        PageHelper.startPage(params.getPageNum(), params.getPageSize());
        Page<CaseInfo> res = onlinesPatrolMapper.getFailedCaseInfo(beginDate, endDate, params.getGroup());
        pageInfo = new PageInfo<>(res);
        return pageInfo;
    }

    public PageInfo getTimeoutCaseInfo(Date beginDate, Date endDate, DataReqDto params) {
        System.out.println("开始日期" + beginDate.toString());
        System.out.println("结束日期" + endDate.toString());
        OnlinesPatrolExample onlinesPatrolExample = null;
        PageInfo pageInfo = null;
        PageHelper.startPage(params.getPageNum(), params.getPageSize());
        Page<CaseInfo> res = onlinesPatrolMapper.getTimeoutCaseInfo(beginDate, endDate, params.getGroup());
        pageInfo = new PageInfo<>(res);
        return pageInfo;
    }

    public PageInfo getTestPlanList(Date beginDate, Date endDate, DataReqDto params) {
//        System.out.println("开始日期" + beginDate.toString());
//        System.out.println("结束日期" + endDate.toString());
        PageInfo pageInfo = null;
        PageHelper.startPage(params.getPageNum(), params.getPageSize());
        Page<TestPlanInfo> res = onlinesPatrolMapper.getTestPlanList(beginDate, endDate);
        pageInfo = new PageInfo<>(res);
        return pageInfo;
    }

    public H5Stat getH5Stat(Date beginDate, Date endDate, Integer group) {
        H5Stat stat = new H5Stat();
        Integer failedCaseCount = onlinesPatrolMapper.getFailedCaseCount(beginDate, endDate, group);
        Integer timeOutCaseCount = onlinesPatrolMapper.getTimeoutCaseCount(beginDate, endDate, group);
        Integer planCount = onlinesPatrolMapper.getPlanCount(beginDate, endDate);
        stat.setFailedCaseCount(failedCaseCount);
        stat.setTimeoutCaseCount(timeOutCaseCount);
        stat.setPlanCount(planCount);  // 巡检次数
        // 如果开始日期 = 结束日期，就和上一天比较做趋势
        if (DateUtil.formatDate(beginDate, "yyyy-MM-dd").equals(DateUtil.formatDate(endDate, "yyyy-MM-dd"))) {
            Date yesterday = DateUtil.getAfterDayDate(beginDate, -1);
            Integer yesterdayInt = Integer.valueOf(DateUtil.formatDate(yesterday, "yyyyMMdd"));
            // 前一天用例失败数
            Integer failedCaseCountBefore = onlinesPatrolMapper.getFailedCaseCount(DateUtil.getDayStartDate(yesterdayInt), DateUtil.getDayEndDate(yesterdayInt), group);
            stat.setFailedCaseCountBefore(failedCaseCountBefore);
            // 前一天用例超时数
            Integer timeoutCaseCountBefore = onlinesPatrolMapper.getTimeoutCaseCount(DateUtil.getDayStartDate(yesterdayInt), DateUtil.getDayEndDate(yesterdayInt), group);
            stat.setTimeoutCaseCountBefore(timeoutCaseCountBefore);
        }
        return stat;
    }
}