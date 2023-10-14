package com.twitter.storm;


import java.util.*;

import org.apache.storm.tuple.Fields;

import com.twitter.storm.bolts.HashtagCounterBolt;
import com.twitter.storm.bolts.HashtagReaderBolt;
import com.twitter.storm.spouts.TwitterSampleSpout;

import org.apache.storm.Config;
import org.apache.storm.topology.ConfigurableTopology;
import org.apache.storm.topology.TopologyBuilder;

public class TwitterFeedTopology extends ConfigurableTopology{
   public static void main(String[] args) throws Exception{
      ConfigurableTopology.start(new TwitterFeedTopology(), args);
   }

   @Override
   protected int run(String[] args) {
      String consumerKey = args[0];
      String consumerSecret = args[1];
		
      String accessToken = args[2];
      String accessTokenSecret = args[3];
		
      String[] arguments = args.clone();
      String[] keyWords = Arrays.copyOfRange(arguments, 4, arguments.length);
		
      Config config = new Config();
      config.setDebug(true);
		
      TopologyBuilder builder = new TopologyBuilder();
      builder.setSpout("twitter-spout", new TwitterSampleSpout(consumerKey,
         consumerSecret, accessToken, accessTokenSecret, keyWords));

      builder.setBolt("twitter-hashtag-reader-bolt", new HashtagReaderBolt())
         .shuffleGrouping("twitter-spout");

      builder.setBolt("twitter-hashtag-counter-bolt", new HashtagCounterBolt())
         .fieldsGrouping("twitter-hashtag-reader-bolt", new Fields("hashtag"));

      return submit("twitter", config, builder);
   }
}