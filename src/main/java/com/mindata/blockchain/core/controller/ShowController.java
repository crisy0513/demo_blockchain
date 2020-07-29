package com.mindata.blockchain.core.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.mindata.blockchain.block.Block;
import com.mindata.blockchain.block.Instruction;
import com.mindata.blockchain.block.db.DbStore;
import com.mindata.blockchain.common.algorithm.SM3Utils;
import com.mindata.blockchain.common.exception.TrustSDKException;
import com.mindata.blockchain.core.bean.BaseData;
import com.mindata.blockchain.core.bean.ResultGenerator;
import com.mindata.blockchain.core.manager.DbBlockManager;
import com.mindata.blockchain.core.model.BlockEntity;
import com.mindata.blockchain.core.requestbody.BlockRequestBody;
import com.mindata.blockchain.core.requestbody.InstructionBody;
import com.mindata.blockchain.core.service.BlockService;
import com.mindata.blockchain.core.service.InstructionService;
import com.mindata.blockchain.core.service.PairKeyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wangchenxin
 * @date 2020/7/27  10:35
 * @desc
 */

@Api(tags = "演示接口", description = "区块链演示接口")
@RestController
@RequestMapping("/index")
public class ShowController {

    @Resource
    private PairKeyService pairKeyService;
    @Resource
    private InstructionService instructionService;
    @Resource
    private BlockService blockService;
    @Resource
    private DbBlockManager dbBlockManager;
    @Resource
    private DbStore dbStore;

    /**
     * 生成公钥私钥
     */
    @ApiOperation(value = "区块链公私钥接口", notes = "生成区块链节点公私钥", httpMethod = "GET", response = BaseData.class)
    @GetMapping("/getkey")
    public BaseData generate() throws TrustSDKException {
        return ResultGenerator.genSuccessResult(pairKeyService.generate());
    }

    /**
     * 新增区块。构建指令并执行，使用私钥签名后的指令
     */
    @ApiOperation(value = "新增区块", notes = "新增区块", httpMethod = "POST", response = BaseData.class)
    @PostMapping
    public BaseData build(@RequestBody InstructionBody instructionBody) throws Exception {
        if (!instructionService.checkKeyPair(instructionBody)) {
            return ResultGenerator.genFailResult("公私钥不是一对");
        }
        if (!instructionService.checkContent(instructionBody)) {
            return ResultGenerator.genFailResult("Delete和Update操作需要有id和json内容");
        }
        Instruction instruction = instructionService.build(instructionBody);

        BlockRequestBody blockRequestBody = new BlockRequestBody();
        blockRequestBody.setPublicKey(instructionBody.getPublicKey());
        com.mindata.blockchain.block.BlockBody blockBody = new com.mindata.blockchain.block.BlockBody();
        blockBody.setInstructions(CollectionUtil.newArrayList(instruction));
        blockRequestBody.setBlockBody(blockBody);

        return ResultGenerator.genSuccessResult(blockService.addBlock(blockRequestBody));
    }

    /**
     * 查看区块链上的信息
     */


    /**
     * 获取第一个区块信息
     */
    @ApiOperation(value = "获取第一个区块信息", notes = "获取第一个区块信息", httpMethod = "GET", response = BaseData.class)
    @GetMapping("/first")
    public BaseData first() {
        Block block = dbBlockManager.getFirstBlock();
//        BlockPacket packet = new PacketBuilder<RpcBlockBody>()
//                .setType(PacketType.NEXT_BLOCK_INFO_REQUEST)
//                .setBody(new RpcBlockBody(block)).build();
//        packetSender.sendGroup(packet);
        return ResultGenerator.genSuccessResult(block);
    }
    /**
     * 获取最后一个block的信息
     */
    @ApiOperation(value = "获取最后一个块信息", notes = "获取最后一个块信息", httpMethod = "GET", response = BaseData.class)
    @GetMapping("last")
    public BaseData last() {
        return ResultGenerator.genSuccessResult(dbBlockManager.getLastBlock());
    }

    /**
     * 验证
     * @param dh 档号
     * @param tm 提名
     * @param yw 原文
     * @return
     */
    @ApiOperation(value = "验证接口", notes = "获取所有区块链信息", httpMethod = "GET", response = BaseData.class)
    @GetMapping("check")
    public BaseData check(@ApiParam(name = "dh", value = "档号", required = true)  @RequestParam(value = "dh") String dh,
                          @ApiParam(name = "tm", value = "提名", required = true)  @RequestParam(value = "tm") String tm,
                          @ApiParam(name = "yw", value = "原文", required = true)  @RequestParam(value = "yw") String yw) {
        String content = dh+tm+yw;
        List<BlockEntity> byHash = dbStore.getByHash(SM3Utils.encrypt(content));
        if(byHash==null || byHash.size()<=0){
            return ResultGenerator.genFailResult("检索无结果，数据可能已出现异常");
        }
        return ResultGenerator.genSuccessResult(byHash);
    }

    /**
     * 查询所有数据
     *//*
    @ApiOperation(value = "查询所有-区块链数据", notes = "查询区块链数据", httpMethod = "GET", response = BaseData.class)
    @GetMapping("sqlite") 
    public BaseData sqlite() {
        return ResultGenerator.genSuccessResult(messageManager.findAll());
    }

    *//**
     * 查询content字段
     *//*
    @ApiOperation(value = "查询所有-区块链内容", notes = "查询区块链内容", httpMethod = "GET", response = BaseData.class)
    @GetMapping("sqlite/content")
    public BaseData content() {
        return ResultGenerator.genSuccessResult(messageManager.findAllContent());
    }*/

}
