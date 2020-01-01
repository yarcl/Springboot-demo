package com.service.impl;

import com.dao.EsDao;
import com.entity.qo.EsQueryQo;
import com.service.EsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xiaozhi on 2019/12/31.
 */
@Service
public class EsServiceImpl implements EsService {

    @Autowired
    private EsDao esDao;

    public String queryByName(EsQueryQo esQueryQo) {
        String response = esDao.queryByName(esQueryQo.getQueryIndex(), esQueryQo.getQueryType(), esQueryQo.getQueryCondition());
        return response;
    }

}
