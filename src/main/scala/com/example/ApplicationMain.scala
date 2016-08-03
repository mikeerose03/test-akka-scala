package com.example

import akka.actor.{ ActorSystem, Actor, Props, ActorRef }

object ApplicationMain extends App {
	val system = ActorSystem("SystemName")
	val server = system.actorOf(Props[ServerActor])
	val client = system.actorOf(classOf(Props[ClientActor], "mikee", server), "client") 

	client ! Messages.ClientMessage("Hello")
}

class ClientActor(username: String, server: ActorRef) extends Actor {
	import Messages._

	Override def preStart(){
		println(s"Initializing $username...")
		server ! Initialize
	}

	def receive = {
		case ClientMessage(msg) => server ! ClientMessage(username +":"+ msg)
		case ServerMessage(msg) => println("server: " + msg)
		case _ => println("No message received.")
	}
}

class ServerActor extends Actor {
	import Messages._

	val users = Set[ActorRef]()

	def receive = {
		case Initialize =>
			users += sender
			println(sender)
			sender ! ServerMessage("Client Registered")
		case ServerMessage(msg) => users.foreach(println(_ ! ServerMessage(msg)))
		case _ => println("No message received.")
	}

}

object Messages {
	object Initialize
	case class ServerMessage(msg: String)
	case class ClientMessage(msg: String)
}