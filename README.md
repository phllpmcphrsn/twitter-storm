# twitter-storm
## Stand up a minimal cluster
Run `docker compose up -d` in the root directory. As part of the composition, the Apache Storm UI is deployed, and can be reached at `http://localhost:8080`

## Storm config
Not sure which storm config the container looks at, but there's two:
1. /conf/storm.yaml
2. /apache-storm-<version>/conf/storm.yaml

Here's what one (or, both) should look like
```yaml
storm.zookeeper.servers: [zookeeper]
nimbus.seeds: [nimbus]
storm.log.dir: "/logs"
storm.local.dir: "/data"
```

## Submit Topology to cluster
Copy over the shaded (uber) jar - in this case it's default jar, project name - to the container's working dir:
`docker cp target/twitter-storm-<version>.jar nimbus:/apache-storm-2.5.0`

Now, you should be able to submit the topology onto the cluster. Here we're executing the storm command from within the container on the jar that was copied over:
`docker exec nimbus storm jar twitter-storm-1.0-SNAPSHOT.jar com.twitter.storm.TwitterFeedTopology <api-key> <api-secret> <access-token> <access-token-secret>`

Here's what successful logs look like:
```bash
02:09:24.855 [main] INFO  o.a.s.StormSubmitter - Generated ZooKeeper secret payload for MD5-digest: -6611850448928672997:-8503772318766663785
02:09:24.914 [main] INFO  o.a.s.u.NimbusClient - Found leader nimbus : 7a48b9618cae:6627
02:09:24.916 [main] INFO  o.a.s.s.a.ClientAuthUtils - Got AutoCreds []
02:09:24.929 [main] INFO  o.a.s.StormSubmitter - Uploading dependencies - jars...
02:09:24.929 [main] INFO  o.a.s.StormSubmitter - Uploading dependencies - artifacts...
02:09:24.930 [main] INFO  o.a.s.StormSubmitter - Dependency Blob keys - jars : [] / artifacts : []
02:09:24.931 [main] INFO  o.a.s.StormSubmitter - Uploading topology jar twitter-storm-1.0-SNAPSHOT.jar to assigned location: /data/nimbus/inbox/stormjar-54450e39-94e2-4995-b1b2-4bc813b42484.jar
02:09:24.941 [main] INFO  o.a.s.StormSubmitter - Successfully uploaded topology jar to assigned location: /data/nimbus/inbox/stormjar-54450e39-94e2-4995-b1b2-4bc813b42484.jar
02:09:24.941 [main] INFO  o.a.s.StormSubmitter - Submitting topology twitter in distributed mode with conf {"storm.zookeeper.topology.auth.scheme":"digest","storm.zookeeper.topology.auth.payload":"-6611850448928672997:-8503772318766663785","topology.debug":true}
02:09:25.038 [main] INFO  o.a.s.StormSubmitter - Finished submitting topology: twitter
```

Notice that the topology name is "twitter". That's hardcoded so I'll need to change that

## Kill a topology
```bash
$ docker exec nimbus storm kill twitter
Running: /opt/java/openjdk/bin/java -client -Ddaemon.name= -Dstorm.options= -Dstorm.home=/apache-storm-2.5.0 -Dstorm.log.dir=/logs -Djava.library.path=/usr/local/lib:/opt/local/lib:/usr/lib:/usr/lib64 -Dstorm.conf.file= -cp /apache-storm-2.5.0/*:/apache-storm-2.5.0/lib/*:/apache-storm-2.5.0/extlib/*:/apache-storm-2.5.0/extlib-daemon/*:/conf:/apache-storm-2.5.0/bin org.apache.storm.command.KillTopology twitter
02:48:17.746 [main] INFO  o.a.s.v.ConfigValidation - Will use [class org.apache.storm.DaemonConfig, class org.apache.storm.Config] for validation
02:48:17.820 [main] INFO  o.a.s.u.NimbusClient - Found leader nimbus : 7a48b9618cae:6627
02:48:17.833 [main] INFO  o.a.s.c.KillTopology - Killed topology: twitter
```

