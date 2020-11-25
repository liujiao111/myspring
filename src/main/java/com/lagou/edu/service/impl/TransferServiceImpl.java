package com.lagou.edu.service.impl;

import com.lagou.edu.annon.Autowired;
import com.lagou.edu.annon.Service;
import com.lagou.edu.annon.Transactional;
import com.lagou.edu.dao.AccountDao;
import com.lagou.edu.pojo.Account;
import com.lagou.edu.service.TransferService;


/**
 * @author liujiao
 */
@Service(value = "")
@Transactional
public class TransferServiceImpl implements TransferService {

    //改造3
    @Autowired(value = "accountDao")
    private AccountDao accountDao;

    @Override
    public void transfer(String fromCardNo, String toCardNo, int money) throws Exception {
        Account from = accountDao.queryAccountByCardNo(fromCardNo);
        Account to = accountDao.queryAccountByCardNo(toCardNo);

        from.setMoney(from.getMoney() - money);
        to.setMoney(to.getMoney() + money);

        accountDao.updateAccountByCardNo(to);
        int i = 10/0;
        accountDao.updateAccountByCardNo(from);
    }
}
