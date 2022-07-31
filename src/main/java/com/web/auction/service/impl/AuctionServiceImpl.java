package com.web.auction.service.impl;

import com.web.auction.mapper.AuctionCustomMapper;
import com.web.auction.mapper.AuctionMapper;
import com.web.auction.mapper.RecordMapper;
import com.web.auction.pojo.*;
import com.web.auction.service.AuctionService;
import com.web.auction.utils.AuctionPriceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AuctionServiceImpl implements AuctionService {

    @Autowired
    private AuctionMapper auctionMapper;

    @Autowired
    private RecordMapper recordMapper;

    @Autowired
    private AuctionCustomMapper auctionCustomMapper;

    @Override
    public List<Auction> queryAuctions(Auction auction) {

        AuctionExample example = new AuctionExample();
        // 得到查询标准
        AuctionExample.Criteria criteria = example.createCriteria();
        if (auction != null) {

            // 模糊查询名称
            if (auction.getAuctionname() != null && !auction.getAuctionname().equals("")) {
                criteria.andAuctionnameLike("%" + auction.getAuctionname() + "%");
            }
            // 模糊查询描述
            if (auction.getAuctiondesc() != null && !auction.getAuctiondesc().equals("")) {
                criteria.andAuctiondescLike("%" + auction.getAuctiondesc() + "%");
            }
            // 大于起拍价
            if (auction.getAuctionstartprice() != null && !auction.getAuctionstartprice().equals("")) {
                criteria.andAuctionstartpriceGreaterThan(auction.getAuctionstartprice());
            }
            // 大于起拍时间
            if (auction.getAuctionstarttime() != null && !auction.getAuctionstarttime().equals("")) {
                criteria.andAuctionstarttimeGreaterThan(auction.getAuctionstarttime());
            }
            // 小于结束时间
            if (auction.getAuctionendtime() != null && !auction.getAuctionendtime().equals("")) {
                criteria.andAuctionendtimeLessThan(auction.getAuctionendtime());
            }

        }
        // 以开始时间降序排序
        example.setOrderByClause("auctionstarttime desc");
        List<Auction> list = auctionMapper.selectByExample(example);

        return list;
    }

    @Override
    public void saveAuction(Auction auction) {

        auctionMapper.insert(auction);
    }

    @Override
    public Auction queryAuctionsById(int id) {

        return auctionMapper.selectByPrimaryKey(id);
    }

    @Override
    public void updateAuction(Auction auction) {

        auctionMapper.updateByPrimaryKey(auction);
    }

    @Override
    public void deleteAuction(int auctionid) {

        // 1.删除商品之下的所以竞拍记录
        RecordExample example = new RecordExample();
        RecordExample.Criteria criteria = example.createCriteria();

        criteria.andAuctionidEqualTo(auctionid);
        recordMapper.deleteByExample(example);

        // 2.删除商品
        auctionMapper.deleteByPrimaryKey(auctionid);
    }

    @Override
    public Auction queryAuctionDetails(int auctionid) {

        Auction auction = auctionCustomMapper.queryAuctionAndRecordById(auctionid);
        return auction;
    }

    @Override
    public void addAuctionRecord(Record record) throws Exception {

        // 1.处理业务异常
        Auction auction = auctionCustomMapper.queryAuctionAndRecordById(record.getAuctionid());
        // 判断竞拍时间是否过期
        if (record.getAuctiontime().after(auction.getAuctionendtime())) {
            throw new AuctionPriceException("竞拍时间已过期！");
        }

        // 判断是否首次竞拍
        if (auction.getRecordList() != null && auction.getRecordList().size() > 0) {

            // 得到当前竞拍的最高价
            if (record.getAuctionprice() <= auction.getRecordList().get(0).getAuctionprice()) {
                throw new AuctionPriceException("竞拍价格必须大于当前竞拍的最高价");
            }
        } else {

            if (record.getAuctionprice() <= auction.getAuctionstartprice()) {
                throw new AuctionPriceException("竞拍价格必须大于起拍价格");
            }
        }

        // 2.添加竞拍记录
        recordMapper.insert(record);
    }

    @Override
    public List<AuctionCustom> queryEndAuctionResult() {

        return auctionCustomMapper.queryEndAuctionList();
    }

    @Override
    public List<Auction> queryNowAuctionList() {

        return auctionCustomMapper.queryNowAuctionList();
    }

}
