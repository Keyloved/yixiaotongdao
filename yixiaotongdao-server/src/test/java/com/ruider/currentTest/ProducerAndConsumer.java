package com.ruider.currentTest;

public class ProducerAndConsumer {

    private static Resource resource = new Resource();
    public static void main (String[] args) throws InterruptedException{
        ProducerAndConsumer producerAndConsumer = new ProducerAndConsumer();
        Consumer consumer1 = new Consumer(producerAndConsumer.resource);
        Producer producer1 = new Producer(producerAndConsumer.resource);

        Thread consumer = new Thread(consumer1,"consumer");
        Thread producer = new Thread(producer1,"producer");

        producer.start();
        consumer.start();

        //主线程等待1秒，使生产者和消费者两个线程执行
        Thread.sleep(1000);

    }

    //资源类
    public static class Resource {

        //先生产后消费
        private volatile boolean flag = false;

        public boolean getFlag() {
            return flag;
        }

        public void  setFlag(boolean flag) {
            this.flag = flag;
        }
    }


    //消费者
    public static class Consumer implements Runnable {

        private Resource resource;
        public Consumer(Resource resource) {
            this.resource = resource;
        }
        @Override
        public void run () {
                synchronized (resource) {
                    while(true) {
                        try{
                            if(!resource.getFlag()) {
                                resource.wait();
                            }
                            System.out.println("消费者---------消费");
                            Thread.sleep(1000);
                            resource.setFlag(false);
                            resource.notify();
                        }catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
            }
        }
    }

    //生产者
    public static class Producer implements Runnable {

        private Resource resource;
        public Producer(Resource resource) {
            this.resource = resource;
        }
        @Override
        public void run () {
                synchronized (resource) {
                    //获取到锁之后循环获取资源
                    while(true) {
                        try{
                            if(resource.getFlag()) {
                                resource.wait();
                            }
                            System.out.println("生产者---------生产");
                            Thread.sleep(1000);
                            resource.setFlag(true);
                            resource.notify();
                        }catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                }
            }

        }
    }


}
