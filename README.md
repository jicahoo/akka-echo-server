# akka-echo-server
As title. For now, it provided a runnable code for Akka offical doc: https://doc.akka.io/docs/akka/current/io-tcp.html

# How to start the server and client
* Start Server.ServerApp
* Start Client.DriveClient

## Akka io
* akka io的实现也就20多个类：https://github.com/akka/akka/tree/master/akka-actor/src/main/scala/akka/io
* TcpConnection包装了Java NIO的Channel.
* DirectBufferPool和ThreadPool的概念类似。如果有可用Buffer,从pool里拿，如果没有就创建一个。
* 经验: 看代码一定要带着目的去看，没有目的也要找一个目的，去看。看代码不是看散文。有目的你才能有的放矢。不带目的，代码本身就繁杂，肯定耗费精力，也看不出什么结果来。目的一般是从使用者角度来的，这个库提供了什么功能，如何使用这个功能。只有你知道如何使用这个功能，然后再去对应地看某个功能在底层是如何实现的，才有效果。 如果没有具体问题，也要顺着某个脑海中的用例去看相关的代码，而不要被代码干扰，到处毫无目的的看。
* TCP.scala中有两种消息： Command和Event. Command一般就是应用程序发起的，Event一般就是操作系统触发的。 Command的例子有, Abort, Bind, Connect, Write; Event的例子有Aborted, Bound, Connected, Receivied，一个有意思的地方是，Event都是完成时态，有"完成"的意思；而Command都是一般现在时态，有祈使的意味。这也算是好程序的一个特点吧--用词准确。
* 一些表述都是站在server这一角度。

## 如何处理读的压力？Push-reading 和 Pull-Reading
* 压力在哪？ 压力在于可读的数据来的快，但逻辑处理的慢。这是过快的输入数据对处理逻辑造成的压力，如果处理逻辑不能及时处理，可能就是放在内存的buffer中，一直不能及时处理，buffer会越来越大，最终导致内存不够用，应用挂掉。
* akka官方文档：https://doc.akka.io/docs/akka/current/io-tcp.html 提出了两种模式Pull Reading 和 Push Reading.
* Push Readinng: Connection Actor很积极，有数据到了，立马发送给已注册的Actor。“不用你说，我就给你发数据；你说停我就停，你说开始我再开始”。数据到了Connection Actor, Connection Actor就会将数据发送给注册的Actor；注册的Actor，觉得有压力，可以发送SuspendReading给Connection Actor, Conncetion Actor就不再发送，知道注册的Actor又发了一个ResumeReading消息给Connection Actor。不用催Connection Actor, 默认Connection Actor会把数据“推送”给你。
* Pull Reading: Connetion Actor很懒，注册的Actor不说要数据，它就不发送。“要数据的时候，给我说声，我给你。”。 所以说，这个模式是通过给Connection Actor发送ResumeReading消息把数据"拉“过来的。

## 如何处理写的压力？ACK-based, NACK-based, NACK-based with write suspending.
* 压力在哪？ 
* ACK-based: 一个一个写，（有些像TCP的一些行为，滑动窗口什么的。）只有当前的写操作成功了，才会接受下一个写请求。WriteCommand可以带一个Object (不能是Tcp.NoAck)，当写操作成功了，就会将WriteCommand带的这个Object返回给WriteCommand的发送者。(还是要加强阅读理解的能力，无论是中文还是英文，我现在的理解方式都毫无章法，也无耐心，随便看看就自以为理解了，还是要有章法地尝试理解作者的意图，上下文是什么，语法结构是怎样的，代词指的是谁，一些判断的前提条件是什么。原文中的这一句话`every Write command carries an arbitrary object, and if this object is not Tcp.NoAck then it will be returned to the sender of the Write upon successfully writing all contained data to the socket.`虽然复杂，但是确实比较清楚完整地描述了程序行为。 `it`指代的就是WriteCommand携带的Object；这个Object， 当(upon)成功将所有的数据写到Socket的时候，会被返回给Write的发送者(sender))。
* NACK-based: 写操作不需要ACK, 尽管发送写操作给Connection Actor，出错的时候会Connection Actor告诉发送者写失败了。由发送者处理失败的写，失败的写消息会包含要写的数据。失败的时候，Connection Actor会继续处理其他写请求。
* NACK-based with write suspending: 某个写操作未能成功完成的时候，Connection Actor会对之后的写请求，返回错误消息？ 当Connection Actor收到ResumeWriting消息的时候，如果最后接受的写请求成功了，ConnectionActor才会返回WritingResumed。那么，Connection Actor才会接受新的写请求。和NACK-based的区别，错误的时候是否暂停处理新的写请求。

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
