package com.paysphere.query.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paysphere.db.entity.MerchantOperator;
import com.paysphere.query.MerchantOperatorQueryService;
import com.paysphere.query.dto.MerchantOperatorDTO;
import com.paysphere.query.dto.PageDTO;
import com.paysphere.query.param.MerchantOperatorListParam;
import com.paysphere.query.param.MerchantOperatorPageParam;
import com.paysphere.repository.MerchantOperatorService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MerchantOperatorQueryServiceImpl implements MerchantOperatorQueryService {

    @Resource
    MerchantOperatorService merchantOperatorService;

    @Override
    public PageDTO<MerchantOperatorDTO> pageMerchantOperatorList(MerchantOperatorPageParam param) {
        log.info("pageMerchantOperatorList param={}", JSONUtil.toJsonStr(param));

        QueryWrapper<MerchantOperator> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(param.getMerchantId()), MerchantOperator::getMerchantId, param.getMerchantId())
                .like(StringUtils.isNotBlank(param.getUsername()), MerchantOperator::getUsername, param.getUsername())
                .eq(Objects.nonNull(param.getRole()), MerchantOperator::getRole, param.getRole());
        Page<MerchantOperator> page = merchantOperatorService.page(new Page<>(param.getPageNum(), param.getPageSize()), queryWrapper);
        if (Objects.isNull(page) || 0 == page.getTotal()) {
            return PageDTO.empty();
        }

        List<MerchantOperatorDTO> collect = page.getRecords().stream().map(e -> {
            MerchantOperatorDTO operatorDTO = new MerchantOperatorDTO();
            operatorDTO.setId(e.getId());
            operatorDTO.setMerchantId(e.getMerchantId());
            operatorDTO.setUsername(e.getUsername());
            operatorDTO.setRole(e.getRole());
            operatorDTO.setStatus(e.isStatus());
            operatorDTO.setAttribute(e.getAttribute());
            return operatorDTO;
        }).collect(Collectors.toList());
        return PageDTO.of(page.getTotal(), page.getCurrent(), collect);
    }

    @Override
    public List<MerchantOperator> getMerchantOperatorList(MerchantOperatorListParam param) {
        log.info("getMerchantOperatorList param={}", JSONUtil.toJsonStr(param));

        QueryWrapper<MerchantOperator> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(param.getMerchantId()), MerchantOperator::getMerchantId, param.getMerchantId())
                .eq(StringUtils.isNotBlank(param.getUsername()), MerchantOperator::getUsername, param.getUsername());
        return merchantOperatorService.list(queryWrapper);
    }

}
