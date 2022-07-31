package com.web.auction.controller;

import com.web.auction.pojo.Auction;
import com.web.auction.pojo.AuctionCustom;
import com.web.auction.pojo.Record;
import com.web.auction.pojo.User;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.web.auction.service.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
public class AuctionController {

    @Autowired
    private AuctionService auctionService;
    //分页的大小


    private static final int PAGE_SIZE = 6;

    @RequestMapping("/queryAuctions")
    public ModelAndView queryAuctions(
            //springmvc默认pojo自动回显，ModelAttribute可以将新的变量名作为key，指定的pojo作为value。
            @ModelAttribute("auction") Auction auction,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum) {

        ModelAndView mv = new ModelAndView();
        //分页拦截，重构sql语句
        PageHelper.startPage(pageNum, PAGE_SIZE);
        List<Auction> list = auctionService.queryAuctions(auction);
        //创建分页bean
        PageInfo page = new PageInfo<>(list);

        mv.addObject("auctionList", list);
        mv.addObject("page", page);
        mv.setViewName("index");
        return mv;
    }

    //MulipartFile接收二进制文件
    @RequestMapping("/publishAuctions")
    public String publishAuctions(Auction auction, MultipartFile pic, HttpSession session) {

        //存放图片的两种方式
        //1.工程所在容器的文件夹中 2.保存在独立的文件存储服务器中
        String path = "d:\\upload";
        File file = new File(path, pic.getOriginalFilename());
        try {
            //pic保存到指定路径
            pic.transferTo(file);
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //2.设置文件的名称和类型
        auction.setAuctionpic(pic.getOriginalFilename());
        auction.setAuctionpictype(pic.getContentType());

        //3.保存auction到数据库
        auctionService.saveAuction(auction);
        return "redirect:/queryAuctions";
    }

    @RequestMapping("/cancleAuction")
    public String cancleAuction() {

        return "redirect:/queryAuctions";
    }

    @RequestMapping("/update/{id}")
    public ModelAndView update(@PathVariable int id) {

        ModelAndView mv = new ModelAndView();
        Auction auction = auctionService.queryAuctionsById(id);
        mv.addObject("auction", auction);
        mv.setViewName("updateAuction");
        return mv;
    }

    @RequestMapping("/submitUpdateAuction")
    public String submitUpdateAuction(Auction auction, MultipartFile pic, HttpSession session) {

        //1.判断是否重新上传了图片
        if (pic.getSize() > 0) {

            String path = session.getServletContext().getRealPath("upload");
            //2.删除旧的文件
            File oldFile = new File(path, auction.getAuctionpic());
            if (oldFile.exists()) {
                oldFile.delete();
            }
            //3.保存新文件到指定路径
            File newFile = new File(path, pic.getOriginalFilename());

            try {
                pic.transferTo(newFile);
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        //4.修改auction信息
        auctionService.updateAuction(auction);

        return "redirect:/queryAuctions";
    }

    @RequestMapping("/delete/{auctionid}")
    public String delete(@PathVariable int auctionid) {

        auctionService.deleteAuction(auctionid);
        return "redirect:/queryAuctions";
    }

    @RequestMapping("/toAuctionDetail/{id}")
    public ModelAndView toAuctionDetail(@PathVariable int id) {

        ModelAndView mv = new ModelAndView();
        //auction封装了三个pojo的数据
        Auction auction = auctionService.queryAuctionDetails(id);

        mv.addObject("auctionDetail", auction);
        mv.setViewName("auctionDetail");
        return mv;
    }

    @RequestMapping("/saveAuctionRecord")
    public String saveAuctionRecord(Record record, HttpSession session) throws Exception {

        User user = (User) session.getAttribute("loginUser");

        //1.设置record相关属性的初值
        record.setUserid(user.getUserid());
        record.setAuctiontime(new Date());

        //2.处理业务异常
        auctionService.addAuctionRecord(record);
        return "redirect:/toAuctionDetail/" + record.getAuctionid();
    }

    @RequestMapping("/queryAuctionResult")
    public ModelAndView queryAuctionResult() {

        ModelAndView mv = new ModelAndView();
        List<AuctionCustom> list1 = auctionService.queryEndAuctionResult();
        List<Auction> list2 = auctionService.queryNowAuctionList();
        mv.addObject("EndAuctionList", list1);
        mv.addObject("NowAuctionList", list2);
        mv.setViewName("auctionResult");
        return mv;
    }
}
