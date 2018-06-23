package com.horyu1234.eventangel.controller.admin;

import com.horyu1234.eventangel.constant.View;
import com.horyu1234.eventangel.domain.Account;
import com.horyu1234.eventangel.factory.ModelAttributeNameFactory;
import com.horyu1234.eventangel.factory.SessionAttributeNameFactory;
import com.horyu1234.eventangel.form.LoginForm;
import com.horyu1234.eventangel.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

/**
 * Created by horyu on 2018-04-03
 */
@RequestMapping("/admin")
@Controller
public class AccountController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);
    private AccountService accountService;

    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model, HttpSession session) {
        if (session.getAttribute(SessionAttributeNameFactory.LOGIN_USERNAME) != null) {
            return View.ADMIN_EVENT_SETTING.toRedirect();
        }

        model.addAttribute(ModelAttributeNameFactory.VIEW_NAME, View.ADMIN_LOGIN.toView());

        return View.LAYOUT.getTemplateName();
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginReceive(Model model, LoginForm loginForm, HttpSession session) {
        Account account = accountService.login(loginForm.getUsername(), loginForm.getPassword());

        if (account == null) {
            model.addAttribute("loginFailed", true);
            model.addAttribute(ModelAttributeNameFactory.LOGIN_USERNAME, loginForm.getUsername());
            model.addAttribute(ModelAttributeNameFactory.VIEW_NAME, View.ADMIN_LOGIN.toView());

            return View.LAYOUT.getTemplateName();
        }

        session.setAttribute(SessionAttributeNameFactory.LOGIN_USERNAME, account.getUsername());
        session.setAttribute(SessionAttributeNameFactory.LOGIN_NICKNAME, account.getNickname());

        LOGGER.info("{} 님이 로그인하셨습니다.", loginForm.getUsername());

        return View.ADMIN_EVENT_SETTING.toRedirect();
    }

    @RequestMapping(value = "/logout")
    public String logout(HttpSession session) {
        if (session.getAttribute(SessionAttributeNameFactory.LOGIN_USERNAME) == null) {
            return View.ADMIN_LOGIN.toRedirect();
        }

        session.removeAttribute(SessionAttributeNameFactory.LOGIN_USERNAME);
        session.removeAttribute(SessionAttributeNameFactory.LOGIN_NICKNAME);

        return View.ADMIN_LOGIN.toRedirect();
    }
}