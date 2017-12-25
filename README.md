# akka-echo-server
As title. For now, it provided a runnable code for Akka offical doc: https://doc.akka.io/docs/akka/current/io-tcp.html

# How to start the server and client
* Start Server.ServerApp
* Start Client.DriveClient

## Akka io
* akka io的实现就20多个类：https://github.com/akka/akka/tree/master/akka-actor/src/main/scala/akka/io
* TcpConnection包装了Java NIO的Channel.
* DirectBufferPool和ThreadPool的概念类似。如果有可用Buffer,从pool里拿，如果没有就创建一个。
* 经验: 看代码一定要带着目的去看，没有目的也要找一个目的，去看。看代码不是看散文。有目的你才能，有的放矢，舍弃不关心的，才有效果。不带目的，代码本身就繁杂，肯定耗费精力，也看不出什么结果来。目的一般是从使用者角度来的，这个库提供了什么功能，如何使用这个功能。只有你知道如何使用这个功能，然后再去对应地看某个功能在底层是如何实现的，才有效果。
* TCP.scala中有两种消息： Command和Event. Command一般就是应用程序发起的，Event一般就是操作系统触发的。 Command的例子有, Abort, Bind, Connect, Write; Event的例子有Aborted, Bound, Connected, Receivied，一个有意思的地方是，Event都是完成时态，有完成的意思；而Command都是一般现在时态，有祈使的意味。这也算是好程序的一个特点吧。

## Push-reading 和 Pull-Reading
* akka官方文档：https://doc.akka.io/docs/akka/current/io-tcp.html 提出了两种模式Pull Reading 和 Push Reading.
* Push Readinng: 数据到了Connection Actor, Connection Actor就会将数据发送给注册的Actor；注册的Actor，觉得有压力，可以发送SuspendReading给Connection Actor, Conncetion Actor就不再发送，知道注册的Actor又发了一个ResumeReading消息给Connection Actor.


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
