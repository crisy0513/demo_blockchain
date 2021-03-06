package com.mindata.blockchain.core.sqlite;

import com.mindata.blockchain.ApplicationContextProvider;
import com.mindata.blockchain.block.Block;
import com.mindata.blockchain.block.Instruction;
import com.mindata.blockchain.core.event.DbSyncEvent;
import com.mindata.blockchain.core.manager.SyncManager;
import com.mindata.blockchain.core.manager.DbBlockManager;
import com.mindata.blockchain.core.model.SyncEntity;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 对sqlite数据库的操作（监听新增区块请求，执行对应的sql命令）
 *
 * @author wuweifeng wrote on 2018/3/15.
 */
@Component
public class SqliteManager {
    @Resource
    private SyncManager syncManager;
    @Resource
    private DbBlockManager dbBlockManager;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * sqlite同步，监听该事件后，去check当前已经同步到哪个区块了，然后继续执行之后的区块
     */
    @EventListener(DbSyncEvent.class)
    public void dbSync() {
        logger.info("开始执行导入区块到Sqlite操作");
        //查看同步到哪个区块了
        SyncEntity syncEntity = syncManager.findLastOne();

        Block block;
        if (syncEntity == null) {
            //从第一个开始
            block = dbBlockManager.getFirstBlock();
            logger.info("正在导入第一个区块，hash为：" + block.getHash());
        } else {
            Block lastBlock = dbBlockManager.getLastBlock();
            //已经同步到最后一块了
            if (lastBlock.getHash().equals(syncEntity.getHash())) {
                logger.info("导入完毕");
                return;
            }
            logger.info("正在导入区块，hash为：" + lastBlock.getHash());
            String hash = syncEntity.getHash();
            block = dbBlockManager.getNextBlock(dbBlockManager.getBlockByHash(hash));
        }
        execute(block);
        ApplicationContextProvider.publishEvent(new DbSyncEvent(""));
    }

    /**
     * 根据一个block执行sql
     * 整个block一个事务
     * 
     * @param block
     *         block
     */
    @Transactional(rollbackFor = Exception.class)
    public void execute(Block block) {
        List<Instruction> instructions = block.getBlockBody().getInstructions();
        //InstructionParserImpl类里面执行的是InstructionBase，需要转成InstructionBase
//        for (Instruction instruction : instructions) {
//            if(StringUtils.isEmpty(instruction.getOldJson())){
//                instruction.setOldJson(instruction.getJson());
//            }
//        }
        //doSqlParse(instructions);

        //保存已同步的进度
        SyncEntity syncEntity = new SyncEntity();
        syncEntity.setHash(block.getHash());
        syncManager.save(syncEntity);
    }

    /**
     * 测试block的代码是否能正确执行
     * 
     * @param block block
     */
    @Transactional(rollbackFor = Exception.class)
    public void tryExecute(Block block) {
    	execute(block);
    }
}
