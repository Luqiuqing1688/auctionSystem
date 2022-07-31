package com.web.auction.mapper;


import com.web.auction.pojo.Auction;
import com.web.auction.pojo.AuctionCustom;

import java.util.List;

public interface AuctionCustomMapper {

    Auction queryAuctionAndRecordById(int auctionid);

    List<AuctionCustom> queryEndAuctionList();

    List<Auction> queryNowAuctionList();
}
