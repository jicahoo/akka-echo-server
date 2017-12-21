package com.hello

import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.io.Tcp.Connected
import akka.io.{IO, Tcp}
import akka.util.ByteString

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object DriveClient extends App {
  println("OK")
  val socket = new InetSocketAddress("127.0.0.1", 8000)
  val system = ActorSystem("ClientSystem")
  val listener = system.actorOf(Props[ResponseListener], "RespListener")
  val helloActor = system.actorOf(Client.props(socket, listener), name = "clientactor")
  Await.ready(system.whenTerminated, Duration(1, TimeUnit.MINUTES))

}

class ResponseListener extends Actor {
  override def receive: PartialFunction[Any, Unit] = {
    case s: String => println(s)
    case Connected(remote, local) => {
      println(s"Client Connected. Remote: $remote, Local: $local.")
      val client = sender()
      client ! ByteString.fromString("Hello")
    }
    case data: ByteString => println(s"Got data: ${data.map(_.toChar).mkString}")
    case _ => println("What happened??")
  }
}

object Client {
  def props(remote: InetSocketAddress, replies: ActorRef) =
    Props(classOf[Client], remote, replies)
}

class Client(remote: InetSocketAddress, listener: ActorRef) extends Actor {

  import akka.io.Tcp._
  import context.system

  IO(Tcp) ! Connect(remote)

  def receive: PartialFunction[Any, Unit] = {
    case CommandFailed(_: Connect) ⇒
      listener ! "connect failed"
      context stop self

    case conn@Connected(`remote`, _) ⇒
      listener ! conn
      val connection = sender()
      connection ! Register(self)
      context become {
        case data: ByteString ⇒
          connection ! Write(data)
        case CommandFailed(_: Write) ⇒
          // O/S buffer was full
          listener ! "write failed"
        case Received(data) ⇒
          listener ! data
        case "close" ⇒
          connection ! Close
        case _: ConnectionClosed ⇒
          listener ! "connection closed"
          context stop self
      }
  }
}