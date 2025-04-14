package app.sphere.query.impl;

import app.sphere.query.MerchantOperatorQueryService;
import app.sphere.query.dto.MerchantOperatorDTO;
import app.sphere.query.dto.PageDTO;
import app.sphere.query.param.MerchantOperatorListParam;
import app.sphere.query.param.MerchantOperatorPageParam;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import domain.sphere.repository.MerchantOperatorRepository;
import infrastructure.sphere.db.entity.MerchantOperator;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 商户操作员查询服务
 */
@Slf4j
@Service
public class MerchantOperatorQueryServiceImpl implements MerchantOperatorQueryService {

    @Resource
    MerchantOperatorRepository merchantOperatorRepository;

    /**
     * 分页查询商户操作员列表
     */
    @Override
    public PageDTO<MerchantOperatorDTO> pageMerchantOperatorList(MerchantOperatorPageParam param) {
        log.info("开始分页查询商户操作员列表, param={}", JSONUtil.toJsonStr(param));
        Assert.notNull(param, "查询参数不能为空");

        // 1. 构建查询条件
        LambdaQueryWrapper<MerchantOperator> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(param.getMerchantId()), 
                       MerchantOperator::getMerchantId, param.getMerchantId())
                   .like(StringUtils.isNotBlank(param.getUsername()), 
                        MerchantOperator::getUsername, param.getUsername());

        // 2. 执行分页查询
        Page<MerchantOperator> page = merchantOperatorRepository.page(
            new Page<>(param.getPageNum(), param.getPageSize()), 
            queryWrapper
        );

        // 3. 处理空结果
        if (Objects.isNull(page) || page.getTotal() == 0) {
            log.info("未查询到商户操作员数据");
            return PageDTO.empty();
        }

        // 4. 转换结果
        List<MerchantOperatorDTO> records = page.getRecords().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());

        log.info("商户操作员列表查询完成, total={}, current={}", 
                page.getTotal(), page.getCurrent());
        return PageDTO.of(page.getTotal(), page.getCurrent(), records);
    }

    /**
     * 查询商户操作员列表
     */
    @Override
    public List<MerchantOperator> getMerchantOperatorList(MerchantOperatorListParam param) {
        log.info("开始查询商户操作员列表, param={}", JSONUtil.toJsonStr(param));
        Assert.notNull(param, "查询参数不能为空");

        // 1. 构建查询条件
        LambdaQueryWrapper<MerchantOperator> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(param.getMerchantId()), 
                       MerchantOperator::getMerchantId, param.getMerchantId())
                   .eq(StringUtils.isNotBlank(param.getUsername()), 
                       MerchantOperator::getUsername, param.getUsername());

        // 2. 执行查询
        List<MerchantOperator> result = merchantOperatorRepository.list(queryWrapper);
        
        log.info("商户操作员列表查询完成, size={}", result.size());
        return result;
    }

    /**
     * 转换为DTO对象
     */
    private MerchantOperatorDTO convertToDTO(MerchantOperator operator) {
        MerchantOperatorDTO dto = new MerchantOperatorDTO();
        dto.setId(operator.getId());
        dto.setMerchantId(operator.getMerchantId());
        dto.setUsername(operator.getUsername());
        dto.setStatus(operator.isStatus());
        dto.setAttribute(operator.getAttribute());
        return dto;
    }
}
