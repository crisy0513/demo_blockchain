package com.mindata.blockchain.core.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.mindata.blockchain.block.Block;
import com.mindata.blockchain.block.Instruction;
import com.mindata.blockchain.block.Operation;
import com.mindata.blockchain.block.check.BlockChecker;
import com.mindata.blockchain.block.db.DbStore;
import com.mindata.blockchain.common.FastJsonUtil;
import com.mindata.blockchain.common.algorithm.SM2Utils;
import com.mindata.blockchain.common.algorithm.SM3Utils;
import com.mindata.blockchain.common.exception.TrustSDKException;
import com.mindata.blockchain.core.bean.BaseData;
import com.mindata.blockchain.core.bean.ResultGenerator;
import com.mindata.blockchain.core.manager.DbBlockManager;
import com.mindata.blockchain.core.model.BlockEntity;
import com.mindata.blockchain.core.model.DaEntity;
import com.mindata.blockchain.core.requestbody.BlockRequestBody;
import com.mindata.blockchain.core.requestbody.InstructionBody;
import com.mindata.blockchain.core.service.BlockService;
import com.mindata.blockchain.core.service.DaService;
import com.mindata.blockchain.core.service.InstructionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Value;
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
    private InstructionService instructionService;
    @Resource
    private BlockService blockService;
    @Resource
    private DbBlockManager dbBlockManager;
    @Resource
    private BlockChecker blockChecker;
    @Resource
    private DbStore dbStore;
    @Value("${publicKey:A8WLqHTjcT/FQ2IWhIePNShUEcdCzu5dG+XrQU8OMu54}")
    private String publicKey;
    @Value("${privateKey:yScdp6fNgUU+cRUTygvJG4EBhDKmOMRrK4XJ9mKVQJ8=}")
    private String privateKey;
    @Resource
    private DaService daService;
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
        List<BlockEntity> blockEntities = dbStore.getByHash(SM3Utils.encrypt(content));
        if(blockEntities==null || blockEntities.size()<=0){
            return ResultGenerator.genFailResult("检索无结果，档案可能已被篡改");
        }

        for(BlockEntity blockEntitie:blockEntities){
            Block block = FastJsonUtil.toBean(blockEntitie.getSvalue(), Block.class);
            if((block.getBlockBody().getInstructions().get(0).getJson()).indexOf(blockEntitie.getShash())==-1){
                return ResultGenerator.genFailResult("检索发现链路中的数据可能已经被篡改，请先进行全量检查");
            }
        }
        //判断是否为最新的
        List<BlockEntity> newblockEntities = dbStore.getByOldHash(SM3Utils.encrypt(content));
        //有区块以这个信息为oldhash
        if(newblockEntities!=null && newblockEntities.size() > 0){
            //判断num的值是否更大
            //blockentities中最大的num和newblockentities中最大的num比较
            int num1 = 0;
            int num2 = 0;
            for(BlockEntity blockEntitie:blockEntities){
                Block block = FastJsonUtil.toBean(blockEntitie.getSvalue(), Block.class);
                if(num1<block.getBlockHeader().getNumber()){
                    num1 = block.getBlockHeader().getNumber();
                }
            }
            for(BlockEntity blockEntitie:newblockEntities){
                Block block = FastJsonUtil.toBean(blockEntitie.getSvalue(), Block.class);
                if(num2<block.getBlockHeader().getNumber()){
                    num2 = block.getBlockHeader().getNumber();
                }
            }
            if(num2 > num1){
                return ResultGenerator.genSuccessResult("档案真实存在，但可能不是最新版本，请确认");
            }
        }

        return ResultGenerator.genSuccessResult(blockEntities);
    }


    /**
     * 测试生成一个insert:Block，公钥私钥可以通过PairKeyController来生成
     * @param
     *
     */
    @GetMapping("/create")
    @ApiOperation(value = "创建一个区块", notes = "创建一个新区块", httpMethod = "GET", response = BaseData.class)
    public BaseData create(@ApiParam(name = "dh", value = "档号", required = true)  @RequestParam(value = "dh") String dh,
                           @ApiParam(name = "tm", value = "提名", required = true)  @RequestParam(value = "tm") String tm,
                           @ApiParam(name = "yw", value = "原文", required = true)  @RequestParam(value = "yw") String yw
    ) throws Exception {
        daService.addDa(dh, tm, yw);
        InstructionBody instructionBody = new InstructionBody();
        instructionBody.setOperation(Operation.ADD);
        instructionBody.setTable("message");
        String content = dh+tm+yw;
        instructionBody.setJson("{\"content\":\"" + SM3Utils.encrypt(content) + "\"}");
        /*instructionBody.setPublicKey("A8WLqHTjcT/FQ2IWhIePNShUEcdCzu5dG+XrQU8OMu54");
        instructionBody.setPrivateKey("yScdp6fNgUU+cRUTygvJG4EBhDKmOMRrK4XJ9mKVQJ8=");*/
        instructionBody.setPublicKey(publicKey);
        instructionBody.setPrivateKey(privateKey);
        Instruction instruction = instructionService.build(instructionBody);

        BlockRequestBody blockRequestBody = new BlockRequestBody();
        blockRequestBody.setPublicKey(instructionBody.getPublicKey());
        com.mindata.blockchain.block.BlockBody blockBody = new com.mindata.blockchain.block.BlockBody();
        blockBody.setInstructions(CollectionUtil.newArrayList(instruction));

        blockRequestBody.setBlockBody(blockBody);

        return ResultGenerator.genSuccessResult(blockService.addBlock(blockRequestBody));
    }

    /**
     * 测试生成一个update:Block，公钥私钥可以通过PairKeyController来生成
     * @param id 更新的主键
     * @param
     *
     */
    @GetMapping("update")
    @ApiOperation(value = "更新区块链内容", notes = "根据ID更新区块链内容", httpMethod = "GET", response = BaseData.class)
    public BaseData testUpdate(@ApiParam(name = "id", value = "主键", required = true) @RequestParam(value = "id",required = true) Long id,
                               @ApiParam(name = "dh", value = "档号", required = true)  @RequestParam(value = "dh") String dh,
                               @ApiParam(name = "tm", value = "提名", required = true)  @RequestParam(value = "tm") String tm,
                               @ApiParam(name = "yw", value = "原文", required = true)  @RequestParam(value = "yw") String yw) throws Exception {
        if(id == null) return ResultGenerator.genSuccessResult("主键不可为空");
        InstructionBody instructionBody = new InstructionBody();
        instructionBody.setOperation(Operation.UPDATE);
        instructionBody.setTable("message");
        String content = dh+tm+yw;
        instructionBody.setJson("{\"content\":\"" + SM3Utils.encrypt(content) + "\"}");
        DaEntity daEntity = daService.findById(id);
        if (daEntity!=null){
            instructionBody.setOldJson("{\"content\":\"" + SM3Utils.encrypt(daEntity.getDh()+daEntity.getTm()+daEntity.getYw()) + "\"}");
        }else{
            return ResultGenerator.genSuccessResult("主键查不到对应记录，请检查该档案是否存在");
        }
        daService.update(id,dh,tm,yw);
    	 /*instructionBody.setPublicKey("A8WLqHTjcT/FQ2IWhIePNShUEcdCzu5dG+XrQU8OMu54");
        instructionBody.setPrivateKey("yScdp6fNgUU+cRUTygvJG4EBhDKmOMRrK4XJ9mKVQJ8=");*/
        instructionBody.setPublicKey(publicKey);
        instructionBody.setPrivateKey(privateKey);
        Instruction instruction = instructionService.build(instructionBody);

        BlockRequestBody blockRequestBody = new BlockRequestBody();
        blockRequestBody.setPublicKey(instructionBody.getPublicKey());
        com.mindata.blockchain.block.BlockBody blockBody = new com.mindata.blockchain.block.BlockBody();
        blockBody.setInstructions(CollectionUtil.newArrayList(instruction));

        blockRequestBody.setBlockBody(blockBody);

        return ResultGenerator.genSuccessResult(blockService.addBlock(blockRequestBody));
    }


    /**
     * 全量检测区块是否正常
     * @return
     * null - 通过
     * hash - 第一个异常hash
     */
    @ApiOperation(value = "全量检测区块是否正常", notes = "全量检测区块是否正常", httpMethod = "GET", response = BaseData.class)
    @GetMapping("checkAll")
    public BaseData checkAll() {

        Block block = dbBlockManager.getFirstBlock();

        String hash = null;
        while(block != null && hash == null) {
            hash = blockChecker.checkBlock(block);
            block = dbBlockManager.getNextBlock(block);
        }
        return ResultGenerator.genSuccessResult(hash);
    }

    /**
     * 获取全部档案数据
     */
    @ApiOperation(value = "查询档案表所有数据", notes = "查询档案表所有数据", httpMethod = "GET", response = BaseData.class)
    @GetMapping("queryAllDa")
    public BaseData queryAllDa() {
        List<DaEntity> daEntities = daService.queryAll();
        return ResultGenerator.genSuccessResult(daEntities);
    }

    /**
     * 私下修改档案表数据，不入链
     */
    @ApiOperation(value = "修改档案表数据，不入链", notes = "修改档案表数据，不入链", httpMethod = "POST", response = BaseData.class)
    @PostMapping("editDa")
    public BaseData editDa(@ApiParam(name = "id", value = "主键", required = true) @RequestParam(value = "id",required = true) Long id,
                           @ApiParam(name = "dh", value = "档号", required = true)  @RequestParam(value = "dh") String dh,
                           @ApiParam(name = "tm", value = "提名", required = true)  @RequestParam(value = "tm") String tm,
                           @ApiParam(name = "yw", value = "原文", required = true)  @RequestParam(value = "yw") String yw) {
        DaEntity daEntity = daService.findById(id);
        if (daEntity==null){
            return ResultGenerator.genSuccessResult("主键查不到对应记录，请检查该档案是否存在");
        }
        daService.update(id,dh,tm,yw);
        return ResultGenerator.genSuccessResult();
    }


    /*    *//**
     * 生成公钥私钥
     *//*
    @ApiOperation(value = "区块链公私钥接口", notes = "生成区块链节点公私钥", httpMethod = "GET", response = BaseData.class)
    @GetMapping("/getkey")
    public BaseData generate() throws TrustSDKException {
        return ResultGenerator.genSuccessResult(SM2Utils.generateKeyPair());
    }

    *//**
     * 新增区块。构建指令并执行，使用私钥签名后的指令
     *//*
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
    */



}
