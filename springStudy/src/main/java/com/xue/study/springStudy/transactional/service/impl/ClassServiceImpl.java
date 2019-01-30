package com.xue.study.springStudy.transactional.service.impl;

import com.xue.study.springStudy.transactional.dao.ClassDao;
import com.xue.study.springStudy.transactional.dto.ClassDo;
import com.xue.study.springStudy.transactional.service.ClassService;
import lombok.Data;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2019/1/16 17:52
 * @Description:
 */
@Data
public class ClassServiceImpl implements ClassService , ApplicationContextAware {
    private ClassDao classDao;

    private ApplicationContext applicationContext;

    private PlatformTransactionManager platformTransactionManager;
    @Override
    public int iaddOnes(ClassDo classDo) {
        int num = this.iaddOne(classDo);

        classDo.setClassId(classDo.getClassId()+1);
        ClassService classService2 = (ClassService) applicationContext.getBean("serviceProxy");
        int num2 = classService2.iaddOne(classDo);

//        classDo.setClassId(classDo.getClassId()+1);
//        ClassService classService3 = (ClassService)AopContext.currentProxy();
//        int num3 =classService3.addOne(classDo);

        return num;
    }

    @Override
    public int iaddOne(ClassDo classDo) {
        int num = classDao.addOne(classDo);
        return num;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addOneAnnotation(ClassDo classDo) {
        int num = classDao.addOne(classDo);
        ClassServiceImpl classService2 = (ClassServiceImpl) applicationContext.getBean("classService");
        int num2 = classService2.getClassDao().addOne(classDo);
        if(1==1){
            throw new RuntimeException("事务回滚测试");
        }
        return num;
    }
    @Override
    public int programmingAddOne(ClassDo classDo){
        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);
        try {
            int num = classDao.addOne(classDo);
//            platformTransactionManager.rollback(transactionStatus);
        }catch (Exception e){
            platformTransactionManager.rollback(transactionStatus);
        }finally {

        }
        platformTransactionManager.commit(transactionStatus);
        return 0;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
