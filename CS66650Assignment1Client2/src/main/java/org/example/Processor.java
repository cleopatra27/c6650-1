package org.example;

import org.apache.commons.lang3.ArrayUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class Processor {

    public  void process(int skierCount, int startThreadCount, int runCount) throws Exception {
        //initialize variables
        BlockingQueue<SkierBean> SkierQueue = new LinkedBlockingDeque<>(skierCount);

        //phase 1
        //produce skier
        new SkierProducer(SkierQueue).run();


        //phase 2
        //run the start threads
        Consumer consumer = new Consumer(SkierQueue);
        CountDownLatch completed = new CountDownLatch(runCount);
        for (int i = 0; i < startThreadCount; i++) {
            StartThreads t1=new StartThreads(runCount);
            t1.run(consumer);
            completed.countDown();
        }
        completed.await();


        //phase 3
        //run more threads to complete POSTs
        CountDownLatch completedNext = new CountDownLatch((skierCount - SkierQueue.remainingCapacity()));
        while(SkierQueue.remainingCapacity() < skierCount){
            new Thread(() ->
            {
                try {
                    consumer.consume();
                    completedNext.countDown();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
        completedNext.await();

        //all done
       // writeToFile(consumer.getRecord());
        performance(consumer.getRecord());
    }

    private Runnable writeToFile(List<String[]> fileData) throws Exception {
        createDir("src/main/resources/output");
        //header
        String[] headerRecord = {"start time", "request type", "latency", "response code"};

        //remove end time
        fileData.forEach(data -> ArrayUtils.remove(data, 1));

        new CSVWriteHelper("src/main/resources/output.csv")
                .csvWriterAll(fileData, headerRecord);
        return null;
    }

    private void perfodrmance(List<String[]> fileData){

        fileData.stream().forEach(data -> {
            Long.parseLong(data[1]);
        });
    }
    private void performance(List<String[]> fileData){
        System.out.println("************************ PERFORMANCE LOG START *************************");

        if (fileData == null || fileData.isEmpty()) {
            System.out.println("List is null/empty: " + fileData);
            return;
        }

        Long sum  = 0L;
        int size = fileData.size();
        System.out.println("List size: " + size);
            for (String[] arr : fileData) {
                //mean response time (millisecs)
                //end[2].average
                sum += Long.parseLong(arr[1]);
            }

        double average = (1.0 * sum) / size;
        System.out.println("average : " + average);

        //median response time (millisecs)
        //parse to Long
        List<Long> nList = new ArrayList<>();
        for (String[] arr : fileData) {
            //mean response time (millisecs)
            //end[2].average
            nList.add(Long.parseLong(arr[1]));
        }

        Double median = getMedian(size, nList);
        System.out.println("Median: " + median);

        //throughput = total number of requests/wall time (requests/second) - size / all(filedata[0] +filedata[1])
        long wallTimeSum = 0;
        for (String[] arr : fileData) {
            //mean response time (millisecs)
            //end[2].average
            wallTimeSum += Long.parseLong(arr[0]) + Long.parseLong(arr[1]);
        }
        double throughput = (1.0 * size) / wallTimeSum;
        System.out.println("throughput: " + throughput);



        //p99 (99th percentile) response time. -- (n/N) × 100

        //min and max response time (millisecs)
        Long min = 0L;
        Long max = 0L;
        for (String[] arr : fileData) {
            //mean response time (millisecs)
            //end[2].average
            long n = Long.parseLong(arr[1]);
            if(min == 0 || n < min){
                min = n;
            }
            if(max == 0 || n > max){
                max = n;
            }
        }
        System.out.println("Min : " + min);
        System.out.println("Max : " + max);

        System.out.println("************************ PERFORMANCE LOG END *************************");
    }

//    public static void main(String[] args) {
//        List<Long> nList = Arrays.asList(new Long[]{5L, 9L, 11L, 9L, 7L});
//
//        System.out.println(getMedian(nList.size(), nList));
//        nList = Arrays.asList(new Long[]{ 2L, 5L, 1L, 4L, 2l, 7L});
//        System.out.println(getMedian(nList.size(), nList));
//    }
    private static Double getMedian(int size, List<Long> nList) {
        //order the list
        Collections.sort(nList);

        //find med index
        Double median;
        if(size % 2 == 1){
            //odd 5
            // 5 / 2 = 2
            //result = 2 + 1 = 3 rd element
            //in 0 based index it's 2
            int mid = (size / 2) ;
            median = Double.valueOf(nList.get(mid));
        }
        else{
            //even 6
            // 6 /2 = 3
            int mid = (size / 2) ;
            //0 1 2 3 4 5
            //1 2 3 4 5 6
            //result (average of mid + (mid + 1))
            // average of 3 + 4
            //in 0 based average of 2 + 3 (so mid + (mid -1 ))
            median = (nList.get(mid) + nList.get(mid-1))/2.0;
        }
        return median;
    }

    private void createDir(String newDirPath) {
        File dir = new File(newDirPath);
        if(dir.mkdir()) {
            System.out.println("Directory created");
        }
        else {
            System.out.println("Already exists. Directory not created");
        }
    }
}
