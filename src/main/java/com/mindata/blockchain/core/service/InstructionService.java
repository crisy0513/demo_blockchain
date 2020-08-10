package com.mindata.blockchain.core.service;

import cn.hutool.core.bean.BeanUtil;
import com.mindata.blockchain.block.Instruction;
import com.mindata.blockchain.block.InstructionReverse;
import com.mindata.blockchain.block.Operation;
import com.mindata.blockchain.common.CommonUtil;
import com.mindata.blockchain.common.algorithm.SM2Utils;
import com.mindata.blockchain.common.algorithm.SM3Utils;
import com.mindata.blockchain.common.exception.TrustSDKException;
import com.mindata.blockchain.core.requestbody.InstructionBody;
import org.springframework.stereotype.Service;

/**
 * 一条指令的service
 *
 * @author wuweifeng wrote on 2018/3/7.
 */
@Service
public class InstructionService {
    /**
     * 校验公私钥是不是一对
     *
     * @param instructionBody
     *         instructionBody
     * @return boolean
     * @throws TrustSDKException
     *         TrustSDKException
     */
    public boolean checkKeyPair(InstructionBody instructionBody) throws TrustSDKException {
        return SM2Utils.checkPairKey(instructionBody.getPrivateKey(), instructionBody.getPublicKey());
    }

    /**
     * 校验内容的合法性
     * @param instructionBody instructionBody
     * @return true false
     */
    public boolean checkContent(InstructionBody instructionBody) {
        byte operation = instructionBody.getOperation();
        if (operation != Operation.ADD && operation != Operation.DELETE && operation != Operation.UPDATE) {
            return false;
        }
        //不是add时，必须要有id和json和原始json
        return Operation.UPDATE != operation && Operation.DELETE != operation || instructionBody.getInstructionId()
                != null && instructionBody.getJson() != null && instructionBody.getOldJson() != null;
    }

    /**
     * 根据传来的body构建一条指令
     *
     * @param instructionBody
     *         body
     * @return Instruction
     */
    public Instruction build(InstructionBody instructionBody) throws Exception {
        Instruction instruction = new Instruction();
        BeanUtil.copyProperties(instructionBody, instruction);
        if (Operation.ADD == instruction.getOperation()) {
            instruction.setInstructionId(CommonUtil.generateUuid());
        }
        instruction.setTimeStamp(CommonUtil.getNow());
        String buildStr = getSignString(instruction);
        //设置签名，供其他人验证
        instruction.setSign(SM2Utils.signString(instructionBody.getPrivateKey(),buildStr.getBytes()));
        //设置hash，
        instruction.setHash(SM3Utils.encrypt(buildStr));

        return instruction;
    }
    
    private String getSignString(Instruction instruction) {
    	return instruction.getOperation() + instruction.getTable() + instruction
    			.getInstructionId() + (instruction.getJson()==null?"":instruction.getJson());
    }

    /**
     * 根据一个指令，计算它的回滚时的指令。<p>
     * 如add table1 {id:xxx, name:"123"}，那么回滚时就是delete table1 {id:xxx}
     * 如delete table2 id2 oldJson:{id:xxx, name:"123"}，那么回滚时就是add table2 {id:xxx, name:"123"}。
     * 如update table3 id3 json:{id:xxx, name:"123"} oldJson:{id:xxx, name:"456"}
     * 注意，更新和删除时，原来的json都得有，不然没法回滚
     *
     * @param instruction
     *         instruction
     * @return 回滚指令
     */
    public InstructionReverse buildReverse(Instruction instruction) {
        InstructionReverse instructionReverse = new InstructionReverse();
        BeanUtil.copyProperties(instruction, instructionReverse);

        if (Operation.ADD == instruction.getOperation()) {
            instructionReverse.setOperation(Operation.DELETE);
        } else if (Operation.DELETE == instruction.getOperation()) {
            instructionReverse.setOperation(Operation.ADD);
        }

        return instructionReverse;
    }

    public boolean checkSign(Instruction instruction){
        String buildStr = getSignString(instruction);
        return SM2Utils.verifyString(instruction.getPublicKey(),buildStr,instruction.getSign());
    }

    public boolean checkHash(Instruction instruction) {
        String buildStr = getSignString(instruction);
        return SM3Utils.encrypt(buildStr).equals(instruction.getHash());
    }
}
