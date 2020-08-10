package com.mindata.blockchain.block.db;

import com.mindata.blockchain.block.Block;
import com.mindata.blockchain.core.model.BlockEntity;

import java.util.List;

/**
 * key-value型DB数据库操作接口
 * @author wuweifeng wrote on 2018/3/26.
 */
public interface DbStore {
    /**
     * 数据库key value
     *
     * @param key
     *         key
     * @param value
     *         value
     */
    void put(String key, String value);

    /**
     * 数据库 正常存放的信息
     * @param key
     * @param block
     */
    void put(String key, Block block);

    /**
     * get By Key
     *
     * @param key
     *         key
     * @return value
     */
    String get(String key);

    /**
     * remove by key
     *
     * @param key
     *         key
     */
    void remove(String key);

    /**
     * 根据hash值获取block链信息
     * @param hash
     * @return
     */
    List<BlockEntity> getByHash(String hash);


    List<BlockEntity> getByOldHash(String encrypt);
}
