package org.example;

import org.apache.commons.lang3.ArrayUtils;
import java.util.*;
import java.util.concurrent.*;

public class Processor {

    public  void process(int skierCount, int startThreadCount, int runCount, String baseURL) throws Exception {
        //initialize variables
        BlockingQueue<SkierBean> SkierQueue = new LinkedBlockingDeque<>(skierCount);

        //phase 1
        //produce skier
        new SkierProducer(SkierQueue).run();


        //phase 2
        //run the start threads
        CountDownLatch completed = new CountDownLatch(skierCount);
        Consumer consumer = new Consumer(SkierQueue, baseURL, completed);
        Phase phase = new Phase(consumer, completed, startThreadCount, runCount);
        phase.start();


        //phase 3
        //run more threads to complete POSTs
        Phase phase2 = new Phase(consumer, completed, 200, 840);
        phase2.start();

        phase.waitForPhaseFinish();
        phase2.waitForPhaseFinish();

        //all done
        writeToFile(consumer.getRecord());
        performance(consumer.getRecord());
    }

    private Runnable writeToFile(Queue<String[]> fileData) throws Exception {
        //header
        String[] headerRecord = {"start time", "end time", "request type", "latency", "response code"};

        //remove end time
        fileData.forEach(data -> ArrayUtils.remove(data, 1));

        new CSVWriteHelper("src/main/resources/output.csv")
                .csvWriterAll(fileData.stream().toList(), headerRecord);
        return null;
    }

    private void performance(Queue<String[]> fileData){
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

}
