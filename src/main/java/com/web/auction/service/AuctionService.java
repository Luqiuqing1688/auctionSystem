package com.web.auction.service;


import com.web.auction.pojo.Auction;
import com.web.auction.pojo.AuctionCustom;
import com.web.auction.pojo.Record;

import java.util.List;

public interface AuctionService {

    List<Auction> queryAuctions(Auction auction);

    void saveAuction(Auction auction);

    Auction queryAuctionsById(int id);

    void updateAuction(Auction auction);

    void deleteAuction(int auctionid);

    Auction queryAuctionDetails(int auctionid);

    void addAuctionRecord(Record record) throws Exception;

    List<AuctionCustom> queryEndAuctionResult();

    List<Auction> queryNowAuctionList();
}
