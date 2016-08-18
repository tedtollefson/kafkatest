package com.ilwllc.sgerke.kafkatest;

import java.util.Properties;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class SimpleConsumer {
   public static void main(String[] args) throws Exception {
      if(args.length == 0){
         System.out.println("Enter topic name");
         return;
      }
      String topicName = args[0].toString();

      Properties props = new Properties();
      
      props.put("bootstrap.servers", "localhost:9092");
      props.put("group.id", "test");
      props.put("enable.auto.commit", "true");
      props.put("auto.commit.interval.ms", "1000");
      props.put("session.timeout.ms", "30000");
      props.put("key.deserializer", 
         "org.apache.kafka.common.serialization.StringDeserializer");
      props.put("value.deserializer", 
         "org.apache.kafka.common.serialization.StringDeserializer");

	 KafkaConsumer<String, String> consumer = new KafkaConsumer
         <String, String>(props);
      
      consumer.subscribe(Arrays.asList(topicName));
      
      System.out.println("Subscribed to topic " + topicName);
      
      while (true) {
         ConsumerRecords<String, String> records = consumer.poll(100);
         for (ConsumerRecord<String, String> record : records)
         	{
        	 System.out.printf("offset = %d, key = %s, value = %s\n", 
        			 record.offset(), record.key(), record.value());
//        	 System.out.println("---1---");
        	 try{
        		 Path pt=new Path("hdfs://quickstart.cloudera:8020/user/cloudera/kafkatest.txt");
        		 FileSystem fs = FileSystem.get(new Configuration());
        		 BufferedWriter br=new BufferedWriter(new OutputStreamWriter(fs.append(pt)));
        		 System.out.println(record.value());
        		 br.write(record.value());
        		 br.newLine();
//        		 System.out.println("---2---");
        		 br.close();
        	 }catch(Exception e){
        		 System.out.println("File not found");
//        		 System.out.println("---3---");
        	 	}
         	}
      }
   }
}
