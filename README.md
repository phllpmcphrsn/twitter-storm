# twitter-storm
## Stand up a minimal cluster
Run `docker compose up -d` in the root directory

## Submit Topology to cluster
docker cp storm.yaml c51e91b7173e:/apache-storm-2.5.0/conf/storm.yaml
docker exec -it some-nimbus storm jar /apache-storm-2.5.0/topology.jar org.apache.storm.starter.ExclamationTopology






docker run --network storm-network --link nimbus:nimbus -it --rm -v $(pwd)/topology.jar:/twitter-storm-1.0-SNAPSHOT.jar storm storm jar /topology.jar com.apache.twitter.storm.TwitterFeedTopology topology

