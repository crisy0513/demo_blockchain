package com.mindata.blockchain.core.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.mindata.blockchain.block.Block;
import com.mindata.blockchain.block.BlockHeader;
import com.mindata.blockchain.block.Instruction;
import com.mindata.blockchain.block.merkle.MerkleTree;
import com.mindata.blockchain.common.CommonUtil;
import com.mindata.blockchain.common.Sha256;
import com.mindata.blockchain.common.exception.TrustSDKException;
import com.mindata.blockchain.core.manager.DbBlockManager;
import com.mindata.blockchain.core.manager.PermissionManager;
import com.mindata.blockchain.core.model.DaEntity;
import com.mindata.blockchain.core.repository.DaRespository;
import com.mindata.blockchain.core.requestbody.BlockRequestBody;
import com.mindata.blockchain.socket.body.RpcBlockBody;
import com.mindata.blockchain.socket.client.PacketSender;
import com.mindata.blockchain.socket.packet.BlockPacket;
import com.mindata.blockchain.socket.packet.PacketBuilder;
import com.mindata.blockchain.socket.packet.PacketType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wuweifeng wrote on 2018/3/8.
 */
@Service
public class DaService {
    @Resource
    private DaRespository daRespository;

    @Transactional
    public DaEntity addDa(String dh,String tm,String yw) {
        DaEntity da = new DaEntity();
        da.setDh(dh);
        da.setTm(tm);
        da.setYw(yw);
        DaEntity daEntity = daRespository.save(da);
        return  daEntity;
    }

}
