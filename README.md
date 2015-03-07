= ProcMetrics =

The first motivation was: learning Functional Programming style in Scala with a usefull project. ProcMetrics key points:

* collect performance data from the /proc filesystem
* easy extendable
* perform calculations, for example IOPS
* collect the qemu main stats: network usage, CPU seconds, disk usage
* send collected data to a CouchDB
* send collected data to OpenTSDB
* could be started as daemon with the Akka micro kernel

The project is in a early state and is currently not useable.

= Build ProcMetrics =

1. git clone ...
2. cd ProcMetrics
3. sbt compile or sbt run

== Build a Jar ==

* sbt assembly

produce a jar in the target directory path

= Working with Eclipse =

1. sbt eclipse
2. In Eclipse: import existing project 

= Current State =

Two main object are available:

1. at.linuxhacker.procmetrics.bin.ProcMetricsMain
2. at.linuxhacker.procmetrics.bin.ProcMetricsMonitor

Start the program with 
```sbt run
Multiple main classes detected, select one to run:

 [1] at.linuxhacker.procmetrics.bin.ProcMetricsMain
 [2] at.linuxhacker.procmetrics.bin.ProcMetricsMonitor

Enter number: 
```

or build a jar file and start it with the java command:
``` sbt assembly

...

[info] Packaging /.../ProcMetrics/target/scala-2.11/ProcMetrics-assembly-0.1.jar ...
[info] Done packaging.

$ java -cp  target/scala-2.11/ProcMetrics-assembly-0.1.jar \
   at.linuxhacker.procmetrics.bin.ProcMetricsMain
```

Tip: Download [jq](http://stedolan.github.io/jq/) and use it with the JSON output:
``` $java -cp  target/scala-2.11/ProcMetrics-assembly-0.1.jar \
  at.linuxhacker.procmetrics.bin.ProcMetricsMain | jq "."

...

  "multi": [
    {
      "NetDev": [
        {
          "tun0": [
            {
              "recv_bytes": 773606656
            },
            {
              "trans_bytes": 38190596
            }
          ]
        },
        {
          "br0": [
            {
              "recv_bytes": 1023074816
            },
            {
              "trans_bytes": 117986240
            }
          ]
        },
        {
          "p10p1": [
            {
              "recv_bytes": 1042125184
            },
            {
              "trans_bytes": 117948832
            }
          ]
        },

...
```

= License =

GNU GPLv3



