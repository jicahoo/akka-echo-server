package com.hello

import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorSystem, Props}
import akka.io.{IO, Tcp}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object ServerApp extends App {
  val system = ActorSystem("ServerSystem")
  val echo = system.actorOf(Props[Server], name = "echo_server")
  Await.ready(system.whenTerminated, Duration(1, TimeUnit.MINUTES))
}

class Server extends Actor {

  import akka.io.Tcp._
  import context.system

  println("Before IO(Tcp)")
  val tcpManager = IO(Tcp)
  tcpManager ! Bind(self, new InetSocketAddress("localhost", 8000))
  println(s"what object is a ${tcpManager.getClass}")
  println("Done")

  def receive: Actor.Receive = {
    case b@Bound(localAddress) ⇒
      context.parent ! b

    case CommandFailed(_: Bind) ⇒ context stop self

    case conn@Connected(remote, local) ⇒
      println(s"Server Connected. Remote: $remote, Local: $local.")
      val handler = context.actorOf(Props[SimplisticHandler])
      val connection = sender()
      connection ! Register(handler)
  }

}

class SimplisticHandler extends Actor {

  import Tcp._

  def receive:Actor.Receive = {
    case Received(data) ⇒
      println(s"Got data. Data type: ${data.getClass}")
      println(data.map(b => b.toChar).mkString)
      sender() ! Write(data)
    case  PeerClosed ⇒ context stop self
  }
}