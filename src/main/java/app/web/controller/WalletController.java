package app.web.controller;

import app.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }


    @GetMapping
    public ModelAndView getWalletsPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("wallets");
        return modelAndView;
    }






}
