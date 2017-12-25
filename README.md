# akka-echo-server
As title. For now, it provided a runnable code for Akka offical doc: https://doc.akka.io/docs/akka/current/io-tcp.html

# How to start the server and client
* Start Server.ServerApp
* Start Client.DriveClient

## Akka io
* akka io的实现就20多个类：https://github.com/akka/akka/tree/master/akka-actor/src/main/scala/akka/io
* TcpConnection包装了Java NIO的Channel.
* DirectBufferPool和ThreadPool的概念类似。如果有可用Buffer,从pool里拿，如果没有就创建一个。
* 经验: 看代码一定要带着目的去看，没有目的也要找一个目的，去看。看代码不是看散文。有目的你才能，有的放矢，舍弃不关心的，才有效果。不带目的，耗费精力，也看不出什么结果来。目的一般是从使用者角度来的，这个库提供了什么功能，如何使用这个功能。你知道如何使用这个功能，然后，再去对应地看，某个功能呢在底层是如何实现的。

## Architecture
  ![screenshot](https://www.lucidchart.com/publicSegments/view/39b0a770-82cd-4078-b1ff-6ab6ee63ffc8/image.png "Logo Title Text 1")
  ![Hee](http://on-img.com/chart_image/5a3c6fe7e4b0ce9ffea59979.png "Ni")
* 做图软件：
  * https://www.processon.com/
  * https://www.lucidchart.com/
## References
* https://gist.github.com/masahitojp/4373489
* https://doc.akka.io/docs/akka/2.5/io-tcp.html?language=scala
* https://doc.yonyoucloud.com/doc/akka-doc-cn/2.3.6/scala/book/chapter5/06_using_tcp.html
* http://blog.csdn.net/zxhoo/article/details/39996205
* https://github.com/zxh0/myblog/tree/master/code/akka-examples/echo-server-java8
