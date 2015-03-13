# ProcMetrics

The first motivation was: learning Functional Programming style in Scala with a usefull project. ProcMetrics key points:

* collect performance data from the /proc filesystem
* easy extendable
* perform calculations, for example IOPS
* collect the qemu main stats: network usage, CPU seconds, disk usage
* send collected data to a CouchDB
* send collected data to OpenTSDB
* could be started as daemon with the Akka micro kernel

The project is in a early state and is currently not useable.

# Build ProcMetrics

1. git clone ...
2. cd ProcMetrics
3. sbt compile or sbt run

# Build a Jar

* sbt assembly

produce a jar in the target directory path

# Working with Eclipse

1. sbt eclipse
2. In Eclipse: import existing project 

# Current State

Two main object are available:

1. at.linuxhacker.procmetrics.bin.ProcMetricsMain
2. at.linuxhacker.procmetrics.bin.ProcMetricsMonitor

Start the program with 

```
$ sbt run
Multiple main classes detected, select one to run:

 [1] at.linuxhacker.procmetrics.bin.ProcMetricsMain
 [2] at.linuxhacker.procmetrics.bin.ProcMetricsMonitor

Enter number: 
```

or build a jar file and start it with the java command:

```
$ sbt assembly

...

[info] Packaging /.../ProcMetrics/target/scala-2.11/ProcMetrics-assembly-0.1.jar ...
[info] Done packaging.

$ java -cp  target/scala-2.11/ProcMetrics-assembly-0.1.jar \
   at.linuxhacker.procmetrics.bin.ProcMetricsMain
```

Tip: Download [jq](http://stedolan.github.io/jq/) and use it with the JSON output:

```
$java -cp  target/scala-2.11/ProcMetrics-assembly-0.1.jar \
  at.linuxhacker.procmetrics.bin.ProcMetricsMain | jq "."

...

     },
      "statm": {
        "size": 29011,
        "resident": 947,
        "share": 427,
        "data": 548
      },
      "status": {
        "thread_count": 1,
        "VmPeak": 118829056,
        "VmSize": 118829056,
        "VmLck": 0,
        "VmPin": 0,
        "VmHWM": 3878912,
        "VmRSS": 3878912,
        "VmData": 2105344,
        "VmStk": 139264,
        "VmExe": 892928,
        "VmLib": 2097152,
        "VmPTE": 73728,
        "VmSwap": 0
      },
      "stat": {
        "cpu_user_sec": 11,
        "cpu_sys_sec": 3,
        "cpu_sum_sec": 14
      }
    }
  },
  "multi": [
    {
      "NetDev": {
        "lo": {
          "recv_bytes": 88990032,
          "trans_bytes": 88990032
        },
        "em1": {
          "recv_bytes": 3225896704,
          "trans_bytes": 824780928
        }
      }
    }
  ],
  "sysfs": {
    "lo": {
      "mac": "00:00:00:00:00:00"
    },
    "em1": {
      "mac": "24:be:05:0f:57:e7"
    }
  }
}
```

# License

GNU GPLv3
