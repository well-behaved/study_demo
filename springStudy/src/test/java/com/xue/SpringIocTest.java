package com.xue;


import org.junit.Test;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2018/8/9 10:19
 * @Description:
 */
public class SpringIocTest {
    @Test
    public void iocTest1(){}
    @Test
    public void test(){
        System.out.println(WorkflowTypeEnums.APPLY_ALTER_PRICE);
    }

    @Test
    public void test2(){
        //排序略
        Integer[] nums = {-111,-11,-4,-1,-1,0, 1, 2,9,23,111};
        Integer begin = 0;
        Integer end = nums.length-1;
        while(begin<end){
            Integer first = nums[begin];
            Integer thrid = nums[end];
            end--;
            while (begin<end){
                Integer second =nums[end];
//                System.out.println(first.toString()+thrid.toString()+second.toString()+"--");
                if(first+thrid+second==0){
                    System.out.println(first.toString()+"***"+second.toString()+"***"+thrid.toString());
                }
                end--;
            }
            begin++;
            end=nums.length-1;
        }
    }
}
