package com.twitter.storm.spouts;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import twitter4j.v1.FilterQuery;
import twitter4j.v1.RawStreamListener;
import twitter4j.v1.Status;
import twitter4j.v1.StatusDeletionNotice;
import twitter4j.v1.StatusListener;
import twitter4j.Twitter;
import twitter4j.v1.TwitterStream;

import org.apache.storm.Config;
import org.apache.storm.spout.SpoutOutputCollector;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import org.apache.storm.utils.Utils;

public class TwitterSampleSpout extends BaseRichSpout {
    SpoutOutputCollector _collector;
    LinkedBlockingQueue<Status> queue = null;
    TwitterStream _twitterStream;
            
    String consumerKey;
    String consumerSecret;
    String accessToken;
    String accessTokenSecret;
    String[] keyWords;
            
    public TwitterSampleSpout(String consumerKey, String consumerSecret,
        String accessToken, String accessTokenSecret, String[] keyWords) {
            this.consumerKey = consumerKey;
            this.consumerSecret = consumerSecret;
            this.accessToken = accessToken;
            this.accessTokenSecret = accessTokenSecret;
            this.keyWords = keyWords;
    }
            
    public TwitterSampleSpout() {
        // TODO Auto-generated constructor stub
    }
            
    @Override
    public void open(Map conf, TopologyContext context,
        SpoutOutputCollector collector) {
            queue = new LinkedBlockingQueue<Status>(1000);
            _collector = collector;
            _twitterStream = Twitter.newBuilder().listener(new RawStreamListener() {
                @Override
                public void onMessage(String rawJson) {
                    System.out.println(rawJson);
                }

                @Override
                public void onException(Exception ex) {
                    ex.printStackTrace();
                }
            })
            .oAuthConsumer(consumerKey, consumerSecret)
            .oAuthAccessToken(accessToken, accessTokenSecret)
            .build().v1().stream().sample();
                    
            if (keyWords.length == 0) {
                _twitterStream.sample();
            }else {
                _twitterStream.filter(FilterQuery.ofTrack(keyWords));
            }
    }
                
    @Override
    public void nextTuple() {
        Status ret = queue.poll();
                    
        if (ret == null) {
            Utils.sleep(50);
        } else {
            _collector.emit(new Values(ret));
        }
    }
                
    @Override
    public void close() {
        _twitterStream.shutdown();
    }
                
    @Override
    public Map<String, Object> getComponentConfiguration() {
        Config ret = new Config();
        ret.setMaxTaskParallelism(1);
        return ret;
    }
                
    @Override
    public void ack(Object id) {}
                
    @Override
    public void fail(Object id) {}
                
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("tweet"));
    }
}
