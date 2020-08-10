package com.mindata.blockchain.block.db;

import com.alibaba.fastjson.JSONObject;
import com.mindata.blockchain.block.Block;
import com.mindata.blockchain.core.model.BlockEntity;
//import com.mindata.blockchain.socket.common.Const;
//import org.rocksdb.RocksDB;
//import org.rocksdb.RocksDBException;
import com.mindata.blockchain.core.repository.MysqlDb;
import jodd.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.tio.utils.json.Json;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * rocksDB对于存储接口的实现
 * @author wuweifeng wrote on 2018/3/13.
 */
@Component("dbStore")
@ConditionalOnProperty("db.rocksDB")
public class RocksDbStoreImpl implements DbStore {
    @Resource
//    private RocksDB rocksDB;
    private MysqlDb mysqlDb;

    @Override
    @Transactional
    public void put(String key, String value) {
            BlockEntity blockEntity = new BlockEntity();
            blockEntity.setSkey(key);
            blockEntity.setSvalue(value);
            mysqlDb.save(blockEntity);
//            mysqlDb.put(key.getBytes(Const.CHARSET), value.getBytes(Const.CHARSET));
//        try {
//            rocksDB.put(key.getBytes(Const.CHARSET), value.getBytes(Const.CHARSET));
//        } catch (RocksDBException | UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    @Transactional
    public void put(String key, Block block) {
        BlockEntity blockEntity = new BlockEntity();
        blockEntity.setSkey(key);
        blockEntity.setSvalue(Json.toJson(block));
        JSONObject newjson = JSONObject.parseObject(block.getBlockBody().getInstructions().get(0).getJson());
        if(StringUtils.isNotEmpty(block.getBlockBody().getInstructions().get(0).getOldJson())){
            JSONObject oldjson = JSONObject.parseObject(block.getBlockBody().getInstructions().get(0).getOldJson());
            blockEntity.setSoldhash(oldjson.getString("content"));
        }
        blockEntity.setShash(newjson.getString("content"));
        mysqlDb.save(blockEntity);
    }



    @Override
    public String get(String key) {
//        try {
//            byte[] bytes = rocksDB.get(key.getBytes(Const.CHARSET));
//            if (bytes != null) {
//                return new String(bytes, Const.CHARSET);
//            }
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
        BlockEntity blockEntity = mysqlDb.findTopBySkeyOrderByIdDesc(key);
        if(blockEntity!=null){
            return blockEntity.getSvalue();
        }else{
            return null;
        }

    }

    @Override
    public List<BlockEntity> getByHash(String hash) {
        List<BlockEntity> result = mysqlDb.findByShash(hash);
        return result;
    }

    @Override
    public List<BlockEntity> getByOldHash(String encrypt) {
        List<BlockEntity> result = mysqlDb.findBySoldhash(encrypt);
        return result;
    }

    @Override
    @Transactional
    public void remove(String key) {
//        try {
//            rocksDB.delete(rocksDB.get(key.getBytes(Const.CHARSET)));
//        } catch (RocksDBException | UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        mysqlDb.deleteBySkey(key);
    }

}
