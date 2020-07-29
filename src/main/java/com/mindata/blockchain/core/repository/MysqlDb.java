package com.mindata.blockchain.core.repository;

import com.mindata.blockchain.core.model.BlockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author wangchenxin
 * @date 2020/7/29  8:45
 * @desc
 */
public interface MysqlDb extends JpaRepository<BlockEntity, Long> {

    BlockEntity save(BlockEntity blockEntity);

    BlockEntity findTopBySkeyOrderByIdDesc(String skey);

    void deleteBySkey(String key);

    List<BlockEntity> findByShash(String hash);
}
