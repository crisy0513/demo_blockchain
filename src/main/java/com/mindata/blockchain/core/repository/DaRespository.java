package com.mindata.blockchain.core.repository;

import com.mindata.blockchain.core.model.BlockEntity;
import com.mindata.blockchain.core.model.DaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author wangchenxin
 * @date 2020/7/29  8:45
 * @desc
 */
public interface DaRespository extends JpaRepository<DaEntity, Long> {

    DaEntity findById(Long id);
}
