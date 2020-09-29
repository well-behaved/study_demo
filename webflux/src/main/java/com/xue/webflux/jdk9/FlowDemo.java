package com.xue.webflux.jdk9;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

/**
 * @program: demo
 * @description: Flow Demo
 * @author: 01
 * @create: 2018-10-04 13:25
 **/
public class FlowDemo {

    public static void main(String[] args) throws Exception {
        // 1. 定义发布者, 发布的数据类型是 Integer
        // 直接使用jdk自带的SubmissionPublisher, 它实现了 Publisher 接口
        SubmissionPublisher<Integer> publiser = new SubmissionPublisher<>();

        // 2. 定义订阅者
        Flow.Subscriber<Integer> subscriber = new Flow.Subscriber<>() {

            private Flow.Subscription subscription;

            /**
             * 当执行Publisher里的subscribe方法时，发布者会回调订阅者的onSubscribe方法，
             */
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                System.out.println("onSubscribe");
                // 保存订阅关系, 需要用它来给发布者响应
                this.subscription = subscription;

                // 请求一个数据
                this.subscription.request(1);
            }

            @Override
            public void onNext(Integer item) {
                // 接受到一个数据, 处理
                System.out.println("接受到数据: " + item);

                // 处理完调用request再请求一个数据
                this.subscription.request(1);
                // 模拟复杂场景降低消费速度
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 或者已经达到了目标, 可以调用cancel告诉发布者不再接受数据了
                // this.subscription.cancel();
            }

            @Override
            public void onError(Throwable throwable) {
                // 出现了异常(例如处理数据的时候产生了异常)
                throwable.printStackTrace();

                // 我们可以告诉发布者, 后面不接受数据了
                this.subscription.cancel();
            }

            @Override
            public void onComplete() {
                // 全部数据处理完了(发布者关闭了)
                System.out.println("处理完了!");
            }

        };

        // 3. 发布者和订阅者 建立订阅关系
        // 当执行Publisher里的subscribe方法时，发布者会回调订阅者的onSubscribe方法，
        publiser.subscribe(subscriber);

        // 4. 生产数据, 并发布
        // 这里忽略数据生产过程
        for (int i = 0; i < 2000; i++) {
            System.out.println("生成数据:" + i);
            // submit是个block方法
            publiser.submit(i);
        }
        System.out.println("生成数据完毕");

        // 5. 结束后 关闭发布者
        // 正式环境 应该放 finally 或者使用 try-resouce 确保关闭
        publiser.close();


        // debug的时候, 下面这行需要有断点
        // 否则主线程结束无法debug
        System.out.println("主线程结束");
        // 主线程延迟停止, 否则数据没有消费就会退出
        Thread.currentThread().join(1000000000);
        // debug的时候, 下面这行需要有断点
        // 否则主线程结束无法debug
        System.out.println("主线程结束");
    }
}