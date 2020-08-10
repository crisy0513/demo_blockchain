package com.mindata.blockchain.core.service;

import com.mindata.blockchain.core.model.DaEntity;
import com.mindata.blockchain.core.repository.DaRespository;
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

    public DaEntity findById(Long id){
        return daRespository.findById(id);
    }

    @Transactional
    public void update(Long id,String dh,String tm,String yw){
        DaEntity daEntity = new DaEntity();
        daEntity.setId(id);
        daEntity.setDh(dh);
        daEntity.setTm(tm);
        daEntity.setYw(yw);
        daRespository.save(daEntity);
    }


    public List<DaEntity> queryAll() {
        List<DaEntity> all = daRespository.findAll();
        return all;
    }
}
